package race.pigeon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.appUser;
import race.pigeon.model.enums.Role;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.CompetitionService;

@RestController
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostMapping("/add")
    public ResponseEntity<Competition> addCompetition(@RequestBody Competition competition) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        appUser user = appUserRepository.findByUsername(currentUsername);

        if (user == null || user.getRole() != Role.Organizer) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Competition savedCompetition = competitionService.addCompetition(competition);
        return new ResponseEntity<>(savedCompetition, HttpStatus.CREATED);
    }

}
