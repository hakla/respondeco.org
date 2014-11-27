package org.respondeco.respondeco.web.rest.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 27/11/14.
 */
public class RestUtil {

    public List<String> splitCommaSeparated(String input) {
        List<String> output = new ArrayList<>();
        if(input != null) {
            for(String s : input.split(",")) {
                s = s.trim();
                if(s.length() > 0) {
                    output.add(s.trim());
                }
            }
        }
        return output;
    }

}
