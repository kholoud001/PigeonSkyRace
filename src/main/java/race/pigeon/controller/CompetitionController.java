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
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.appUser;
import race.pigeon.model.enums.Role;
import race.pigeon.repository.AppUserRepository;
import race.pigeon.service.CompetitionService;
import race.pigeon.service.PigeonService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PigeonService pigeonService;

    @PostMapping("/add")
    public ResponseEntity<Competition> addCompetition(@RequestBody Competition competition) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        appUser user = appUserRepository.findByUsername(currentUsername);

        if (user == null || user.getRole() != Role.Organizer) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        competition.setUser(user);

        Competition savedCompetition = competitionService.addCompetition(competition);
        return new ResponseEntity<>(savedCompetition, HttpStatus.CREATED);
    }

    @PostMapping("/addPigeon")
    public ResponseEntity<Competition> addPigeonToCompetition(@RequestBody Competition competitionRequest) {
        Competition existingCompetition = competitionService.findById(competitionRequest.getId());
        if (existingCompetition == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<String> pigeonRingNumbers = competitionRequest.getPigeons()
                .stream()
                .map(Pigeon::getRingNumber)
                .toList();

        List<Pigeon> pigeonsToAdd = pigeonService.findByRingNumbers(pigeonRingNumbers);

        if (existingCompetition.getPigeons() == null) {
            existingCompetition.setPigeons(new ArrayList<>());
        }

        existingCompetition.getPigeons().addAll(pigeonsToAdd);

        Competition updatedCompetition = competitionService.addCompetition(existingCompetition);
        return new ResponseEntity<>(updatedCompetition, HttpStatus.OK);
    }

}
