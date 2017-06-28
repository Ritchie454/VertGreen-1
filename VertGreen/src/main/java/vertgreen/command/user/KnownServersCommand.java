package vertgreen.command.user;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUserCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import vertgreen.commandmeta.MessagingException;

import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class KnownServersCommand extends Command implements IUserCommand {
    Member target;
    String msgcontent;
    String searchterm;
    EmbedBuilder eb;
    String hasteurl;
    List<Member> list;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        if(args.length == 1) {
            knownServersSelf(channel, invoker);
            postToWeb(channel);
        } else {
            getFuzzyResult(channel, message);
            if (list.size() == 0) {
               searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                knownServersTarget(channel, message);
                postToWeb(channel);
            } else if (list.size() >= 2){
                multiFuzzyResult(channel, message);
            } 
        }
    }

    private void knownServersSelf(TextChannel channel, Member invoker){
            List<Guild> matchguild = new ArrayList<>();
            StringBuilder knownServers = new StringBuilder();
            target = invoker;
            if (target == null) return;
            for(Guild g: VertGreen.getAllGuilds()) {
                if(g.getMemberById(target.getUser().getId()) != null) {
                    matchguild.add(g);
                }
            }
            if(matchguild.size() > 10) {
                knownServers.append("\nMore than 10 servers, please see the link below for a full list\n");
            } else {
                int i = 0;
                for(Guild g: matchguild) {
                    i++;
                    knownServers.append(g.getName()).append(",\n");
                    if(i == 10) {
                        break;
                    }
                }
            }
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.addField("Shared servers with " + target.getEffectiveName() ,knownServers.toString(),true); //Known Server
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void knownServersTarget(TextChannel channel, Message message){ 
            msgcontent = message.getRawContent();
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
            List<Guild> matchguild = new ArrayList<>();
            StringBuilder knownServers = new StringBuilder();
            target = list.get(0);
            if (target == null) return;
                for(Guild g: VertGreen.getAllGuilds()) {
                    if(g.getMemberById(target.getUser().getId()) != null) {
                    matchguild.add(g);
                    }
                }
                int i = 0;
                for(Guild g: matchguild) {
                    i++;
                    knownServers.append(g.getName()).append(",\n");
                    if(i == 10) {
                        knownServers.append("\nMore than 10 servers, please see the link below for a full list\n");
                        break;
                }
            }
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.addField("Shared servers with " + target.getEffectiveName() ,knownServers.toString(),true); //Known Server
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void getFuzzyResult(TextChannel channel, Message message){
            msgcontent = message.getRawContent();
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
    }
    
    private void multiFuzzyResult(TextChannel channel, Message message){
            msgcontent = message.getRawContent();
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
            String msg = "Multiple users were found. Did you mean any of these users?\n```";
            for (int i = 0; i < 5; i++){
                if(list.size() == i) break;
                msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
            }
            msg = list.size() > 5 ? msg + "\n[...]" : msg;
            msg = msg + "```";
            channel.sendMessage(msg).queue();
    }
    
    private void postToWeb(TextChannel channel){
            List<Guild> matchguild = new ArrayList<>();
            StringBuilder knownServers = new StringBuilder();
            if (target == null) return;
                for(Guild g: VertGreen.getAllGuilds()) {
                    if(g.getMemberById(target.getUser().getId()) != null) {
                    matchguild.add(g);
                    }
                }
            int i = 0;
            for(Guild g: matchguild) {
                i++;
                knownServers.append(g.getName()).append(",\n");
            }
            try {
                hasteurl = TextUtils.postToHastebin(knownServers.toString(), true) + ".kservers";
            }
            catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload servers to hastebin :(");
            }
            channel.sendMessage("View the full list of known servers here:\n" + hasteurl).queue();
    }  
    
    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} OR {0}{1} <user>\n#";
        return usage + I18n.get(guild).getString("helpUserInfoCommand");
    }
}


