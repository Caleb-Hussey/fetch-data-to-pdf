package nfl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import nfl.model.Spread;
import nfl.model.WeeklyData;

import java.io.*;
import java.util.List;

class CsvConverter {

    void write(List<Spread> data, String filePath) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        // CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
        Writer writer = new FileWriter(filePath);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        beanToCsv.write(data);
        writer.close();
    }

    List<Spread> read(String filePath) throws FileNotFoundException {
        CsvToBean<Spread> reader = new CsvToBeanBuilder(new FileReader(filePath))
            .withType(Spread.class)
            .build();
        return reader.parse();
    }
}
