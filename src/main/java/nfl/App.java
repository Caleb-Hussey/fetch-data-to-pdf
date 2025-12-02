package nfl;


import nfl.model.WeeklyData;

/**
 * Fetch spreads and create pdf
 *
 */
public class App 
{

    private static final String tempSpreadsFile = "spreads.csv";
    private static final String tempByeWeeksFile = "bye_weeks.csv";

    public static void main( String[] args ) {
        PdfMaker pdfMaker = new PdfMaker();
        SpreadFetcher spreadFetcher = new SpreadFetcher();
        CsvConverter converter = new CsvConverter();
        String fetchHeader = "Week 14";
        String filename = "NFL Week 14 Spreads.pdf";
        String title = "NFL Week 14 Spreads";
        boolean shouldRefetchSpreads = true;
        boolean shouldIncludeByeWeeks = true;
        int horizontal_line_position = 1;
        WeeklyData data = null;
        WeeklyData readData;
        try {
            if (shouldRefetchSpreads) {
                data = spreadFetcher.fetch(fetchHeader);
            }
        } catch (Exception ex) {
            System.out.println("Exception fetching: " + ex.getMessage());
            return;
        }

        try {
            if (shouldRefetchSpreads) {
                converter.writeSpreads(data.getSpreads(), tempSpreadsFile);
                converter.writeByeWeeks(data.getByeWeekTeams(), tempByeWeeksFile);
            }
        } catch (Exception ex) {
            System.out.println("Exception writing: " + ex.getMessage());
            return;
        }

        try {
            readData = new WeeklyData();
            readData.setSpreads(converter.readSpreads(tempSpreadsFile));
            readData.setByeWeekTeams(converter.readByeWeeks(tempByeWeeksFile));
            readData.setTitle(title);
            //System.out.println("CHY " + readData.getSpreads());
        } catch (Exception ex) {
            System.out.println("Exception reading: " + ex.getMessage());
            return;
        }

        try {
            pdfMaker.make(readData, filename, horizontal_line_position, shouldIncludeByeWeeks);
        } catch (Exception ex) {
            System.out.println("Exception making pdf: " + ex.getMessage());
        }
    }
}
