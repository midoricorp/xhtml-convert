package com.sipstacks.xhml;

import emoji4j.EmojiUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;
import allbegray.slack.type.Attachment;
import org.w3c.dom.NodeList;

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
    static List<String> markdown_in = new ArrayList<String>();
    static {
        markdown_in.add("text");
    }

    public static void convert(List<Attachment> attachments, NodeList objects) {
        for(int i = 0; i < objects.getLength(); i++ ) {
            Node item = objects.item(i);
            if(item.getNodeType() == Node.TEXT_NODE) {
                if(item.getTextContent().replaceAll("\\s+", "").length() == 0) {
                    continue;
                }

                Attachment a = null;
                if (attachments.size() > 0) {
                    a = attachments.get(attachments.size() - 1);
                } else {
                    a = new Attachment();
                    System.err.println("Adding new text attachment");
                    attachments.add(a);
                }
                String text = a.getText();
                if (text == null) {
                    text = "";
                } else {
                    text += " ";
                }
                String newText = EmojiUtils.emojify(item.getTextContent());
                a.setText(text + newText);
            } else {
                switch(item.getNodeName()) {
                    case "span":
                    case "font": {
                        Attachment a;
                        if (attachments.size() > 0) {
                            a = attachments.get(attachments.size() - 1);
                        } else {
                            a = new Attachment();
                            attachments.add(a);
                            System.err.println("Adding new span/font attachment");
                        }

                        Node styleNode = item.getAttributes().getNamedItem("style");
                        if (styleNode != null) {
                            String style = styleNode.getTextContent();
                            System.err.println("Got Style: " + style);

                            String color = getStyleAttribute(style, "color");
                            if (color != null) {
                                if(!color.startsWith("#")) {
                                    String hex = ColorMapper.getHex(color);
                                    if (hex != null) {
                                        color = hex;
                                    }
                                }
                                a.setColor(color);
                                System.err.println("Setting color " + color + " on node");
                            }
                        } else {
                            Node colorNode = item.getAttributes().getNamedItem("color");
                            if (colorNode != null) {
                                String color = colorNode.getTextContent();
                                if (color != null) {
                                    if(!color.startsWith("#")) {
                                        String hex = ColorMapper.getHex(color);
                                        if (hex != null) {
                                            color = hex;
                                        }
                                    }
                                    a.setColor(color);
                                    System.err.println("Setting color " + color + " on node");
                                }
                            }
                        }

                        convert(attachments, item.getChildNodes());

                        break;
                    }
                    case "img": {
                        String img = item.getAttributes().getNamedItem("src").getTextContent();
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
                                System.err.println("adding Image url " + img);
                                attachments.add(a);
                            }
                            a.setImage_url(img);
                        } else {
                            System.err.println("No src attribute found!");
                        }
                        break;
                    }
                    case "a": {
                        Node hrefItem = item.getAttributes().getNamedItem("href");
                        if(hrefItem == null) {
                            break;
                        }
                        String href = hrefItem.getTextContent();
                        // add the markup
                        Attachment a;

                        if (attachments.size() > 0) {
                            a = attachments.get(attachments.size() - 1);
                        } else {
                            a = new Attachment();
                            attachments.add(a);
                        }
                        convert(attachments, item.getChildNodes());
                        break;
                    }
                    case "b": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;

                            if (attachments.size() > 0) {
                                a = attachments.get(attachments.size() - 1);
                            } else {
                                a = new Attachment();
                                attachments.add(a);
                            }
                            String text = a.getText();
                            if (text == null) {
                                text = "";
                            } else {
                                text += " ";
                            }
                            a.setText(text + "*" + item.getFirstChild().getTextContent() + "*");
                            a.setMrkdwn_in(markdown_in);

                        }
                        break;
                    }
                    case "pre": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
                            if (attachments.size() > 0) {
                                a = attachments.get(attachments.size() - 1);
                            } else {
                                a = new Attachment();
                                attachments.add(a);
                            }
                            String text = a.getText();
                            if (text == null) {
                                text = "";
                            } else {
                                text += " ";
                            }
                            a.setText(text + "```" + item.getFirstChild().getTextContent() + "```");
                            a.setMrkdwn_in(markdown_in);

                        }
                        break;
                    }
                    case "i": {
                        // add the markup
                        if (item.getFirstChild() != null) {
                            Attachment a;
                            if (attachments.size() > 0) {
                                a = attachments.get(attachments.size() - 1);
                            } else {
                                a = new Attachment();
                                attachments.add(a);
                            }
                            String text = a.getText();
                            if (text == null) {
                                text = "";
                            } else {
                                text += " ";
                            }
                            a.setText(text + "_" + item.getFirstChild().getTextContent() + "_");
                            a.setMrkdwn_in(markdown_in);

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
                            convert(attachments, item.getChildNodes());
                        }


                }
            }
        }
    }

    public static List<Attachment> convert(XHTMLObject obj) {
        List<Attachment> attachments = new ArrayList<Attachment>();

        convert(attachments, obj.objects);
        return attachments;
    }
}
