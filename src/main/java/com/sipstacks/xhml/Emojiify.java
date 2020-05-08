package com.sipstacks.xhml;

import emoji4j.EmojiUtils;
import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;




/**
 * Created by torrey on 27/04/16.
 */
public class Emojiify {

    public static String convert(XHTMLObject obj) throws TransformerException {

        NodeList nodeList = obj.objects;
        convertNodeList(nodeList);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        for(int i = 0; i < nodeList.getLength(); i++ ) {
            Node item = nodeList.item(i);
            transformer.transform(new DOMSource(item), new StreamResult(writer));
        }
        return writer.toString();
    }

    private static void convertNodeList(NodeList nodeList) {
        for(int i = 0; i < nodeList.getLength(); i++ ) {
            Node item = nodeList.item(i);
            if(item.getNodeType() == Node.TEXT_NODE) {
                String newBody = EmojiUtils.emojify(item.getTextContent());
                item.setNodeValue(newBody);
            } else {
                NodeList childNodes = item.getChildNodes();
                if(childNodes != null) convertNodeList(childNodes);
            }
        }
    }
}
