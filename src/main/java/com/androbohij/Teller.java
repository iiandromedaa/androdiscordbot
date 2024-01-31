package com.androbohij;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


public class Teller {

    private static File csv = new File("src\\main\\java\\com\\androbohij\\cash.csv");
    private static List<Map<String, String>> bank = new ArrayList<>();
    private static CSVFormat format = CSVFormat.DEFAULT.builder().setHeader("snowflake", "tomilliens", "username").setSkipHeaderRecord(true).build();

    static void loadToMap() throws IOException {
        Reader input = new FileReader(csv);
        Iterable<CSVRecord> records = format.parse(input);
        for (CSVRecord record : records) {
            bank.add(record.toMap());
        }
        input.close();
    }

    //0 stands for 0K!!! :)
    static int newAccount(String snowflake, String username) {
        if (!getAccount(snowflake).isEmpty())
            return 1;
        bank.add(new HashMap<String, String>() {{
            put("snowflake", snowflake);
            put("tomilliens", "100");
            put("username", username);
        }});
        writeChanges();
        return 0;
    }

    static Map<String, String> getAccount(String snowflake) {
        for (Map<String, String> map : bank) {
            if (map.containsValue(snowflake))
                return map;
        }
        //empty map if we fucked up and cant find
        return new HashMap<String,String>();
    }

    //writes to csv specifically
    static void writeChanges() {
        try (FileWriter csvWriter = new FileWriter("src\\main\\java\\com\\androbohij\\cash.csv")) {
            csvWriter.append("snowflake,tomilliens,username").append("\n");
            for (Map<String, String> map : bank) {
                csvWriter.append(map.get("snowflake")).append(",");
                csvWriter.append(map.get("tomilliens")).append(",");
                csvWriter.append(map.get("username")).append("\n");
            }
            csvWriter.close();
        } catch (IOException e) {
            
        }
    }

    static void deleteAccount(String snowflake) {
        if (!getAccount(snowflake).isEmpty()) {
            bank.remove(getAccount(snowflake));
            writeChanges();
        }
    }

    static void transfer(String snowflake, String recipient, Double amount) {
        Map<String, String> user = getAccount(snowflake);
        Map<String, String> reci = getAccount(recipient);
        double i = Double.parseDouble(user.get("tomilliens"));
        double j = Double.parseDouble(reci.get("tomilliens"));
        user.put("tomilliens", Double.toString(i-amount));
        reci.put("tomilliens", Double.toString(j+amount));
        writeChanges();
    }

    static List<Map<String, String>> getMap() {
        return bank;
    }

}
