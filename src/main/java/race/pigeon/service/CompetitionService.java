package race.pigeon.service;

import race.pigeon.model.entity.Competition;

public interface CompetitionService {
    Competition addCompetition(Competition competition);

    Competition findById(String id);
}
