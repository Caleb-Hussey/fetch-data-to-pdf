package nfl;


import nfl.model.WeeklyData;

import java.util.ArrayList;
import java.util.List;

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
        String header = "Week 1";
        int weekNumber = 1;
        String filename = "NFL Week 1 Spreads.pdf";
        int horizontal_line_position = 2;
        WeeklyData data;
        WeeklyData readData;
        try {
            data = spreadFetcher.fetch(header);
        } catch (Exception ex) {
            System.out.println("Exception fetching: " + ex.getMessage());
            return;
        }

        try {
            converter.write(data.getSpreads(), tempFile);
        } catch (Exception ex) {
            System.out.println("Exception writing: " + ex.getMessage());
            return;
        }

        try {
            readData = new WeeklyData();
            readData.setWeekNumber(weekNumber);
            readData.setSpreads(converter.read(tempFile));
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
