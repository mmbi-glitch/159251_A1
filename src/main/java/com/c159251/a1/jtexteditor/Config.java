package com.c159251.a1.jtexteditor;



import java.io.*;
import java.util.*;

import javafx.scene.text.Font;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.ScalarStyle;

public class Config {


    private String username;
    private int fontSize;


     String getUsername() {
        return this.username;
    }

    void setUsername(String name) {
        this.username = name;
    }

    int getFontSize() {
        return this.fontSize;
    }

    void setFontSize(int size) {
        this.fontSize = size;
    }




    public Config() {

        Map<String, Object> configMap = loadConfigYaml();

        if (!(configMap == null)) {
            this.username = (String) configMap.get("username");
            this.fontSize = (int) configMap.get("fontSize");


        } else {
            this.username = System.getenv("username");
            this.fontSize = 12;


        }


    }



    private Map<String, Object> loadConfigYaml() {

        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);

        try (InputStream stream = new FileInputStream("src/main/Config/config.yaml")) {

            return (Map<String, Object>) load.loadFromInputStream(stream);

        } catch (FileNotFoundException fnf) {
            System.out.println("Config file not found, creating default");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }



    void updateConfig(String username, int size) {

        setUsername(username);
        setFontSize(size);


        saveConfigYaml();

    }



    private void saveConfigYaml() {

        DumpSettings settings = DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED).build();
        Dump dump = new Dump(settings);

        HashMap<String, Object> output = new HashMap<>();
        output.put("username", this.username);
        output.put("fontSize", this.fontSize);


        String outString = "#configuration\n" + dump.dumpToString(output);

        File outputFile = new File("src/main/Config/config.yaml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(outString);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}