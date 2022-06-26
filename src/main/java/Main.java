import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvConverter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //задача 2
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> result = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader).
                    withMappingStrategy(strategy).
                    build();
            result = csvToBean.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseXML(String src) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        List<Employee> result = new ArrayList<>();
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(src));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        Node root = null;
        if (doc != null) {
            root = doc.getDocumentElement();
        }
        Element employee = (Element) root;
        for (int i = 0; i < employee.getElementsByTagName("id").getLength(); i++) {
            Employee employeeTmp = new Employee();
            employeeTmp.id = Long.parseLong(employee.getElementsByTagName("id").item(i).getTextContent());
            employeeTmp.firstName = employee.getElementsByTagName("firstName").item(i).getTextContent();
            employeeTmp.lastName = employee.getElementsByTagName("lastName").item(i).getTextContent();
            employeeTmp.country = employee.getElementsByTagName("country").item(i).getTextContent();
            employeeTmp.age = Integer.parseInt(employee.getElementsByTagName("age").item(i).getTextContent());
            result.add(employeeTmp);
            System.out.println();
        }
        return result;
    }

    private static void writeString(String json, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(json);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
