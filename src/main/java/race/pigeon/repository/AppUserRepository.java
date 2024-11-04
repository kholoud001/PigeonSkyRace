package race.pigeon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import race.pigeon.model.entity.appUser;

@Repository
public interface AppUserRepository extends MongoRepository<appUser, Integer> {
    appUser findByUsername(String username);

    Boolean existsByUsername(String username);

    appUser findByLoftName(String email);
}
