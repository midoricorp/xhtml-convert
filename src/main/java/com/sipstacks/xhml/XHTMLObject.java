package com.sipstacks.xhml;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by torrey on 27/04/16.
 */
public class XHTMLObject {
    public NodeList objects;
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

    private String getString() throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        for(int i = 0; i < objects.getLength(); i++ ) {
            Node item = objects.item(i);
            transformer.transform(new DOMSource(item), new StreamResult(writer));
        }
        return writer.toString();
    }
}
