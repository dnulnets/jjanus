package eu.stenlund.janus.base;

import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * A helper class for various functions used in the Janus application.
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public abstract class JanusHelper {
    

    /**
     * Return with the value of a property.
     * 
     * @param <T> The type of the property.
     * @param type The type class of the property.
     * @param property The name of the property.
     * @return Returns with the value.
     */
    public static <T> T getConfig(Class<T> type, String property)
    {
        return ConfigProvider.getConfig().getValue(property, type);
    }

    /**
     * Return with the value of a property or default.
     * 
     * @param <T> The type of the property.
     * @param type The type class of the property.
     * @param property The name of the property.
     * @param dflt The default of the property if it does not exists.
     * @return Returns with the value.
     */
    public static <T> T getConfig(Class<T> type, String property, T dflt)
    {
        Optional<T> value = ConfigProvider.getConfig().getOptionalValue(property, type);
        return value.orElse(dflt);
    }

    /**
     * Check if the string is not null and not empty.
     * 
     * @param s The string to check.
     * @return True if the string is not null and contain at least some alphanumeric character.
     */
    public static boolean isBlank (String s)
    {
        if (s!=null)
            return s.isBlank();
        else
            return true;
    }
}
