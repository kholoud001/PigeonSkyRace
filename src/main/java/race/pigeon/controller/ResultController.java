package race.pigeon.controller;


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
import race.pigeon.model.entity.Result;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.ResultService;
import race.pigeon.util.CsvParserUtil;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private AppUserRepository appUserRepository;

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
                                   RedirectAttributes redirectAttributes) {

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
    }


