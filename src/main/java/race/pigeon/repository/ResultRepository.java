package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;

import java.util.Optional;

@Repository
public interface ResultRepository extends MongoRepository<Result, String> {
    Optional<Result> findByCompetitionAndPigeon(Competition competition, Pigeon pigeon);

}
