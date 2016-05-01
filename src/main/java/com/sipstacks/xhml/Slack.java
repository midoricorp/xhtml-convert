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
        List<String> markdown_in = new ArrayList<String>();
        markdown_in.add("text");
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
                        boolean isNew = false;
                        Attachment a;
                        if (img != null) {
                            if (attachments.size() > 0 && attachments.get(attachments.size() - 1).getImage_url() == null
                                    && attachments.get(attachments.size() - 1).getText() == null) {
                                a = attachments.get(attachments.size() - 1);
                                if(a.getText() == null) {
                                    a.setText("");
                                }
                            } else {
                                a = new Attachment();
                                a.setText("");
                                isNew = true;
                            }
                            a.setImage_url(img);
                            if (isNew) {

                                System.err.println("adding Image url " + img);
                                attachments.add(a);
                            }
                        } else {
                            System.err.println("No src attribute found!");
                        }
                        break;
                    }
                    case "a": {
                        String href = item.getAttributes().getNamedItem("href").getTextContent();
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
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
                            text += "<" + href;
                            if(item.getFirstChild().getTextContent()!=null) {
                                text += "|" + item.getFirstChild().getTextContent();
                            }
                            text+=">";
                            a.setText(text);
                            a.setMrkdwn_in(markdown_in);
                            if (isNew) {
                                attachments.add(a);
                            }
                        }
                        break;
                    }
                    case "b": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
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
                            a.setText(text + "*" + item.getFirstChild().getTextContent() + "*");
                            a.setMrkdwn_in(markdown_in);
                            if (isNew) {
                                attachments.add(a);
                            }
                        }
                        break;
                    }
                    case "pre": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
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
                            a.setText(text + "```" + item.getFirstChild().getTextContent() + "```");
                            a.setMrkdwn_in(markdown_in);
                            if (isNew) {
                                attachments.add(a);
                            }
                        }
                        break;
                    }
                    case "i": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
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
                            a.setText(text + "_" + item.getFirstChild().getTextContent() + "_");
                            a.setMrkdwn_in(markdown_in);
                            if (isNew) {
                                attachments.add(a);
                            }
                        }
                        break;
                    }
                    case "br": {
                        Attachment a = new Attachment();
                       attachments.add(a);
                        break;
                    }
                    default:
                        // ignore the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
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
                            a.setText(text + item.getFirstChild().getTextContent());
                            if (isNew) {
                                attachments.add(a);
                            }
                        }


                }
            }

        }
        return attachments;
    }
}
