package com.sipstacks.xhml;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by torrey on 27/04/16.
 */
public class Slack {
    String convert(XHTMLObject obj) {
        JSONObject jsonRoot = new JSONObject();
        JSONArray attachments = new JSONArray();
        for(int i = 0; i < obj.objects.getLength(); i++ ) {
            Node item = obj.objects.item(i);
            if(item.getNodeType() == Node.TEXT_NODE) {
                JSONObject o = new JSONObject();
                o.put("text", item.getTextContent());
                attachments.add(o);
            } else {
                switch(item.getNodeName()) {
                    case "span": {
                        String style = item.getAttributes().getNamedItem("style").getTextContent();
                        System.err.println("Got Style: " + style);
                        Pattern pattern = Pattern.compile("color:\\s+([#0-9a-zA-Z]+);");
                        Matcher matcher = pattern.matcher(style);
                        if (!matcher.matches()) {
                            break;
                        }
                        String color = matcher.group(1);
                        JSONObject o;
                        boolean isNew = false;
                        if (attachments.size() > 0) {
                            o = (JSONObject) attachments.get(attachments.size() - 1);
                        } else {
                            o = new JSONObject();
                            isNew = true;
                        }
                        o.put("color", color);
                        String text = (String) o.get("text");
                        if (text == null) {
                            text = "";
                        } else {
                            text += " ";
                        }
                        o.put("text", text + item.getFirstChild().getTextContent());
                        if (isNew) {
                            attachments.add(o);
                        }
                        break;
                    }
                    case "img": {
                        String img = item.getAttributes().getNamedItem("src").getTextContent();
                        if (img != null) {
                            JSONObject o = new JSONObject();
                            o.put("text", "");
                            o.put("image_url", img);
                            attachments.add(o);
                        }
                        break;
                    }
                    default:
                        // ignore the markup
                        JSONObject o = new JSONObject();
                        o.put("text", item.getFirstChild().getTextContent());
                        attachments.add(o);


                }
            }

        }
        jsonRoot.put("attachments", attachments);
        return jsonRoot.toJSONString();
    }
}
