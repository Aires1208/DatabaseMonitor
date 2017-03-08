package com.zte.ums.oespaas.mysql.util;

import java.util.*;

/**
 * Created by 10203846 on 9/18/16.
 */
public class MapUtils {

    public static List<Map.Entry<?, ?>> getTopNSortedByMapValueDescend(Map<?, ?> map, int topN) {
        List<Map.Entry<?, ?>> entryList = new ArrayList<Map.Entry<?, ?>>(map.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<?, ?>>() {
            public int compare(Map.Entry<?, ?> o1, Map.Entry<?, ?> o2) {
                if (o1.getValue() instanceof Integer && o2.getValue() instanceof Integer) {
                    return (Integer) o2.getValue() - (Integer) o1.getValue();
                } else if (o1.getValue() instanceof String && o2.getValue() instanceof String) {
                    return ((String) o2.getValue()).compareTo((String) o1.getValue());
                } else {
                    return 0;
                }
            }
        });

        return entryList.subList(0, Math.min(topN, entryList.size()));
    }

}
