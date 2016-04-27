package com.sipstacks.xhml;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
/**
 * Created by torrey on 27/04/16.
 */
public class XHTMLObject {
    NodeList objects;
    public void parse(String bodyPart) throws XHtmlConvertException {
        if(bodyPart == null ){
            throw new IllegalArgumentException("BodyPart cannot be null");
        }
        Document document;
        try{
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final String wrappedMessage = "<html><body>"+bodyPart+"</body></html>";
            document = parser.parse(new InputSource(new StringReader(wrappedMessage)));
        }catch (ParserConfigurationException | SAXException | IOException e  ) {
            throw new XHtmlConvertException("Invalid XHTML",e);
        }
        objects = document.getFirstChild().getFirstChild().getChildNodes();
    }
}
