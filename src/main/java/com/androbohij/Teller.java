package com.androbohij;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


public class Teller {

    private static Reader input;
    private static FileWriter output;
    private static List<Map<String, String>> bank;
    private static CSVFormat format;

    Teller(InputStream file) throws IOException {
        input = new InputStreamReader(file);
        format = CSVFormat.DEFAULT.builder().setHeader("snowflake", "tomilliens").setSkipHeaderRecord(true).build();
        bank = new ArrayList<>();
    }

    static void loadToMap() throws IOException {
        Iterable<CSVRecord> records = format.parse(input);
        for (CSVRecord record : records) {
            String snowflake = record.get("snowflake");
            String cash = record.get("tomilliens");
            bank.add(record.toMap());
        }
    }

    static int newAccount(String id) {
        for (Map<String, String> hash : bank){
            if (hash.containsValue(id))
                return 1;
        }
        bank.add(new HashMap<String, String>() {{
            put("snowflake", id);
            put("tomilliens", "100");
        }});
        return 0;
    }

    static void getAccount(String snowflake) {

    }

    static void writeChanges() {
        
    }

    static List<Map<String, String>> getMap() {
        return bank;
    }

}
