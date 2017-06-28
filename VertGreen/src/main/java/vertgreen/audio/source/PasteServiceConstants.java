package vertgreen.audio.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PasteServiceConstants {

    static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("(?:([a-z0-9]+(?:-[a-z0-9]+)*)\\.)+[a-z]{2,}");

    static final Pattern HASTEBIN_PATTERN = Pattern
            .compile("^(?:(?:https?://)?(?:www\\.)?)?hastebin\\.com/(?:raw/)?(\\w+)(?:\\..+)?$");

    static final Pattern PASTEBIN_PATTERN = Pattern
            .compile("^(?:(?:https?://)?(?:www\\.)?)?pastebin\\.com/(?:raw/)?(\\w+)(?:\\..+)?$");

    static final Map<String, String> PASTE_SERVICE_URLS;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("hastebin", "http://hastebin.com/raw/");
        m.put("pastebin", "http://pastebin.com/raw/");
        PASTE_SERVICE_URLS = Collections.unmodifiableMap(m);
    }

}
