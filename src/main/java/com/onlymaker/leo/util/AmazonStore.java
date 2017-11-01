package com.onlymaker.leo.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jibo on 2016/10/18.
 */

public class AmazonStore {
    public static final Map<String, String> STORES = new HashMap<String, String>() {{
        put("AODE", "https://www.amazon.de/dp/");
        put("AKEU", "https://www.amazon.de/dp/");
        put("AHUS", "https://www.amazon.com/dp/");
        put("ACUS", "https://www.amazon.com/dp/");
    }};
}
