package eu.stenlund.janus.ssr;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension(namespace = "janus")
public abstract class JanusTemplateExtension {
    
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

    /**
     * Concatenates two strings. The reason is that qute do not have any advanced expressions and one of the
     * most common needs are to concatenate two strings in Janus.
     * 
     * @param s1 The left string.
     * @param s2 The right string.
     * @return The concatenade strings.
     */
    public static String concat (String s1, String s2)
    {
        return s1+s2;
    }
}
