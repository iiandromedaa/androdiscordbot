package com.androbohij;
import java.io.FileReader;
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

    private static InputStream csv = Teller.class.getClassLoader().getResourceAsStream("cash.csv");
    private static List<Map<String, String>> bank = new ArrayList<>();
    private static CSVFormat format = CSVFormat.DEFAULT.builder().setHeader("snowflake", "tomilliens").setSkipHeaderRecord(true).build();

    static void loadToMap() throws IOException {
        Reader input = new InputStreamReader(csv);
        Iterable<CSVRecord> records = format.parse(input);
        for (CSVRecord record : records) {
            String snowflake = record.get("snowflake");
            String cash = record.get("tomilliens");
            bank.add(record.toMap());
        }
    }

    //0 stands for 0K!!! :)
    static int newAccount(String snowflake) {
        if (!getAccount(snowflake).isEmpty())
            return 1;
        bank.add(new HashMap<String, String>() {{
            put("snowflake", snowflake);
            put("tomilliens", "100");
        }});
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

    }

    static List<Map<String, String>> getMap() {
        return bank;
    }

}
