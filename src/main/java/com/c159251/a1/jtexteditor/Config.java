package com.c159251.a1.jtexteditor;



import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import javafx.scene.text.Font;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.ScalarStyle;

public class Config {


    private String username;
    private int fontSize;
    private String textFont;
    private String codeFont;
    private ArrayList<String> themes;

    public ArrayList<String> getThemes() {
        return themes;
    }



    public Config() {

        Map<String, Object> configMap = loadConfigYaml();

        if (!(configMap == null)) {
            this.username = (String) configMap.get("username");
            this.fontSize = (int) configMap.get("fontSize");
            this.textFont = (String) configMap.get("textFont");
            this.codeFont = (String) configMap.get("codeFont");

            this.themes = (ArrayList<String>) configMap.get("themes");


        } else {
            this.username = System.getenv("username");
            this.fontSize = 12;
            this.textFont = "Arial";
            this.codeFont = "Consolas";

        }


    }



    @Override
    public String toString() {
        return username + " uses " + fontSize + "pt " + textFont + " for text and " + codeFont + " for code";
    }


    private Map<String, Object> loadConfigYaml() {

        LoadSettings settings = LoadSettings.builder().setLabel("Custom user configuration").build();
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

    void setTextFont(String family) {
        this.textFont = family;
    }


    void setCodeFont(String family) {
        this.codeFont = family;
    }

    void updateConfig(String username, int size, String text, String code) {

        setUsername(username);
        setFontSize(size);
        setTextFont(text);
        setCodeFont(code);
        saveConfigYaml();

    }

    Font getTextFont() {
        return new Font(this.textFont, (double) this.fontSize);
    }

    Font getCodeFont() {
        return new Font(this.codeFont, (double) this.fontSize);
    }

    private void saveConfigYaml() {

        DumpSettings settings = DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.PLAIN).build();
        Dump dump = new Dump(settings);
        HashMap<String, Object> output = new HashMap<>();
        output.put("username", this.username);
        output.put("fontSize", this.fontSize);
        output.put("textFont", this.textFont);
        output.put("codeFont", this.codeFont);
        output.put("themes", this.themes);

        String outString = "#configuration\n" + dump.dumpToString(output);

        File outputFile = new File("src/main/Config/config.yaml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(outString);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}