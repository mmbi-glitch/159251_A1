package com.c159251.a1.jtexteditor;



import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.text.Font;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Config {


    private String username;
    private int fontSize;
    private String textFont;
    private String codeFont;


    public Config() {

        Map<String, Object> configMap = loadConfigYaml();
        this.username = (String) configMap.get("username");
        this.fontSize = (int) configMap.get("fontSize");
        this.textFont = (String) configMap.get("textFont");
        this.codeFont = (String) configMap.get("codeFont");


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

        DumpSettings settings = DumpSettings.builder().build();
        Dump dump = new Dump(settings);
        HashMap<String, Object> output = new HashMap<>();
        output.put("codeFont", this.codeFont);
        output.put("textFont", this.textFont);
        output.put("fontSize", this.fontSize);
        output.put("username", this.username);

        String outString = "#configuration\n" + dump.dumpToString(output);
        System.out.println(outString);

        File outputFile = new File("src/main/Config/config.yaml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(outString);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}