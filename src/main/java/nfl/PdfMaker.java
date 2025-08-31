package nfl;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import nfl.model.Spread;
import nfl.model.WeeklyData;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;

public class PdfMaker {

    private static final PDFont headerFont = PDType1Font.HELVETICA_BOLD;
    private static final int headerFontSize = 22;

    private static final PDFont spreadHeaderFont = PDType1Font.HELVETICA_BOLD;
    private static final PDFont spreadFont = HELVETICA;
    private static final int spreadFontSize = 14;

    private static final String header_date = "Date & Time";
    private static final String header_favorite = "Favorite";
    private static final String header_spread = "Spread";
    private static final String header_underdog = "Underdog";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd h:mm z");



    public void make(WeeklyData data, String savePath, int horizontal_line_position) throws IOException {
        TimeZone etTimeZone = TimeZone.getTimeZone("America/New_York");
        dateFormat.setTimeZone(etTimeZone);

        // Create a new empty document
        PDDocument document = new PDDocument();

        // Create a new blank page and add it to the document
        PDPage page = new PDPage();
        document.addPage( page );

        final String headerText = data.getHeader();
        float headerWidth = headerFont.getStringWidth(headerText) * headerFontSize / 1000;
        float headerHeight = headerFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerFontSize;

        PDRectangle pageSize = page.getMediaBox();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();
        float centerHorizontal = pageWidth / 2F;


        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont( headerFont, headerFontSize );

        float textX = centerHorizontal - 40 - headerWidth / 2F;
        float textY = pageHeight - headerHeight - 30;
        contentStream.setTextMatrix(Matrix.getTranslateInstance(textX, textY));

        contentStream.showText(headerText);
        contentStream.endText();

        // Uncomment for default image
        PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/football.jpg", document);
        // Uncomment for snowman
        // PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/christmas.PNG", document);
        int initialWidth = image.getWidth();
        int initialHeight = image.getHeight();
        // Uncomment for snowman
        // contentStream.drawImage(image, pageWidth - 110, textY - 30, (float) (initialWidth*0.35), (float) (initialHeight*0.35));

        // Uncomment for default image
        contentStream.drawImage(image, pageWidth - 140, textY - 45, (float) (initialWidth*0.15), (float) (initialHeight*0.15));

        float tableY = textY - 30F;
        float tableX = 50F;

        Table spreadsTable = createSpreadsTable(data, horizontal_line_position);

        // Set up the drawer
        TableDrawer tableDrawer = TableDrawer.builder()
            .contentStream(contentStream)
            .startX(tableX)
            .startY(tableY)
            .table(spreadsTable)
            .build();

        // And go for it!
        tableDrawer.draw();

        // Make sure that the content stream is closed:
        contentStream.close();

        // Save the newly created document
        document.save(savePath);

        // finally make sure that the document is properly
        // closed.
        document.close();
    }

    private Table createSpreadsTable(WeeklyData data, int horizontal_line_position) {

        final Table.TableBuilder tableBuilder = Table.builder()
            .addColumnsOfWidth(140, 140, 100, 140)
            .fontSize(spreadFontSize)
            .font(spreadFont)
            .borderWidth(0)
            .borderColor(Color.WHITE);

        // Add the header row ...
        tableBuilder.addRow(Row.builder()
            .add(TextCell.builder().text(header_date).build())
            .add(TextCell.builder().text(header_favorite).build())
            .add(TextCell.builder().text(header_spread).build())
            .add(TextCell.builder().text(header_underdog).build())
            .font(spreadHeaderFont)
            .horizontalAlignment(LEFT)
            .borderColor(Color.BLACK)
            .padding(10)
            .build());

        int index = 0;

        for (Spread spread : data.getSpreads()) {
            index++;
            tableBuilder.addRow(Row.builder()
                .add(TextCell.builder().text(dateFormat.format(spread.getDateTime())).borderWidthBottom(index == horizontal_line_position ? 3 : 0).borderColor(Color.BLACK).build())
                .add(TextCell.builder().text(spread.getFavorite()).borderWidthBottom(index == horizontal_line_position ? 3 : 0).borderColor(Color.BLACK).build())
                .add(TextCell.builder().text("-" + spread.getSpread()).borderWidthBottom(index == horizontal_line_position ? 3 : 0).borderColor(Color.BLACK).build())
                .add(TextCell.builder().text(spread.getUnderdog()).borderWidthBottom(index == horizontal_line_position ? 3 : 0).borderColor(Color.BLACK).build())
                .horizontalAlignment(LEFT)
                .padding(10)
                .build());
        }

        return tableBuilder.build();
    }
}
