package com.cd.bad.code;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.List;

public class DocElement implements TocElement {

    private static final String EMPTY_DOC = "{}";

    @Override
    public String processElement(String xPathExpression, Element element) {
        List<Attribute> attributes = element.attributes();

        if (attributes.isEmpty()) {
            return EMPTY_DOC.concat(",");
        }

        String titleAttrContent = element.attributeValue("title");
        String fileAttrContent = element.attributeValue("file");
        String jsonString = "";

        jsonString = processDocAttributes(xPathExpression, element, jsonString, attributes, titleAttrContent, fileAttrContent);

        if (hasChildren(element)) {
            jsonString = jsonString.concat(",'state':'closed'");
        }

        jsonString = jsonString.concat("},");

        return jsonString;
    }

    private String processDocAttributes(String xPathExpression, Element element, String jsonString,
                                        List<Attribute> list, String titleAttrContent, String fileAttrContent) {
        for (Attribute attribute : list) {
            jsonString = jsonString.concat("{");
            String attrName = attribute.getName();
            jsonString = jsonString.concat("'data':'").concat(titleAttrContent).concat("',");

            if (attrName.equals("key")) {
                jsonString = processKeyAttribute(xPathExpression, element, jsonString, fileAttrContent);
                break;
            } else if (attrName.equals("trnum")) {
                jsonString = processTrnumAttribute(xPathExpression, element, jsonString, fileAttrContent);
                break;
            }
        }

        jsonString = withTrailingCommaRemoved(jsonString);

        return jsonString;
    }

    private String processTrnumAttribute(String xPathExpression, Element element, String jsonString,
                                         String fileAttrContent) {
        String trnumContent = element.attributeValue("trnum");
        jsonString = jsonString.concat("'attr':{'id':'").concat(xPathExpression).concat("_dtrn:")
                .concat(trnumContent).concat("','file':'").concat(fileAttrContent).concat("'}");
        return jsonString;
    }

    private String processKeyAttribute(String xPathExpression, Element element, String jsonString,
                                       String fileAttrContent) {
        String keyContent = element.attributeValue("key");
        jsonString = jsonString.concat("'attr':{'id':'").concat(xPathExpression).concat("_dk:")
                .concat(keyContent).concat("','file':'").concat(fileAttrContent).concat("'}");
        return jsonString;
    }

    private String withTrailingCommaRemoved(String jsonString) {
        if (jsonString.endsWith(",")) {
            jsonString = jsonString.substring(0, jsonString.length() - 1);
        }
        return jsonString;
    }

    private boolean hasChildren(Element elem) {
        return elem.elements().size() > 0;
    }
}