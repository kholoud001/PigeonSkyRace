package race.pigeon.controller;


import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;
import race.pigeon.model.entity.appUser;
import race.pigeon.model.enums.Role;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.impl.CompetitionServiceImpl;
import race.pigeon.service.impl.PigeonServiceImpl;
import race.pigeon.service.impl.ResultServiceImpl;
import race.pigeon.service.impl.UserServiceImpl;
import race.pigeon.util.ExportResults;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultServiceImpl resultService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CompetitionServiceImpl competitionService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PigeonServiceImpl pigeonService;


    private final Path rootLocation;


    private static final Logger logger = LoggerFactory.getLogger(ResultController.class);

    public ResultController(Path rootLocation) {
        this.rootLocation = rootLocation;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("competitionId") String competitionId) {

        String contentType = file.getContentType();
        logger.info("File Content Type: {}", contentType);
        logger.info("File Name: {}", file.getOriginalFilename());

        // Check if the file content type is correct
        if (contentType == null || (!contentType.equals("text/csv") && !contentType.equals("application/vnd.ms-excel"))) {
            return new ResponseEntity<>("Format de fichier non accepté, seul le format CSV est autorisé.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Ensure that the file is not empty
            if (file.isEmpty()) {
                return new ResponseEntity<>("Aucun fichier n'a été téléchargé.", HttpStatus.BAD_REQUEST);
            }

            // Process the CSV file
            resultService.uploadData(file, competitionId);
            return new ResponseEntity<>("Données téléchargées et sauvegardées avec succès !", HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error processing file upload: {}", e.getMessage());
            return new ResponseEntity<>("Erreur lors du téléchargement des données : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/do")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        resultService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }


    @GetMapping("/all")
    public ResponseEntity<List<Result>> getAllResultats() {
        List<Result> resultats = resultService.getAllResultats();
        return new ResponseEntity<>(resultats, HttpStatus.OK);
    }


    /**
     * Endpoint pour calculer et sauvegarder la distance entre un point de départ (compétition)
     * et le loft de l'utilisateur actuellement connecté.
     *
     * @param competitionId ID de la compétition
     * @param pigeonId      ID du pigeon pour lequel la distance est calculée
     * @return Le résultat contenant la distance calculée
     */
    @PostMapping("/calculate-distance")
    public ResponseEntity<Result> calculateAndSaveDistance(
            @RequestParam String competitionId,
            @RequestParam String pigeonId) {

        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        appUser user = userService.findByUsername(currentUsername);

        // Check if the user exists and has an authorized role
        if (user == null || user.getRole() != Role.Breeder) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Retrieve the competition and pigeon using the services
        Competition competition = competitionService.findById(competitionId);
        Pigeon pigeon = pigeonService.findById(pigeonId);

        // Calculate and save the distance in the result
        Result result = resultService.calculateAndUpdateDistance(competition, user, pigeon);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/calculate-flight-time")
    public ResponseEntity<String> calculateFlightTime(
            @RequestParam String competitionId,
            @RequestParam String pigeonId) {

        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        appUser user = userService.findByUsername(currentUsername);

        // Check if the user exists and has the authorized role
        if (user == null || user.getRole() != Role.Breeder) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Ensure the pigeonId does not have extra newline characters
        pigeonId = pigeonId.trim();

        // Find the Competition and Pigeon entities by their IDs
        Competition competition = competitionService.findById(competitionId);
        Pigeon pigeon = pigeonService.findById(pigeonId);

        // If the competition or pigeon is not found, return a 404 response
        if (competition == null || pigeon == null) {
            return ResponseEntity.status(404).body("Competition or Pigeon not found.");
        }

        // Find the result by competition and pigeon
        Optional<Result> resultOptional = resultService.findByCompetitionAndPigeon(competition, pigeon);
        if (resultOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Result not found for the given competition and pigeon.");
        }

        Result result = resultOptional.get();

        // Calculate the flight time
        String flightTime = resultService.calculateFlightTime(result);

        return ResponseEntity.ok(flightTime);
    }


    @GetMapping("/calculate-vitesse")
    public ResponseEntity<String> calculateVitesse(
            @RequestParam String competitionId,
            @RequestParam String pigeonId) {

        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        appUser user = userService.findByUsername(currentUsername);

        // Check if the user exists and has the authorized role
        if (user == null || user.getRole() != Role.Breeder) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Ensure the pigeonId does not have extra newline characters
        pigeonId = pigeonId.trim();

        // Find the Competition and Pigeon entities by their IDs
        Competition competition = competitionService.findById(competitionId);
        Pigeon pigeon = pigeonService.findById(pigeonId);

        // If the competition or pigeon is not found, return a 404 response
        if (competition == null || pigeon == null) {
            return ResponseEntity.status(404).body("Competition or Pigeon not found.");
        }

        // Find the result by competition and pigeon
        Optional<Result> resultOptional = resultService.findByCompetitionAndPigeon(competition, pigeon);
        if (resultOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Result not found for the given competition and pigeon.");
        }

        Result result = resultOptional.get();

        // Calculate and update the vitesse (speed) for the pigeon
        String resultMessage = resultService.calculateVitesse(result);

        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping("/competitionR/{competitionId}")
    public List<Result> getResultsByCompetition(@PathVariable String competitionId) {
        Competition competition = competitionService.findById(competitionId);
        return resultService.getResultsByRank(competition);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<Void> exportResultsToPdf(@RequestParam String competitionId, HttpServletResponse response) {
        // Fetch competition details
        Competition competition = competitionService.findById(competitionId);

        // Fetch results by competition
        List<Result> results = resultService.getResultsByCompetition(competitionId);

        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Create PDF export utility
        ExportResults exportResults = new ExportResults(results, competition);

        try {
            // Set the response type to PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=results.pdf");

            // Generate PDF and write to the response output stream
            exportResults.export(response);

            return ResponseEntity.ok().build();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}


