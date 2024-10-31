package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.entities.model.Breeder;

@Repository
public interface BreederRepository extends MongoRepository<Breeder, Integer> {
    boolean existsByName(String name);
}
