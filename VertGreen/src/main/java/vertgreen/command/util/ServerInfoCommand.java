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

package vertgreen.command.util;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.constant.BotConstants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by midgard/Chromaryu/knight-ryu12 on 17/01/18.
 */
public class ServerInfoCommand extends Command implements IUtilCommand {
    EmbedBuilder eb;
    Random rand = new Random();
    int n = rand.nextInt(13);
    int i = 0;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        ResourceBundle rb = I18n.get(guild);
        pickColor();
        eb.setTitle(MessageFormat.format(I18n.get(guild).getString("serverinfoTitle"),guild.getName()), null);
        eb.setThumbnail(guild.getIconUrl());
        guild.getMembers().stream().filter((u) -> (u.getOnlineStatus() != OnlineStatus.OFFLINE)).forEachOrdered((_item) -> {
            i++;
        });
        eb.addField(rb.getString("serverinfoOnlineUsers"), String.valueOf(i),true);
        eb.addField(rb.getString("serverinfoTotalUsers"), String.valueOf(guild.getMembers().size()),true);
        eb.addField(rb.getString("serverinfoRoles"), String.valueOf(guild.getRoles().size()),true);
        eb.addField(rb.getString("serverinfoText"), String.valueOf(guild.getTextChannels().size()),true);
        eb.addField(rb.getString("serverinfoVoice"), String.valueOf(guild.getVoiceChannels().size()),true);
        eb.addField(rb.getString("serverinfoCreationDate"), guild.getCreationTime().format(dtf),true);
        eb.addField(rb.getString("serverinfoVLv"), guild.getVerificationLevel().name(),true);
        eb.addField(rb.getString("serverinfoOwner"), guild.getOwner().getAsMention(),true);
        eb.addField("Region", guild.getRegion().toString(), true);
        eb.setFooter(channel.getJDA().getSelfUser().getName(), channel.getJDA().getSelfUser().getAvatarUrl());
        channel.sendMessage(eb.build()).queue();

    }

    public void pickColor(){
        switch (n) {
            case 1:
                eb.setColor(Color.BLACK);
                break;
            case 2:
                eb.setColor(Color.BLUE);
                break;
            case 3:
                eb.setColor(Color.CYAN);
                break;
            case 4:
                eb.setColor(Color.DARK_GRAY);
                break;
            case 5:
                eb.setColor(Color.GRAY);
                break;
            case 6:
                eb.setColor(Color.GREEN);
                break;
            case 7:
                eb.setColor(Color.LIGHT_GRAY);
                break;
            case 8:
                eb.setColor(Color.MAGENTA);
                break;
            case 9:
                eb.setColor(Color.ORANGE);
                break;
            case 10:
                eb.setColor(Color.PINK);
                break;
            case 11:
                eb.setColor(Color.RED);
                break;
            case 12:
                eb.setColor(Color.WHITE);
                break;
            case 13:
                eb.setColor(Color.YELLOW);
                break;
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpServerInfoCommand");
    }
}
