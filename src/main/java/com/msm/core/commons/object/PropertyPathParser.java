package com.msm.core.commons.object;

import java.util.ArrayList;
import java.util.List;

public class PropertyPathParser {

    public static List<PropertyToken> parse(String path) {

        List<PropertyToken> tokens = new ArrayList<>();

        for (String part : path.split("\\.")) {

            if (!part.contains("[")) {
                tokens.add(new PropertyToken(part));
                continue;
            }

            String name = part.substring(0, part.indexOf("["));
            String inside = part.substring(part.indexOf("[") + 1, part.indexOf("]"));

            PropertyToken token = new PropertyToken(name);

            if (inside.startsWith("'")) {
                token.setKey(inside.replace("'", ""));
            } else {
                token.setIndex(Integer.parseInt(inside));
            }

            tokens.add(token);
        }

        return tokens;
    }
}