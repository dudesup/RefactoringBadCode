package com.cd.bad.code;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.List;

public class FolderElement implements TocElement {

    @Override
    public String processElement(String xPathExpression, Element element) {
        List<Attribute> attributes = element.attributes();
        String titleAttrContent = element.attributeValue("title");
        String fileAttrContent = element.attributeValue("file");

        String jsonString = "{";
        jsonString = processFolderAttributes(xPathExpression, element, jsonString, attributes, titleAttrContent, fileAttrContent);
        jsonString = jsonString.concat("},");

        return jsonString;
    }

    private String processFolderAttributes(String xPathExpression, Element element, String jsonString,
                                           List<Attribute> attributes, String titleAttrContent, String fileAttrContent) {
        for (Attribute attribute : attributes) {
            String attrName = attribute.getName();
            jsonString = jsonString.concat("'data':'").concat(titleAttrContent).concat("',");

            if (attrName.equals("key")) {
                jsonString = processKeyAttribute(xPathExpression, element, jsonString, fileAttrContent);
                break;
            } else if (attrName.equals("type")) {
                jsonString = processTypeAttribute(xPathExpression, element, jsonString);
                break;
            }
        }
        return jsonString;
    }

    private String processTypeAttribute(String xPathExpression, Element element, String jsonString) {
        String typeContent = element.attributeValue("type");

        if (typeContent == "history") {
            jsonString = jsonString.concat("'attr':{'id':'").concat(xPathExpression).concat("_fth,");
        }
        return jsonString;
    }

    private String processKeyAttribute(String xPathExpression, Element element, String jsonString,
                                       String fileAttrContent) {
        String keyContent = element.attributeValue("key");
        jsonString = jsonString.concat("'attr':{'id':'").concat(xPathExpression).concat("_fk:")
                .concat(keyContent).concat("'}");
        if (fileAttrContent != null) {
            jsonString = jsonString.concat("','file':'").concat(fileAttrContent).concat("'}");
        }
        return jsonString;
    }
}
