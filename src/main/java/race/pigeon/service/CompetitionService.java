package race.pigeon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import race.pigeon.model.entity.Competition;
import race.pigeon.repository.CompetitionRepository;

@Service
public class CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    public Competition addCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }

    public Competition findById(String id){
        return competitionRepository.findById(id);
    }
}
