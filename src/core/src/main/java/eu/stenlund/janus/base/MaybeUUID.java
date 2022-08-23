package eu.stenlund.janus.base;

import java.util.Objects;
import java.util.UUID;

/**
 * A UUID that handles empty strings, used by RestForm:s.
 *
 * @author Tomas Stenlund
 * @since 2022-08-23
 * 
 */
public class MaybeUUID {
    
    /**
     * The value or null.
     */
    private UUID value;

    /**
     * Returns with the value.
     * 
     * @return A UUID or null.
     */
    public UUID get() {
        return value;
    }

    /**
     * Private constructor, creates an empty MaybeUUID.
     */
    private MaybeUUID() {
        this.value = null;
    }

    /**
     * Private constructor, creates an MaybeUUID from a value.
     * @param value
     */
    private MaybeUUID(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Creates an empty MaybeUUID.
     * 
     * @return An empty MaybeUUID.
     */
    public static MaybeUUID empty() {
        MaybeUUID t = new MaybeUUID();
        return t;
    }

    /**
     * Creates an MaybeUUID from a value.
     * 
     * @param value The value.
     * @return A MaybeUUID containig the actual value.
     */
    public static MaybeUUID of(UUID value) {
        return new MaybeUUID(value);
    }

    /**
     * Creates an MaybeUUID from a value, that can be null.
     * @param value The actualy value or null.
     * @return A MaybeUUID containig the value or empty.
     */
    public static MaybeUUID ofNullable(UUID value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Returns true if it contains no value.
     * @return True or false.
     */
    public boolean isEmpty ()
    {
        return value == null;
    }

    /**
     * Returns with the value or with another provided value if it is empty.
     * 
     * @param uuid Fallback value.
     * @return The value or the fallback value.
     */
    public UUID orElse (UUID uuid)
    {
        return value == null ? uuid : value;
    }

    /**
     * Converts to a string.
     * 
     * @return A string representation of the value.
     */
    public String toString ()
    {
        return value!=null?value.toString():"";
    }

    /**
     * Creates a MaybeUUID from a string.
     * 
     * @param value The string representation.
     * @return The value.
     */
    public static MaybeUUID fromString (String value)
    {
        if (JanusHelper.isBlank(value))
            return empty();
        else
            return of (UUID.fromString(value));
    }
}
