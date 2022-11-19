package com.cd.bad.code;

import org.approvaltests.Approvals;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class XMLToJsonTest {
    @Test
    public void shouldTranslateEmptyXMLToJson() throws Exception {
        XMLToJson translate = new XMLToJson();

        URL url = new URL("file:./src/test/resources/toc.xml");
        String xPathString = "fk:AMM24_fk:AMM24-00-00_fk:AMM24-00-00-02";

        Approvals.verify(translate.getJson(url, xPathString));
    }

    @Test
    public void shouldParseEmptyFolder() throws Exception {
        final XMLToJson translate = new XMLToJson();
        final Document document = documentFor("<folder></folder>");
        assertEquals("[]", translate.getJsonFromDocument("/", document));
    }

    @Test
    public void shouldParseEmptyDocument() throws Exception {
        final XMLToJson translate = new XMLToJson();
        final Document document = documentFor("<folder><doc/></folder>");
        assertEquals("[{}]", translate.getJsonFromDocument("/", document));
    }

    @Test
    public void shouldParseDocumentCorrectly() throws Exception {
        final XMLToJson translate = new XMLToJson();
        final Document document = documentFor("<folder><doc title=\"test\"/></folder>");
        assertEquals("[{'data':'test'}]", translate.getJsonFromDocument("/", document));
    }

    private Document documentFor(String xml) throws DocumentException {
        var reader = new SAXReader();
        return reader.read(new ByteArrayInputStream(xml.getBytes()));
    }
}
