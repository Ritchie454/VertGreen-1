package vertgreen.commandmeta;

import vertgreen.commandmeta.CommandRegistry;
import vertgreen.ProvideJDASingleton;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.init.MainCommandInitializer;
import vertgreen.commandmeta.init.MusicCommandInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandInitializerTest extends ProvideJDASingleton {

    @AfterAll
    public static void saveStats() {
        saveClassStats(CommandInitializerTest.class.getSimpleName());
    }
    
    @Test
    public void testHelpStrings() {
//        Assumptions.assumeFalse(isTravisEnvironment(), () -> "Aborting test: Travis CI detected");

        MainCommandInitializer.initCommands();
        MusicCommandInitializer.initCommands();

        for (String c : CommandRegistry.getRegisteredCommandsAndAliases()) {
            Command com = CommandRegistry.getCommand(c).command;

            String help = com.help(null); //sending no guild should have i18n fall back to the default
            Assertions.assertNotNull(help, () -> com.getClass().getName() + ".help() returns null");
            Assertions.assertNotEquals("", help, () -> com.getClass().getName() + ".help() returns an empty string");
        }

        bumpPassedTests();
    }
}
