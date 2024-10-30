package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import race.pigeon.entities.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
