package it.flavianopetrocchi.reshelper;

import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

public class ResHelper {

    private Class cl;
    private String propertiesFile;
    private ResourceBundle textResource;

    public ResHelper(Class cl, String propertiesFile) {
        this.cl = cl;
        this.propertiesFile = propertiesFile;
        textResource = ResourceBundle.getBundle(this.propertiesFile);
    }

    public String getString(String key) {
        String text = null;
        try {
            text = textResource.getString(key);
        } catch (Exception e) {

        }
        return text;
    }

    public ImageIcon getIcon(String name) {
        ImageIcon icon = null;
        URL url = null;
        try {
            url = cl.getResource(name);
            if (url != null) {
                icon = new ImageIcon(url);
            }
        } catch (Exception e) {
        }
        return icon;
    }
}
