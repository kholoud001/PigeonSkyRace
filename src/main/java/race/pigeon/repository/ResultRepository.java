package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.Result;

@Repository
public interface ResultRepository extends MongoRepository<Result, String> {
}
