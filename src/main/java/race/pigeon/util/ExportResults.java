package race.pigeon.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import race.pigeon.model.entity.Competition;
import race.pigeon.model.entity.Pigeon;
import race.pigeon.model.entity.Result;
import race.pigeon.repository.CompetitionRepository;
import race.pigeon.repository.PigeonRepository;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportResults {

    private final List<Result> listResults;
    private final Competition competition;

    public ExportResults(List<Result> listResults, Competition competition) {
        this.listResults = listResults;
        this.competition = competition;
    }

    private void writeCompetitionInfo(Document document) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(Color.BLACK);

        document.add(new Paragraph("Lacher: " + competition.getReleasePlace(), font));
        document.add(new Paragraph("Heure: " + formatTime(competition.getDepartureTime()), font));
        document.add(new Paragraph("Coordonnées: " + competition.getLatitude() + ", " + competition.getLongitude(), font));
        document.add(new Paragraph("Nombre de pigeon: " + competition.getPigeonCount(), font));
        document.add(new Paragraph("% d’admission: " + competition.getPercentage(), font));
        document.add(new Paragraph("\n"));
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.RED);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("CL", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Colombier", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("N°Bague", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Heure", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Distance", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Vitesse", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Point", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Result result : listResults) {
            Pigeon pigeon = result.getPigeon();
            table.addCell(String.valueOf(result.getRanking()));
            table.addCell(pigeon.getUser().getLoftName());
            table.addCell(pigeon.getRingNumber());
            table.addCell(formatArrivalDate(result.getHeureArrivee()));
            table.addCell(String.format("%.2f km", result.getDistance()));
            table.addCell(String.format("%.2f m/mn", result.getVitesse()));
            table.addCell(String.valueOf(result.getPoint()));
        }
    }

    private String formatTime(LocalDateTime dateTime) {
        return String.format("%02d:%02d:%02d", dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
    }

    public static String formatArrivalDate(LocalDateTime arrivalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return arrivalDate.format(formatter);
    }


    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        writeCompetitionInfo(document);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{2.0f, 3.0f, 3.0f, 2.5f, 2.5f, 2.5f, 2.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);
        document.close();
    }
}
