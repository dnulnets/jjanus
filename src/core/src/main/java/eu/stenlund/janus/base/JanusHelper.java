package eu.stenlund.janus.base;

/**
 * A helper class for various functions used in the Janus application.
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public abstract class JanusHelper {
    
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
