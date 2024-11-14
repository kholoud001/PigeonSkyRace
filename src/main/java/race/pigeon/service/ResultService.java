package race.pigeon.service;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;
import race.pigeon.model.entity.appUser;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.repository.PigeonRepository;
import race.pigeon.repository.ResultRepository;
import race.pigeon.util.GeoUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultatRepository;

    @Autowired
    private PigeonRepository pigeonRepository;

    @Autowired
    private CompetitionRepository competitionRepository;




    public void uploadData(MultipartFile file, String competitionId) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // Create a CSVReader to read the content of the CSV file
            CSVReader csvReader = new CSVReader(reader);

            // Read all rows from the CSV file
            List<String[]> rows = csvReader.readAll();

            // Fetch the competition from the database by its ID
            Competition competition = competitionRepository.findById(competitionId);

            if (competition == null) {
                // Handle case where the competition is not found
                System.err.println("Competition not found with ID: " + competitionId);
                return; // Skip processing further if competition is not found
            }

            // Process each row in the CSV file
            for (String[] row : rows) {
                // Parse each row into the corresponding fields
                String heureArriveeString = row[0]; // Arrival Time
                String distanceString = row[1]; // Distance
                String vitesseString = row[2]; // Speed
                String pointString = row[3]; // Points
                String ringNumber = row[4]; // Ring Number (used to find Pigeon)

                // Parse the values
                double distance = 0, vitesse = 0, point = 0;
                try {
                    distance = Double.parseDouble(distanceString);
                    vitesse = Double.parseDouble(vitesseString);
                    point = Double.parseDouble(pointString);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in row for ring number " + ringNumber);
                    continue; // Skip this row if parsing fails
                }

                // Parse the arrival time (assuming it's in the format 'yyyy-MM-dd HH:mm:ss')
                LocalDateTime heureArrivee;
                try {
                    heureArrivee = LocalDateTime.parse(heureArriveeString, formatter);
                } catch (Exception e) {
                    System.err.println("Invalid date format in row for ring number " + ringNumber);
                    continue; // Skip this row if parsing fails
                }

                // Fetch the pigeon from the database using the ring number
                Pigeon pigeon = pigeonRepository.findByRingNumber(ringNumber).orElseThrow(() ->
                        new Exception("Pigeon not found with ring number: " + ringNumber));

                // Create and populate the Result entity
                Result result = new Result();
                result.setHeureArrivee(heureArrivee); // Set heureArrivee as LocalDateTime
                result.setDistance(distance);
                result.setVitesse(vitesse);
                result.setPoint(point);
                result.setPigeon(pigeon);
                result.setCompetition(competition);

                // Save the result to the database
                resultatRepository.save(result);
            }
        } catch (Exception e) {
            // Handle any exceptions (e.g., file reading issues, database errors)
            throw new Exception("Error processing the CSV file: " + e.getMessage(), e);
        }
    }



    public Result calculateAndUpdateDistance(Competition competition, appUser user, Pigeon pigeon) {
        Optional<Result> existingResult = resultatRepository.findByCompetitionAndPigeon(competition, pigeon);

        double distance = GeoUtils.calculateDistance(
                competition.getLatitude(), competition.getLongitude(),
                user.getLatitude(), user.getLongitude()
        );

        Result result;
        if (existingResult.isPresent()) {
            result = existingResult.get();
            result.setDistance(distance);
            result.setHeureArrivee(LocalDateTime.now());
        } else {
            result = new Result();
            result.setCompetition(competition);
            result.setPigeon(pigeon);
            result.setDistance(distance);
            result.setHeureArrivee(LocalDateTime.now());
        }

        return resultatRepository.save(result);
    }



    public String calculateFlightTime(Result result) {
        Competition competition = result.getCompetition();
        LocalDateTime departureTime = competition.getDepartureTime();
        LocalDateTime arrivalTime = result.getHeureArrivee();

        if (departureTime == null || arrivalTime == null) {
            return "Departure or arrival time is not available.";
        }

        Duration duration = Duration.between(departureTime, arrivalTime);

        // Format hours, minutes, and seconds
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }





    private final Path rootLocation = Paths.get("file-storage");

    public void store(MultipartFile file) {
        try {
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            // Security check to prevent storing files outside the designated folder
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            // Copy the file to the destination
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    // Custom exception for storage errors
    public static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }




    private Pigeon findPigeonByRingNumber(List<Pigeon> pigeons, String ringNumber) {
        return pigeons.stream()
                .filter(p -> p.getRingNumber().equals(ringNumber))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pigeon not found"));
    }

    public List<Result> getAllResultats() {
        return resultatRepository.findAll();
    }

    public Optional<Result> findByCompetitionAndPigeon(Competition competition, Pigeon pigeon){
        return resultatRepository.findByCompetitionAndPigeon(competition, pigeon);
    }
}
