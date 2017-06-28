package vertgreen.feature;

import vertgreen.db.DatabaseNotReadyException;
import vertgreen.db.EntityReader;
import vertgreen.db.EntityWriter;
import vertgreen.db.entity.GuildConfig;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

    private static final Logger log = LoggerFactory.getLogger(I18n.class);

    public static VertGreenLocale DEFAULT = new VertGreenLocale(new Locale("en","US"), "en_US", "English");
    public static final HashMap<String, VertGreenLocale> LANGS = new HashMap<>();

    public static void start() {
        LANGS.put("en_US", DEFAULT);

        LANGS.put("en_PT", new VertGreenLocale(new Locale("en", "PT"), "en_PT", "Pirate English"));
        LANGS.put("en_TS", new VertGreenLocale(new Locale("en", "TS"), "en_TS", "Tsundere English"));

        log.info("Loaded " + LANGS.size() + " languages: " + LANGS);
    }

    public static ResourceBundle get(Guild guild) {
        if (guild == null) {
            return DEFAULT.getProps();
        }
        return getLocale(guild).getProps();
    }

    public static VertGreenLocale getLocale(Guild guild) {
        GuildConfig config;

        try {
            config = EntityReader.getGuildConfig(guild.getId());
        } catch (DatabaseNotReadyException e) {
            //don't log spam the full exceptions or logs
            return DEFAULT;
        } catch (Exception e) {
            log.error("Error when reading entity", e);
            return DEFAULT;
        }

        return LANGS.getOrDefault(config.getLang(), DEFAULT);
    }

    public static void set(Guild guild, String lang) throws LanguageNotSupportedException {
        if (!LANGS.containsKey(lang))
            throw new LanguageNotSupportedException("Language not found");

        GuildConfig config = EntityReader.getGuildConfig(guild.getId());
        config.setLang(lang);
        EntityWriter.mergeGuildConfig(config);
    }

    public static class VertGreenLocale {

        private final Locale locale;
        private final String code;
        private final ResourceBundle props;
        private final String nativeName;

        VertGreenLocale(Locale locale, String code, String nativeName) throws MissingResourceException {
            this.locale = locale;
            this.code = code;
            props = ResourceBundle.getBundle("lang." + code, locale);
            this.nativeName = nativeName;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getCode() {
            return code;
        }

        public ResourceBundle getProps() {
            return props;
        }

        public String getNativeName() {
            return nativeName;
        }

        @Override
        public String toString() {
            return "[" + nativeName + ']';
        }
    }

    public static class LanguageNotSupportedException extends Exception {
        public LanguageNotSupportedException(String message) {
            super(message);
        }
    }

}
