package vertgreen.command.user;

import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUserCommand;
import vertgreen.commandmeta.MessagingException;
import vertgreen.util.TextUtils;
import vertgreen.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.Collections;
import java.util.*;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PermissionsCommand extends Command implements IUserCommand {
    EmbedBuilder eb;
    Member target;
    String hasteurl;
    String msgcontent;
    String sortperms;
    String sortedperms;
    List<Permission> permurl;
    List<Member> list;
    String searchterm;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        msgcontent = message.getRawContent();
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
        searchterm = searchterm.toLowerCase();
        list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
        if (args.length == 1) {
            getPermissionsSelf(channel, invoker);
            postToWeb(channel);
        } else {
            getFuzzyResult(channel, message);
            if (list.size() == 0) {
               searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "userinfo ", "");
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                getPermissionsTarget(channel);
                postToWeb(channel);
            } else if (list.size() >= 2){
                fuzzyMultiResult(channel);
            } 
        }       
    }

    private void getPermissionsSelf(TextChannel channel, Member invoker){
            target = invoker;
            eb.setColor(invoker.getColor());
            eb.setThumbnail(invoker.getUser().getAvatarUrl());
            permurl = new ArrayList<>(target.getPermissions()); 
            Collections.sort(permurl);
            sortperms = permurl.toString();
            sortedperms = " - " + sortperms.replace("_", " ").replace("["," ").replace("]", " ").replace(",", "\n - ").toLowerCase();
            eb.addField("Permissions for " + invoker.getEffectiveName(), " " + sortedperms, true);
            channel.sendMessage(eb.build()).queue();
    }
    
    private void getPermissionsTarget(TextChannel channel){
            target = list.get(0);
            eb.setColor(target.getColor());
            permurl = new ArrayList<>(target.getPermissions()); 
            Collections.sort(permurl);
            sortperms = permurl.toString();
            sortedperms = " - " + sortperms.replace("_", " ").replace("["," ").replace("]", " ").replace(",", "\n - ").toLowerCase();              
            eb.addField("Permissions for " + target.getEffectiveName(), " " + sortedperms, true);
            eb.setThumbnail(target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void getFuzzyResult(TextChannel channel, Message message){
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "perms ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
    }
    
    private void postToWeb(TextChannel channel){
            try {
                hasteurl = TextUtils.postToHastebin(sortedperms, true) + ".perms";
            }
            catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload permissions to hastebin :(");
            }
            channel.sendMessage("If you can't see embeds, view your permissions here:\n" + hasteurl).queue();
    }
    
    private void fuzzyMultiResult(TextChannel channel){
            String msg = "Multiple users were found. Did you mean any of these users?\n```";
            for (int i = 0; i < 5; i++){
                if(list.size() == i) break;
                msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
            }
            msg = list.size() > 5 ? msg + "\n[...]" : msg;
            msg = msg + "```";
            channel.sendMessage(msg).queue();
    }
    
    @Override
    public String help(Guild guild) {
        String usage = "{0} {1} @<username>\n#";
        return usage + "Get permissions for the mentionned user";
    }
}
