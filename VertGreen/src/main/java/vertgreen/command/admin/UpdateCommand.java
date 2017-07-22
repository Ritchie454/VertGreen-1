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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import vertgreen.VertGreen;
import vertgreen.command.fun.RandomImageCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommand;
import vertgreen.commandmeta.abs.ICommandRestricted;
import vertgreen.perms.PermissionLevel;
import vertgreen.util.GitRepoState;
import vertgreen.util.TextUtils;
import vertgreen.util.constant.ExitCodes;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertgreen.util.log.SLF4JInputStreamErrorLogger;
import vertgreen.util.log.SLF4JInputStreamLogger;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateCommand extends Command implements ICommand, ICommandRestricted {

    private static final Logger log = LoggerFactory.getLogger(UpdateCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Message msg;
            EmbedBuilder eb = new EmbedBuilder();
            EmbedBuilder eb2 = new EmbedBuilder();
            msg = channel.sendMessage("Downloading Update...").complete(true);
            eb.setTitle("<:download:336195782635945984> Downloading Update from Github");
            channel.sendMessage(eb.build()).queue();
            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }
            String githubUser = "Kurozume";
            if (args.length > 2) {
                githubUser = args[2];
            }

            //Clear any old update folder if it is still present
            try {
                Process rm = rt.exec("rm -rf update");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            Process gitClone = rt.exec("git clone https://github.com/" + githubUser + "/VertGreen.git --branch " + branch + " --recursive --single-branch update");
            new SLF4JInputStreamLogger(log, gitClone.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, gitClone.getInputStream()).start();

            if (!gitClone.waitFor(120, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }
            eb2.setTitle(":white_check_mark: Succeeded downloading update from GitHub");
            channel.sendMessage(eb2.build()).queue();

        } catch (InterruptedException | IOException | RateLimitedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#Update the bot by checking out the provided branch from the provided github repo.";
    }

    @Override
    public PermissionLevel getMinimumPerms() {
        return PermissionLevel.BOT_OWNER;
    }
}
