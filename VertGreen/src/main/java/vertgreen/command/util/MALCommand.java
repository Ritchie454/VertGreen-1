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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.constant.BotConstants;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MALCommand extends Command implements IUtilCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MALCommand.class);
    private static Pattern regex = Pattern.compile("^\\S+\\s+([\\W\\w]*)");

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        Matcher matcher = regex.matcher(message.getContent());

        if (!matcher.find()) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        String term = matcher.group(1).replace(' ', '+').trim();
        log.debug("TERM:" + term);

        //MALs API is currently wonky af, so we are setting rather strict timeouts for its requests
        Unirest.setTimeouts(5000, 10000);
        VertGreen.executor.submit(() -> requestAsync(term, channel, invoker));
        //back to defaults
        Unirest.setTimeouts(10000, 60000);
    }

    private void requestAsync(String term, TextChannel channel, Member invoker) {
        try {
            HttpResponse<String> response = Unirest.get("https://myanimelist.net/api/anime/search.xml")
                    .queryString("q", term)
                    .basicAuth(Config.CONFIG.getMalUser(), Config.CONFIG.getMalPassword())
                    .asString();

            String body = response.getBody();
            if (body != null && body.length() > 0) {
                if (handleAnime(channel, invoker, term, body)) {
                    return;
                }
            }
            response = Unirest.get("http://myanimelist.net/search/prefix.json")
                    .queryString("type", "user")
                    .queryString("keyword", term)
                    .basicAuth(Config.CONFIG.getMalUser(), Config.CONFIG.getMalPassword())
                    .asString();
            body = response.getBody();

            handleUser(channel, invoker, body);
        } catch (UnirestException ex) {
            channel.sendMessage(MessageFormat.format(I18n.get(channel.getGuild()).getString("malNoResults"), invoker.getEffectiveName())).queue();
            log.warn("MAL request blew up", ex);
        }
    }

    private boolean handleAnime(TextChannel channel, Member invoker, String terms, String body) {
        String msg = MessageFormat.format(I18n.get(channel.getGuild()).getString("malRevealAnime"), invoker.getEffectiveName());

        //Read JSON
        log.info(body);
        JSONObject root = XML.toJSONObject(body);
        JSONObject data;
        try {
            data = root.getJSONObject("anime").getJSONArray("entry").getJSONObject(0);
        } catch (JSONException ex) {
            data = root.getJSONObject("anime").getJSONObject("entry");
        }

        ArrayList<String> titles = new ArrayList<>();
        titles.add(data.getString("title"));

        if (data.has("synonyms")) {
            titles.addAll(Arrays.asList(data.getString("synonyms").split(";")));
        }

        if (data.has("english")) {
            titles.add(data.getString("english"));
        }

        int minDeviation = Integer.MAX_VALUE;
        for (String str : titles) {
            str = str.replace(' ', '+').trim();
            int deviation = str.compareToIgnoreCase(terms);
            deviation = deviation - Math.abs(str.length() - terms.length());
            if (deviation < minDeviation) {
                minDeviation = deviation;
            }
        }


        log.debug("Anime search deviation: " + minDeviation);

        if (minDeviation > 3) {
            return false;
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        eb.setThumbnail("https://myanimelist.cdn-dena.com/img/sp/icon/apple-touch-icon-256.png");
        if (data.has("title")){//MessageFormat.format(I18n.get(channel.getGuild()).getString("malTitle"), msg, data.get("title")) : msg;
            eb.addField("Title", "" + data.get("title"), true);
        }
        if (data.has("english")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malEnglishTitle"), msg, data.get("english")) : msg;
            eb.addField("English", "" + data.get("english"), true);
        }
        if (data.has("synonyms")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malSynonyms"), msg, data.get("synonyms")) : msg;
            eb.addField("Synonyms", ""+data.get("synonyms"), true);
        }
        if (data.has("episodes")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malEpisodes"), msg, data.get("episodes")) : msg;
            eb.addField("Episodes", ""+data.get("episodes"), true);
        }
        if (data.has("score")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malScore"), msg, data.get("score")) : msg;
            eb.addField("Score", ""+data.get("score"), true);
        }
        if (data.has("type")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malType"), msg, data.get("type")) : msg;
            eb.addField("Type", ""+data.get("type"), true);
        }
        if (data.has("status")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malStatus"), msg, data.get("status")) : msg;
            eb.addField("Status", ""+data.get("status"), true);
        }
        if (data.has("end_date")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malEndDate"), msg, data.get("end_date")) + "\n" : msg;
            eb.addField("End date", ""+data.get("end_date"), true);
        }
        if (data.has("synopsis")) {
            Matcher m = Pattern.compile("^[^\\n\\r<]+").matcher(StringEscapeUtils.unescapeHtml4(data.getString("synopsis")));
            m.find();
            if(data.has("synopsis")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malSynopsis"), msg, m.group(0)) : msg;
                eb.addField("Synopsis", "" + m.group(0), true);
            }
        }

        if (data.has("id")) {// ? msg + "http://myanimelist.net/anime/" + data.get("id") + "/" : msg;
            eb.addField("http://myanimelist.net/anime/" + data.get("id") + "/", "", true);
        }
        channel.sendMessage(eb.build()).queue();
        return true;
    }

    private boolean handleUser(TextChannel channel, Member invoker, String body) {
        String msg = MessageFormat.format(I18n.get(channel.getGuild()).getString("malUserReveal"), invoker.getEffectiveName());

        //Read JSON
        JSONObject root = new JSONObject(body);
        JSONArray items = root.getJSONArray("categories").getJSONObject(0).getJSONArray("items");
        if (items.length() == 0) {
            channel.sendMessage(MessageFormat.format(I18n.get(channel.getGuild()).getString("malNoResults"), invoker.getEffectiveName())).queue();
            return false;
        }

        JSONObject data = items.getJSONObject(0);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        if (data.has("name")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malUserName"), msg, data.get("name")) : msg;
            eb.setTitle(data.get("name").toString());
        }
        if (data.has("url")){// ? MessageFormat.format(I18n.get(channel.getGuild()).getString("malUrl"), msg, data.get("url")) : msg;
            eb.addField("Url", data.get("url").toString(), true);
        }
        if (data.has("image_url")){// ? msg + data.get("image_url") : msg;
            eb.setImage(data.get("image_url").toString());
        }
        //log.debug(msg);

        channel.sendMessage(eb.build()).queue();
        return true;
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} <search-term>\n#";
        return usage + I18n.get(guild).getString("helpMALCommand");
    }
}
