package eu.stenlund.janus.base;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class URLBuilder {

    private List<Parameter> parameters;
    private List<String> segments;

    private class Parameter {

        public String name;
        public String value;

        public Parameter(String name, String value)
        {
            this.name = name;
            this.value = value;
        }
    }

    public String build()
    {
        StringBuilder str = new StringBuilder();
        str.append(segments.stream().collect(Collectors.joining("/")));
        if(!parameters.isEmpty()) {
            str.append("?");
            str.append(parameters.stream()
                .map(p->p.name + "=" + URLEncoder.encode(p.value, Charset.defaultCharset()))
                .collect(Collectors.joining("&")));
        }
        return str.toString();
    }

    public URLBuilder addSegment (String segment)
    {
        segments.add(segment);
        return this;
    }

    public URLBuilder addQueryParameter(String name, String value)
    {
        parameters.add(new Parameter(name, value));
        return this;
    }

    private URLBuilder() {
        parameters = new ArrayList<Parameter>();
        segments = new ArrayList<String>();
    }

    private URLBuilder(String path) {
        parameters = new ArrayList<Parameter>();
        segments = new ArrayList<String>();
        segments.add(path);
    }

    public static URLBuilder root()
    {
        return new URLBuilder();
    }

    public static URLBuilder root(String root)
    {
        return new URLBuilder(root);
    }

}
