package race.pigeon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import race.pigeon.model.entity.Competition;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.service.CompetitionService;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public Competition addCompetition(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public Competition findById(String id) {
        return competitionRepository.findById(id);
    }
}
