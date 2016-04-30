package com.sipstacks.xhml;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;
import flowctrl.integration.slack.type.Attachment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by torrey on 27/04/16.
 */
public class Slack {
    private static String getStyleAttribute(String style, String attrib) {
        String [] attribs = style.split(";");
        for(String attr : attribs) {
            System.err.println("Got attrib: " + attr);
            Pattern pattern = Pattern.compile(attrib + ":\\s+([#0-9a-zA-Z]+)");
            Matcher matcher = pattern.matcher(attr);
            if (!matcher.matches()) {
                continue;
            }
            return matcher.group(1);
        }
        return null;
    }
    public static List<Attachment> convert(XHTMLObject obj) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        for(int i = 0; i < obj.objects.getLength(); i++ ) {
            Node item = obj.objects.item(i);
            if(item.getNodeType() == Node.TEXT_NODE) {
                Attachment a = null;
                boolean isNew = false;
                if (attachments.size() > 0) {
                    a = attachments.get(attachments.size() - 1);
                } else {
                    a = new Attachment();
                    isNew = true;
                }
                String text = a.getText();
                if (text == null) {
                    text = "";
                } else {
                    text += " ";
                }
                a.setText(text + item.getTextContent());
                if (isNew) {
                    System.err.println("Adding new text attachment");
                    attachments.add(a);
                }
            } else {
                switch(item.getNodeName()) {
                    case "span": {
                        Attachment a;
                        boolean isNew = false;
                        if (attachments.size() > 0) {
                            a = attachments.get(attachments.size() - 1);
                        } else {
                            a = new Attachment();
                            isNew = true;
                        }

                        Node styleNode = item.getAttributes().getNamedItem("style");
                        if (styleNode != null) {
                            String style = styleNode.getTextContent();
                            System.err.println("Got Style: " + style);

                            String color = getStyleAttribute(style, "color");
                            if (color != null) {
                                a.setColor(color);
                                System.err.println("Setting color on " + (isNew ? "new node" : "old node"));
                            }
                        }


                        String text = a.getText();
                        if (text == null) {
                            text = "";
                        } else {
                            text += " ";
                        }
                        a.setText(text + item.getFirstChild().getTextContent());
                        if (isNew) {
                            System.err.println("Adding new span attachment");
                            attachments.add(a);
                        }
                        break;
                    }
                    case "img": {
                        String img = item.getAttributes().getNamedItem("src").getTextContent();
                        if (img != null) {
                            Attachment a = new Attachment();
                            a.setText("");
                            a.setImage_url(img);
                            attachments.add(a);
                        }
                        break;
                    }
                    case "br": {
                        Attachment a = new Attachment();
                       attachments.add(a);

                    }
                    default:
                        // ignore the markup
                        if (item.getFirstChild() != null) {
                            Attachment a = new Attachment();
                            a.setText(item.getFirstChild().getTextContent());
                            attachments.add(a);
                        }


                }
            }

        }
        return attachments;
    }
}
