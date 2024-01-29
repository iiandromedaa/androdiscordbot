package com.androbohij;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class Teller {

    private Reader input;
    private FileWriter output;
    private List<Map<String, String>> bank;
    private CSVFormat format;

    Teller(File file) throws IOException {
        input = new FileReader(file);
        // output = new FileWriter(file);
        format = CSVFormat.DEFAULT.builder().setHeader("snowflake", "tomilliens").setSkipHeaderRecord(true).build();
        bank = new ArrayList<>();
    }

    void loadToMap() throws IOException {
        Iterable<CSVRecord> records = format.parse(input);
        for (CSVRecord record : records) {
            String snowflake = record.get("snowflake");
            String cash = record.get("tomilliens");
            bank.add(record.toMap());
        }
    }

    void newAccount(String snowflake) {
        
    }

    static Map<String, String> getAccount(String snowflake) {
        for (Map<String, String> map : bank) {
            if (map.containsKey(snowflake))
                return map;
        }
        //empty map if we fucked up and cant find
        return new HashMap<String,String>();
    }

    //writes to csv specifically
    static void writeChanges() {

    }

    List<Map<String, String>> getMap() {
        return bank;
    }

}
