package nfl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import nfl.model.Spread;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.List;

class CsvConverter {

    void writeSpreads(List<Spread> data, String filePath) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        // CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
        Writer writer = new FileWriter(filePath);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        beanToCsv.write(data);
        writer.close();
    }

    List<Spread> readSpreads(String filePath) throws FileNotFoundException {
        CsvToBean<Spread> reader = new CsvToBeanBuilder(new FileReader(filePath))
            .withType(Spread.class)
            .build();
        return reader.parse();
    }

    void writeByeWeeks(List<String> data, String filePath) throws IOException {
        // CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath));
        //Writer writer = new FileWriter(filePath);
        //StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
        //beanToCsv.write(data);
        //writer.close();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(data);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    List<String> readByeWeeks(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        List<String> byeWeekTeams = (List<String>) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return byeWeekTeams;
    }
}
