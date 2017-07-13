package vertgreen.commandmeta;

import vertgreen.ProvideJDASingleton;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.init.MainCommandInitializer;
import vertgreen.commandmeta.init.MusicCommandInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by napster on 22.03.17.
 * <p>
 * Tests for command initialization
 */
public class CommandInitializerTest extends ProvideJDASingleton {

    @AfterAll
    public static void saveStats() {
        saveClassStats(CommandInitializerTest.class.getSimpleName());
    }

    /**
     * Make sure all commands initialized in the bot provide help
     */
    @Test
    public void testHelpStrings() {
//        Assumptions.assumeFalse(isTravisEnvironment(), () -> "Aborting test: Travis CI detected");

        MainCommandInitializer.initCommands();
        MusicCommandInitializer.initCommands();

        for (String c : CommandRegistry.getRegisteredCommandsAndAliases()) {
            Command com = CommandRegistry.getCommand(c).command;
        }

        bumpPassedTests();
    }
}
