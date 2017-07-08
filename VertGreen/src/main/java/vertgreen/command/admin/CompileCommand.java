package vertgreen.command.admin;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.log.SLF4JInputStreamErrorLogger;
import vertgreen.util.log.SLF4JInputStreamLogger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CompileCommand extends Command implements ICommandOwnerRestricted {
    private static final Logger log = LoggerFactory.getLogger(GitPullCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedBuilder eb2 = new EmbedBuilder();
            Runtime rt = Runtime.getRuntime();
            Message msg;
            msg = channel.sendMessage("Downloading Update...").complete(true);
            eb.setTitle("<:stafftools:314348604095594498>Running `mvn package shade:shade`... ");
            channel.sendMessage(eb.build()).queue();
            File updateDir = new File("update/VertGreen");

            Process mvnBuild = rt.exec("mvn -f " + updateDir.getAbsolutePath() + "/pom.xml package shade:shade");
            new SLF4JInputStreamLogger(log, mvnBuild.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, mvnBuild.getInputStream()).start();

            if (!mvnBuild.waitFor(600, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: mvn package shade:shade");
            } else if (mvnBuild.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }
            eb2.setTitle("<:check:314349398811475968>Succeeded packaging VertGreen.jar");
            channel.sendMessage(eb2.build()).queue();

            if (!new File("./update/VertGreen/target/VertGreen-1.0.jar").renameTo(new File("./VertGreen-1.0.jar"))) {
                throw new RuntimeException("Failed to move jar to home");
            }
        } catch (InterruptedException | IOException | RateLimitedException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public String help(Guild guild) {
        return "<<compile\n#Compile the downloaded update.";
    }
}