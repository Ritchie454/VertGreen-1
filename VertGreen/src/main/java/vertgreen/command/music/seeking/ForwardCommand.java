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

package vertgreen.command.music.seeking;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandRestricted;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.perms.PermissionLevel;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class ForwardCommand extends Command implements IMusicCommand, ICommandRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.getExisting(guild);

        if(player == null || player.isQueueEmpty()) {
            TextUtils.replyWithName(channel, invoker, I18n.get(guild).getString("unpauseQueueEmpty"));
            return;
        }

        if(args.length == 1) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        long t;
        try {
            t = TextUtils.parseTimeString(args[1]);
        } catch (IllegalStateException e){
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        AudioTrackContext atc = player.getPlayingTrack();
        AudioTrack at = atc.getTrack();

        //Ensure bounds
        t = Math.max(0, t);
        t = Math.min(atc.getEffectiveDuration(), t);

        at.setPosition(at.getPosition() + t);
        channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("fwdSuccess"), atc.getEffectiveTitle(), TextUtils.formatTime(t))).queue();
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} [[hh:]mm:]ss\n#";
        String example = "  {0}{1} 2:30";
        return usage + I18n.get(guild).getString("helpForwardCommand") + example;
    }

    @Override
    public PermissionLevel getMinimumPerms() {
        return PermissionLevel.DJ;
    }
}
