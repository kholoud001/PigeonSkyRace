package race.pigeon.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import race.pigeon.model.entity.Result;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.repository.PigeonRepository;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvParserUtil {

    private final PigeonRepository pigeonRepository;
    private final CompetitionRepository competitionRepository;

    // Constructor injection to get the repositories
    public CsvParserUtil(PigeonRepository pigeonRepository, CompetitionRepository competitionRepository) {
        this.pigeonRepository = pigeonRepository;
        this.competitionRepository = competitionRepository;
    }

//    public List<Result> parseCsvFile(MultipartFile file) throws Exception {
//        List<Result> raceDataList = new ArrayList<>();
//
//        try (Reader reader = new InputStreamReader(file.getInputStream())) {
//            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
//
//            for (CSVRecord record : records) {
//                Result raceData = new Result();
//                raceData.setHeureArrivee(record.get("heureArrivee"));
//                raceData.setDistance(Double.parseDouble(record.get("distance")));
//                raceData.setVitesse(Double.parseDouble(record.get("vitesse")));
//                raceData.setPoint(Double.parseDouble(record.get("point")));
//                raceData.setRingNumber(record.get("ringNumber"));
//
//                raceDataList.add(raceData);
//            }
//        }
//
//        return raceDataList;
//    }
}
