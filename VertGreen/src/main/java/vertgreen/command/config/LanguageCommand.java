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

package vertgreen.command.config;

import vertgreen.Config;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IModerationCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LanguageCommand extends Command implements IModerationCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(args.length != 2) {
            handleNoArgs(guild, channel, invoker, message, args);
            return;
        }

        if (!invoker.hasPermission(Permission.ADMINISTRATOR)
                && !DiscordUtil.isUserBotOwner(invoker.getUser())){
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configNotAdmin"), invoker.getEffectiveName())).queue();
            return;
        }

        //Assume proper usage and that we are about to set a new language
        try {
            I18n.set(guild, args[1]);
        } catch (I18n.LanguageNotSupportedException e) {
            TextUtils.replyWithName(channel, invoker, MessageFormat.format(I18n.get(guild).getString("langInvalidCode"), args[1]));
            return;
        }

        TextUtils.replyWithName(channel, invoker, MessageFormat.format(I18n.get(guild).getString("langSuccess"), I18n.getLocale(guild).getNativeName()));
    }

    private void handleNoArgs(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        eb.setThumbnail("https://d30y9cdsu7xlg0.cloudfront.net/png/51904-200.png");
        eb.addField("Languages available to " + guild.getName(), I18n.get(guild).getString("langInfo").replace(Config.DEFAULT_PREFIX, Config.CONFIG.getPrefix()), true); 
        List<String> keys = new ArrayList<>(I18n.LANGS.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            I18n.VertGreenLocale loc = I18n.LANGS.get(key);
            eb.addField(loc.getNativeName(), loc.getCode(), true);
        }     
        eb.setFooter(" | Disclaimer | Translations may not be 100% accurate or complete.", "https://www.spiralscripts.co.uk/images/stories/warning-medium.png");
        channel.sendMessage(eb.build()).queue(); 
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} OR {0}{1} <code>\n#";
        return usage + I18n.get(guild).getString("helpLanguageCommand");
    }
}
