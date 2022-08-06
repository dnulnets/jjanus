package eu.stenlund.janus.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JanusHelperTest {
    
    @ParameterizedTest
    @ValueSource(strings = { "", "  "})
    public void testIsBlank(String str)
    {
        Assertions.assertTrue(JanusHelper.isBlank(str));
    }

    @Test
    public void testIsBlankNull()
    {
        Assertions.assertTrue(JanusHelper.isBlank(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "tomas", "p",})
    public void testIsNotBlank(String str)
    {
        Assertions.assertFalse(JanusHelper.isBlank(str));
    }

}
