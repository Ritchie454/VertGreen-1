package vertgreen.feature;

import vertgreen.feature.I18n;
import vertgreen.ProvideJDASingleton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class I18nTest extends ProvideJDASingleton {

    private static final Logger log = LoggerFactory.getLogger(I18nTest.class);

    @AfterAll
    public static void postStats() {
        saveClassStats(I18nTest.class.getSimpleName());
    }

    @Test
    public void testTranslatedStrings() {
        I18n.start();

        ResourceBundle id_ID = I18n.LANGS.get("en_US").getProps();
        for(String key :  I18n.DEFAULT.getProps().keySet()){
            Assertions.assertNotNull(id_ID.getString(key), () -> key + " prop missing in language files");
        }
        bumpPassedTests();
    }
}
