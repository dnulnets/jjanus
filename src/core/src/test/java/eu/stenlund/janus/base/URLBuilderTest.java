package eu.stenlund.janus.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class URLBuilderTest {
    
    @Test
    public void testBuild1()
    {
        Assertions.assertEquals("", URLBuilder.root().build());
    }

    @Test
    public void testBuild2()
    {
        Assertions.assertEquals("janus", URLBuilder.root("janus").build());
    }

    @Test
    public void testBuild3()
    {
        String str = URLBuilder.root().addSegment("janus").build();
        Assertions.assertEquals("janus", str);
    }

    @Test
    public void testBuild4()
    {
        String str = URLBuilder.root().addSegment("janus").addSegment("a").addSegment("b").build();
        Assertions.assertEquals("janus/a/b", str);
    }

    @Test
    public void testBuild5()
    {
        String str = URLBuilder.root()
            .addSegment("janus")
            .addSegment("a")
            .addSegment("b")
            .addQueryParameter("p1", "test1")
            .build();

        Assertions.assertEquals("janus/a/b?p1=test1", str);
    }

    @Test
    public void testBuild6()
    {
        String str = URLBuilder.root()
            .addSegment("janus")
            .addSegment("a")
            .addSegment("b")
            .addQueryParameter("p1", "test1")
            .addQueryParameter("p2", "&=")
            .build();

        Assertions.assertEquals("janus/a/b?p1=test1&p2=%26%3D", str);
    }
}
