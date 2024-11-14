package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;

import java.util.List;
import java.util.Optional;

@Repository
public interface PigeonRepository extends MongoRepository<Pigeon, String> {
    List<Pigeon> findByRingNumberIn(List<String> ringNumbers);
    List<Pigeon> findByUser_Id(String userId);


    Optional<Pigeon> findByRingNumber(String ringNumber);
    List<Pigeon> findByCompetition(Competition competition);

}
