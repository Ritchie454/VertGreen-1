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

import vertgreen.VertGreen;
import vertgreen.Config;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.ArgumentUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import com.github.goive.steamapi.SteamApi;
import com.github.goive.steamapi.data.SteamApp;
import com.github.goive.steamapi.exceptions.SteamApiException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.LoggerFactory;

public class SteamGameCommand extends Command implements IUtilCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SteamGameCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
    SteamApi steamApi = new SteamApi("US");
    String term = message.getContent().replace(Config.CONFIG.getPrefix() + "steamgame", "");
    List<String> categories = new ArrayList<>();
    List<String> genres = new ArrayList<>();
    StringBuilder catString = new StringBuilder();
    StringBuilder genString = new StringBuilder();
    StringBuilder compatString = new StringBuilder();
    EmbedBuilder eb = new EmbedBuilder();
        try {
            // Fetches information about the steam game including pricing
            SteamApp steamApp = steamApi.retrieve(term); // by name (fuzzy)

            // Use the getters to retrieve data or these convenience methods
            String Name = steamApp.getName().replace(":tm:", "");
            double Price = steamApp.getPrice();
            Currency Cur = steamApp.getPriceCurrency();
            categories = steamApp.getCategories();
            genres = steamApp.getGenres();
            for (String c : categories){
                catString.append(c + "\n");
            }
            for (String g : genres){
                genString.append(g + "\n");
            }
            String url = steamApp.getWebsite();
            String steamUrl = "http://store.steampowered.com/app/" + steamApp.getAppId() + "/";
            String Thumb = steamApp.getHeaderImage();
            boolean Win = steamApp.isAvailableForWindows();
            boolean Mac = steamApp.isAvailableForMac();
            boolean Lin = steamApp.isAvailableForLinux();
            if (Win == true){
                compatString.append("Windows\n");
            }
            if (Mac == true) {
                compatString.append("MacOS\n");
            }
            if (Lin == true) {
                compatString.append("Linux\n");
            }
            eb.setTitle(Name);
            eb.setThumbnail("http://icons.iconarchive.com/icons/danleech/simple/128/steam-icon.png");
            eb.setImage(Thumb);
            eb.addField("Price", Cur.toString() + Price, false);
            eb.addField("Genres", genString.toString(), false);
            eb.addField("Categories", catString.toString(), true);
            eb.addField(Name + " is compatible with:", compatString.toString(), false);
            eb.addField("Website", url, false);
            eb.setFooter(steamUrl, "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Steam_icon_logo.svg/1024px-Steam_icon_logo.svg.png");
            channel.sendMessage(eb.build()).queue();
        } catch (SteamApiException e) {
            // Exception needs to be thrown here in case of invalid appId or service downtime
            log.warn("Steam request blew up", e);
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} <steamgame>\n#Search the steam store for the specified game";
        return usage;
    }
}
