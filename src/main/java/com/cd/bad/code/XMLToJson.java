package com.cd.bad.code;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * fk--folder key,dk--doc key -->value is key string
 * dt--doc type,ft--folder type -->both have 19 key options:
 * bom tr amend history help info loeid cust cust0-cust9 custhist
 * folder type only seen as "history" in toc,why ??do we use other ones? -- it only has one child
 * doc type only seen two "tr" and "history" in toc??-- it only has one child
 * node with "folder type="history"" is the only child of it parent either "<doc type="tr" key="xxx" trnum="xxx"....." or "<doc type="tr"  trnum="xxx""
 * <folder type="history"  could has more then one doc children .e.g. title="History of AMM31-32-00-720-807" in 700/amm
 * <p>
 * <p>
 * <p>
 * folder element has the follwing to identify itself:
 * 1, key
 * 2, type="history",  in this case, folder is the only child of doc element with type ="tr"?????
 * <p>
 * doc element has the following to identify itself
 * 1, key
 * 2, type="tr" trnum="xxxxx"
 * 3, type="history", in this case, doc isthe only child of a folder element???
 * <p>
 * <p>
 * <p>
 * the return json format likes following:
 * [
 * { "data" : "A node", "children" , "state" : "open" },
 * { "data" : "Only child", "state" : "closed" },
 * "Ajax node"
 * ]
 */

public class XMLToJson {
    private static final String EMPTY_STRING = "";
    private static final Map<String, TocElement> ELEMENTS;

    static {
        ELEMENTS = new HashMap<>();
        ELEMENTS.put("folder", new FolderElement());
        ELEMENTS.put("doc", new DocElement());
    }

    private XPathProvider xPathProvider;

    public XMLToJson() {
        this(new TocQueryExpressions());
    }

    public XMLToJson(XPathProvider xPathProvider) {
        this.xPathProvider = xPathProvider;
    }

    public String getJson(URL url, String xPathString) throws Exception {
        Document TOCDoc = getDocument(url);
        return getJsonFromDocument(xPathString, TOCDoc);
    }

    public String getJsonFromDocument(String xPathString, Document TOCDoc) throws Exception {
        String jsonString = "[";

        Element node = getNode(xPathString, TOCDoc);

        for (Iterator<Element> i = node.elementIterator(); i.hasNext(); ) {
            jsonString = jsonString.concat(getProcessedElement(xPathString, i.next()));
        }

        return getClosedJson(jsonString);
    }

    private Document getDocument(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(url);
    }

    private String getClosedJson(String jsonString) {
        if (jsonString.length() > 1) {
            jsonString = jsonString.substring(0, jsonString.length() - 1);
        }
        jsonString = jsonString.concat("]");
        return jsonString;
    }

    private String getProcessedElement(String xPathExpression, Element element) {
        String elementName = element.getName();
        if (ELEMENTS.containsKey(elementName)) {
            return ELEMENTS.get(elementName).processElement(xPathExpression, element);
        }
        return EMPTY_STRING;
    }

    private Element getNode(String xPathString, Document TOCDoc) throws Exception {
        if (xPathString.equals("/")) {
            return TOCDoc.getRootElement();
        } else {
            String realXPathString = this.xPathProvider.xPathFrom(xPathString);
            return (Element) TOCDoc.selectSingleNode(realXPathString);
        }
    }
}