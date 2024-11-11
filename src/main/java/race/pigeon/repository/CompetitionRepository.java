package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;

@Repository
public interface CompetitionRepository extends MongoRepository<Competition, Integer> {
    Competition findById(String id);
}
