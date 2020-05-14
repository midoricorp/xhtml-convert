package com.sipstacks.xhml;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by torrey on 27/04/16.
 */
public class XhtmlCli {
    public static void main(String[]args) throws IOException, XHtmlConvertException {
        BufferedReader stream = new BufferedReader(new InputStreamReader(System.in));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = stream.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        XHTMLObject obj = new XHTMLObject();
        obj.parse(sb.toString());
        System.err.println("Got:");
        System.err.println(sb.toString());
        System.err.println("***********");
        obj.parse(sb.toString());
        Slack slack = new Slack();
        System.err.println(Arrays.toString(slack.convert(obj).toArray()));

        String emojii = null;
        try {
            Emojiify.convert(obj);
            System.err.println("Emojii conversion:");
            System.err.println(obj.getString());
            System.err.println("***********");
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        String text = obj.toText();
        System.err.println("Text Conversion:");
        System.err.println(text);
        System.err.println("***********");

    }
}
