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

import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommand;
import vertgreen.commandmeta.abs.ICommandRestricted;
import vertgreen.perms.PermissionLevel;
import vertgreen.util.constant.ExitCodes;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotRestartCommand extends Command implements ICommand, ICommandRestricted {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BotRestartCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        Runtime rt = Runtime.getRuntime();
        String started;
        try {
            channel.sendMessage("Launching new process...").queue();
            rt.exec("./start.sh");
            started = "true";
        } catch (IOException e) {
            log.warn("Unable to start new process", e);
            channel.sendMessage("```\nWarning!\n Unable to start new process..\n```").queue();
            started = "false";
        }
        if (started.equals("true")){
            channel.sendMessage("Killing old process...").queue();
            VertGreen.shutdown(ExitCodes.EXIT_CODE_RESTART);
        } else if (started.equals("false")){
            channel.sendMessage("Aborted restart process...").queue();
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Restarts the bot.";
    }

    @Override
    public PermissionLevel getMinimumPerms() {
        return PermissionLevel.BOT_ADMIN;
    }
}
