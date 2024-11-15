package race.pigeon.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
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
import race.pigeon.service.ResultService;
import race.pigeon.util.GeoUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultatRepository;

    @Autowired
    private PigeonRepository pigeonRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    private final Path rootLocation = Paths.get("file-storage");

    // Method to upload and process CSV data
    @Override
    public void uploadData(MultipartFile file, String competitionId) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> rows = csvReader.readAll();

            // Fetch competition, handle the case where it's not found
            Competition competition = competitionRepository.findById(competitionId);
            if (competition == null) {
                System.err.println("Competition not found with ID: " + competitionId);
                return;
            }

            // Process each row in the CSV
            for (String[] row : rows) {
                String heureArriveeString = row[0]; // Arrival Time
                String distanceString = row[1]; // Distance
                String vitesseString = row[2]; // Speed
                String pointString = row[3]; // Points
                String ringNumber = row[4]; // Ring Number (to fetch Pigeon)

                double distance = parseDouble(distanceString);
                double vitesse = parseDouble(vitesseString);
                double point = parseDouble(pointString);

                LocalDateTime heureArrivee = parseDateTime(heureArriveeString);

                // Fetch pigeon by ring number, handle the case where it's not found
                Pigeon pigeon = pigeonRepository.findByRingNumber(ringNumber)
                        .orElseThrow(() -> new RuntimeException("Pigeon not found with ring number: " + ringNumber));

                // Create and populate Result entity
                Result result = new Result();
                result.setHeureArrivee(heureArrivee);
                result.setDistance(distance);
                result.setVitesse(vitesse);
                result.setPoint(point);
                result.setPigeon(pigeon);
                result.setCompetition(competition);

                // Save the result to the database
                resultatRepository.save(result);
            }
        } catch (IOException e) {
            throw new IOException("Error processing the CSV file: " + e.getMessage(), e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to safely parse doubles, returns 0 if invalid
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + value);
            return 0;
        }
    }

    // Helper method to safely parse date-time
    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            System.err.println("Invalid date format: " + dateTimeString);
            return null;
        }
    }

    // Calculate and update distance for a specific pigeon
    @Override
    public Result calculateAndUpdateDistance(Competition competition, appUser user, Pigeon pigeon) {
        Optional<Result> existingResult = resultatRepository.findByCompetitionAndPigeon(competition, pigeon);

        // Calculate distance using GeoUtils (method not shown here)
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

    // Method to calculate and update speed (vitesse)
    @Override
    public String calculateVitesse(Result result) {
        if (result.getDistance() <= 0 || result.getHeureArrivee() == null || result.getCompetition().getDepartureTime() == null) {
            return "Invalid data: Distance, arrival time, or competition departure time is missing.";
        }

        Duration flightTime = Duration.between(result.getCompetition().getDepartureTime(), result.getHeureArrivee());

        if (flightTime.isNegative() || flightTime.isZero()) {
            return "Invalid flight time.";
        }

        double flightTimeMinutes = flightTime.toMinutes();
        double vitesse = result.getDistance() / flightTimeMinutes;
        result.setVitesse(vitesse);

        // Save the updated result entity
        resultatRepository.save(result);

        // Update rankings and get the updated rank and points for this result
        int updatedRank = updateRankings(result.getCompetition(), result);

        return String.format("Speed (Vitesse) updated successfully: %.2f m/min. Rank: %d, Points: %.2f",
                vitesse, result.getRanking(), result.getPoint());
    }

    // Method to update rankings and points after all results are uploaded
    private int updateRankings(Competition competition, Result updatedResult) {
        List<Result> results = resultatRepository.findByCompetition(competition);
        results.sort((r1, r2) -> Double.compare(r2.getVitesse(), r1.getVitesse())); // Sort by speed in descending order

        int totalParticipants = results.size();
        for (int rank = 0; rank < totalParticipants; rank++) {
            Result result = results.get(rank);
            result.setRanking(rank + 1);
            double points = calculatePoints(rank + 1, totalParticipants);
            result.setPoint(points);

            // Check if this is the updated result to return its rank
            if (result.getId().equals(updatedResult.getId())) {
                updatedResult.setRanking(rank + 1);
                updatedResult.setPoint(points);
            }
        }

        resultatRepository.saveAll(results);
        return updatedResult.getRanking();
    }

    private double calculatePoints(int rank, int totalParticipants) {
        return 100 * (1 - (double) (rank - 1) / (totalParticipants - 1));
    }

    // Method to calculate flight time between departure and arrival
    @Override
    public String calculateFlightTime(Result result) {
        Competition competition = result.getCompetition();
        LocalDateTime departureTime = competition.getDepartureTime();
        LocalDateTime arrivalTime = result.getHeureArrivee();

        if (departureTime == null || arrivalTime == null) {
            return "Departure or arrival time is not available.";
        }

        Duration duration = Duration.between(departureTime, arrivalTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }

    // Store the uploaded file securely
    @Override
    public void store(MultipartFile file) throws IOException {
        try {
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new IOException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IOException("Failed to store file.", e);
        }
    }

    public List<Result> getAllResultats() {
        return resultatRepository.findAll();
    }

    @Override
    public List<Result> getResultsByRank(Competition competition) {
        return resultatRepository.findByCompetitionOrderByRankingAsc(competition);
    }

    @Override
    public Optional<Result> findByCompetitionAndPigeon(Competition competition, Pigeon pigeon) {
        return resultatRepository.findByCompetitionAndPigeon(competition, pigeon);
    }

    @Override
    public List<Result> getResultsByCompetition(String competitionId) {
        Competition competition = competitionRepository.findById(competitionId);
        if (competition != null) {
            return resultatRepository.findByCompetition(competition);
        } else {
            return Collections.emptyList();
        }
    }

}
