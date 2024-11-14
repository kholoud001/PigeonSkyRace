package race.pigeon.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import race.pigeon.model.entity.appUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import race.pigeon.repository.AppUserRepository;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    AppUserRepository appUserRepository ;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        appUser appUser = appUserRepository.findByUsername(username);
        if (appUser != null) {
            var springUser = User.withUsername(appUser.getUsername()).password(appUser.getPassword()).roles(String.valueOf(appUser.getRole())).build();
            return springUser;
        }
        return null;
    }

    public appUser findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }
}
