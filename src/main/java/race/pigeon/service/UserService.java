package race.pigeon.service;

import race.pigeon.model.entity.User;

public interface UserService {

    User addUser(User user);

    String verify(String username, String password);
}
