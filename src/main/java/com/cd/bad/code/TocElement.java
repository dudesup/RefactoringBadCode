package com.cd.bad.code;

import org.dom4j.Element;

public interface TocElement {
    String processElement(String xPathString, Element elem);
}
