package com.c159251.a1.jtexteditor;



import java.io.InputStream;
import java.util.Map;

import javafx.scene.text.Font;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Config {


    private String username;
    private int fontSize;
    private String textFont;
    private String codeFont;



    public Config() {

        Map<String,Object> configMap = loadConfigYaml();
        this.username = (String) configMap.get("username");
        this.fontSize = (int) configMap.get("fontSize");
        this.textFont = (String) configMap.get("textFont");
        this.codeFont = (String) configMap.get("codeFont");


    }

    @Override
    public String toString() {
        return username + " uses " + fontSize + "pt " + textFont + " for text and " + codeFont + " for code";
    }

    private Map<String,Object> loadConfigYaml() {

        LoadSettings settings = LoadSettings.builder().setLabel("Custom user configuration").build();
        Load load = new Load(settings);

        try (InputStream stream = this.getClass().getResourceAsStream("config.yaml")) {

            return (Map<String,Object>) load.loadFromInputStream(stream);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    Font getTextFont() {
        return new Font(this.textFont,(double)this.fontSize);
    }
    Font getCodeFont() {
        return new Font(this.codeFont,(double)this.fontSize);
    }

    private void saveConfigYaml() {

    }

}
