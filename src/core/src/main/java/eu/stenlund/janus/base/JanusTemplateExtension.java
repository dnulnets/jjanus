package eu.stenlund.janus.base;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension(namespace = "janus")
public class JanusTemplateExtension {
    
    /**
     * Return with the value of a property in the configuration file.
     * 
     * @param name The name of the property
     * @return The value as a string
     */
    public static String property(String name)
    {
        return ConfigProvider.getConfig().getValue(name, String.class);
    }

    /**
     * Return with the value of a property in the configuration file.
     * 
     * @param name The name of the property
     * @param dflt The default value of the property if it does not exist
     * @return The value or the default value of the property
     */
    public static String property(String name, String dflt)
    {
        String value = ConfigProvider.getConfig().getValue(name, String.class);
        if (value == null)
            value = dflt;
        return value;
    }

}
