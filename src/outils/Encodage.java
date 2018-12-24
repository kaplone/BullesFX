package outils;

import org.apache.commons.text.StringEscapeUtils;

public class Encodage {

    public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&' ) {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else if (c == '\''){
                out.append("&#x");
                out.append(Integer.toHexString((int) c));
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String rebuildString(String s){

        String res = StringEscapeUtils.unescapeHtml4(s);

        return res;
    }
}
