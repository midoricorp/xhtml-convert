package com.sipstacks.xhml;

import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
        }
        XHTMLObject obj = new XHTMLObject();
        obj.parse(sb.toString());
        System.err.println("Got:");
        System.err.println(sb.toString());
        System.err.println("***********");
        obj.parse(sb.toString());
        Slack slack = new Slack();
        System.err.println(slack.convert(obj));

    }
}
