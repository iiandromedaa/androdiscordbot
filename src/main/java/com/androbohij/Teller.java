package com.androbohij;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    void getAccount(String snowflake) {

    }

    void writeChanges() {

    }

    List<Map<String, String>> getMap() {
        return bank;
    }

}
