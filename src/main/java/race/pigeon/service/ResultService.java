package race.pigeon.service;

import org.springframework.web.multipart.MultipartFile;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;
import race.pigeon.model.entity.appUser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ResultService {
    // Method to upload and process CSV data
    void uploadData(MultipartFile file, String competitionId) throws IOException;

    // Calculate and update distance for a specific pigeon
    Result calculateAndUpdateDistance(Competition competition, appUser user, Pigeon pigeon);

    // Method to calculate and update speed (vitesse)
    String calculateVitesse(Result result);

    // Method to calculate flight time between departure and arrival
    String calculateFlightTime(Result result);

    // Store the uploaded file securely
    void store(MultipartFile file) throws IOException;

    List<Result> getResultsByRank(Competition competition);

    Optional<Result> findByCompetitionAndPigeon(Competition competition, Pigeon pigeon);
}
