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

package vertgreen.event;

import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.VertGreen;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.JDA;
import vertgreen.command.fun.RandomImageCommand;
import vertgreen.util.DiscordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertgreen.util.GitRepoState;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventLogger extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(EventLogger.class);
    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("^(git@|https?://)(.+)[:/](.+)/(.+).git$");
    private RandomImageCommand octocats = new RandomImageCommand("https://imgur.com/a/sBkTj");
    String msg;
    private final String logChannelId;
    private VertGreen shard;

    public EventLogger(String logChannelId) {
        this.logChannelId = logChannelId;
        Runtime.getRuntime().addShutdownHook(new Thread(ON_SHUTDOWN, EventLogger.class.getSimpleName() + " shutdownhook"));
    }

    @Override
    public void onReady(ReadyEvent event) {
        VertGreen.getInstance(event.getJDA());
        VertGreen.getTextChannelById("332940748905512960").sendMessage("[:rocket:] Received ready event.").queue();
        getGitInfo(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        msg = "[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        event.getJDA().getTextChannelById("332940748905512960").sendMessage(msg).queue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        msg = "[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        event.getJDA().getTextChannelById("332940748905512960").sendMessage(msg).queue();
    }

    private final Runnable ON_SHUTDOWN = () -> {
        Runtime rt = Runtime.getRuntime();
        JDA jda = shard.getJda();
        if(VertGreen.shutdownCode != VertGreen.UNKNOWN_SHUTDOWN_CODE){
            msg = "[:door:] Exiting with code " + VertGreen.shutdownCode + ".";
        } else {
            msg = "[:door:] Exiting with unknown code.";
        }
        jda.getTextChannelById("285472208686546946").sendMessage(msg).queue();
    };


    private String getGithubCommitLink() {
        String result = "Could not find or create a valid Github url.";
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
        if (gitRepoState != null) {
            String originUrl = gitRepoState.remoteOriginUrl;

            Matcher m = GITHUB_URL_PATTERN.matcher(originUrl);

            if (m.find()) {
                String domain;
                domain = m.group(2);
                String user = m.group(3);
                String repo = m.group(4);
                String commitId = gitRepoState.commitId;

                result = "https://" + domain + "/" + user + "/" + repo + "/commit/" + commitId;
            }
        }
        return result;
    }

    private void getGitInfo(ReadyEvent event) {
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();

        String url = getGithubCommitLink();
        //times look like this: 31.05.2017 @ 01:17:17 CEST
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy @ hh:mm:ss z");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Build & git info", url);
        embedBuilder.addField("Commit info", gitRepoState.describe + "\n\n" + gitRepoState.commitMessageFull, false);
        embedBuilder.addField("Commit timestamp", gitRepoState.commitTime, false);
        embedBuilder.addField("Commit on Github", url, false);

        embedBuilder.addField("Branch", gitRepoState.branch, true);
        embedBuilder.addField("Built by", "<@!197063812027908097>", true);

        embedBuilder.setColor(new Color(240, 81, 51));//git-scm color
        embedBuilder.setThumbnail(octocats.getRandomImageUrl());//github octocat thumbnail

        try {
            Date built = sdf.parse(gitRepoState.buildTime);
            embedBuilder.setTimestamp(built.toInstant());
            embedBuilder.setFooter("Built on", "http://i.imgur.com/RjWwxlg.png");
        } catch (ParseException ignored) {
        }
        VertGreen.getTextChannelById("333312174434942976").sendMessage(embedBuilder.build()).queue();
    }

}
