/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package vertgreen.command.admin;

import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommand;
import vertgreen.commandmeta.abs.ICommandRestricted;
import vertgreen.perms.PermissionLevel;
import vertgreen.util.log.SLF4JInputStreamErrorLogger;
import vertgreen.util.log.SLF4JInputStreamLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CompileCommand extends Command implements ICommand, ICommandRestricted {

    private static final Logger log = LoggerFactory.getLogger(CompileCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            EmbedBuilder eb = new EmbedBuilder();
            EmbedBuilder eb2 = new EmbedBuilder();
            Runtime rt = Runtime.getRuntime();
            Message msg;
            msg = channel.sendMessage("Compiling Update...").complete(true);
            eb.setTitle("<:ntools:336195783735115776> Packaging VertGreen.jar");
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
            eb2.setTitle(":white_check_mark: Succeeded packaging VertGreen.jar");
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
        return "{0}{1} [branch [repo]]\n#Compile the Downloaded Update. Does not restart the bot.";
    }

    @Override
    public PermissionLevel getMinimumPerms() {
        return PermissionLevel.BOT_OWNER;
    }
}
