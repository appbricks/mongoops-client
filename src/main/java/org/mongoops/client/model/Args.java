package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unchecked")
public class Args
    extends HashMap<String, Object> {

    @Override
    public Object get(Object key) {

        String k = (String) key;
        StringTokenizer st = new StringTokenizer(k, ".");

        Map<String, Object> argMap = this;

        while (argMap != null && st.hasMoreTokens()) {

            k = st.nextToken();
            if (st.hasMoreTokens()) {
                argMap = (Map) argMap.get(k);
            }
        }

        if (argMap == null) {
            return null;
        } if (this == argMap) {
            return super.get(k);
        } else {
            return argMap.get(k);
        }
    }

    @Override
    public Object put(String key, Object value) {

        String k = key;
        StringTokenizer st = new StringTokenizer(k, ".");

        Map<String, Object> argMap = this;
        Map<String, Object> argMapNext;

        while (st.hasMoreTokens()) {

            k = st.nextToken();
            if (st.hasMoreTokens()) {
                argMapNext = (Map) argMap.get(k);
                if (argMapNext == null) {
                    argMapNext = new HashMap<>();
                    argMap.put(k, argMapNext);
                }
                argMap = argMapNext;
            }
        }

        if (this == argMap) {
            return super.put(k, value);
        } else {
            return argMap.put(k, value);
        }
    }
}
