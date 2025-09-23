package nfl;


import nfl.model.WeeklyData;

/**
 * Fetch spreads and create pdf
 *
 */
public class App 
{

    private static final String tempFile = "temp.csv";

    public static void main( String[] args ) {
        PdfMaker pdfMaker = new PdfMaker();
        SpreadFetcher spreadFetcher = new SpreadFetcher();
        CsvConverter converter = new CsvConverter();
        String fetchHeader = "Week 4";
        String filename = "NFL Week 4 Spreads.pdf";
        String title = "NFL Week 4 Spreads";
        boolean shouldRefetchSpreads = true;
        int horizontal_line_position = 2;
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
                converter.write(data.getSpreads(), tempFile);
            }
        } catch (Exception ex) {
            System.out.println("Exception writing: " + ex.getMessage());
            return;
        }

        try {
            readData = new WeeklyData();
            readData.setSpreads(converter.read(tempFile));
            readData.setTitle(title);
            //System.out.println("CHY " + readData.getSpreads());
        } catch (Exception ex) {
            System.out.println("Exception reading: " + ex.getMessage());
            return;
        }

        try {
            pdfMaker.make(readData, filename, horizontal_line_position);
        } catch (Exception ex) {
            System.out.println("Exception making pdf: " + ex.getMessage());
            return;
        }
    }
}
