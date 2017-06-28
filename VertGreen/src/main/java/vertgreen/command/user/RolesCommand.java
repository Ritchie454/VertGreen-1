package vertgreen.command.user;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUserCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import java.util.*;
import vertgreen.Config;

import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class RolesCommand extends Command implements IUserCommand {
    EmbedBuilder eb;
    Member target;
    String hasteurl;
    String msgcontent;
    String searchterm;
    List<Member> list;
    String formroles = "";
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        msgcontent = message.getRawContent();
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
        searchterm = searchterm.toLowerCase();
        list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
        if (args.length == 1) {
            getSelfRoles(channel, invoker);
            postToWeb(channel);
        } else {
            getFuzzyResult(channel);
            if (list.size() == 0) {
                searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "roles ", "");
                channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                getRolesTarget(channel);
                postToWeb(channel);
            } else if (list.size() >= 2){
                fuzzyMultiResult(channel);
            } 
        }   
    }

    private void getSelfRoles(TextChannel channel, Member invoker){
            target = invoker;
            String sortroles = "";
            String shortroles = "";
            if (target.getRoles().size() >= 1){     
                List<Role> roles = new ArrayList<>(target.getRoles());
                Collections.sort(roles);
                for (int i = 0; i < 10; i++){
                    if(roles.size() == i) break;
                    shortroles = shortroles + "**" + roles.get(i).getName() + "**" + "-" + roles.get(i).getId() + "\n";
                }
                shortroles = roles.size() > 10 ? shortroles + "\nYou have more than 10 roles, please click the hastebin link for a full list" : shortroles;
                sortroles = roles.toString();
                formroles = sortroles.replace("R:", "**").replace("[", "").replace("(", "**--").replace("),", "\n").replace("]", "").replace(")", "");
            } else {
                formroles = "everyone";
            }
            eb.addField("Roles for " + invoker.getEffectiveName(), "" + shortroles, true);
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setColor(invoker.getColor());
            channel.sendMessage(eb.build()).queue();
            
    }
    
    private void getRolesTarget(TextChannel channel){
            target = list.get(0);
            String sortroles = "";
            String shortroles = "";
            if (target.getRoles().size() >= 1){     
                List<Role> roles = new ArrayList<>(target.getRoles());
                Collections.sort(roles);
                for (int i = 0; i < 10; i++){
                    if(roles.size() == i) break;
                    shortroles = shortroles + "**" + roles.get(i).getName() + "**" + "-" + roles.get(i).getId() + "\n";
                }
                shortroles = roles.size() > 10 ? shortroles + "\nYou have more than 10 roles, please click the hastebin link for a full list" : shortroles;
                sortroles = roles.toString();
                formroles = sortroles.replace("R:", "**").replace("[", "").replace("(", "**--").replace("),", "\n").replace("]", "").replace(")", "");
            } else {
                formroles = "everyone";
            }
            eb.addField("Roles for " + target.getEffectiveName(), "" + shortroles, true);
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setColor(target.getColor());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void postToWeb(TextChannel channel){
            try {
                hasteurl = TextUtils.postToHastebin(formroles, true) + ".roles";
            }
            catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload roles to hastebin :(");
            }
            channel.sendMessage("If you can't see embeds, view your roles here:\n" + hasteurl).queue();
    }
    
    private void getFuzzyResult(TextChannel channel){
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "roles ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
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
        return usage + "Get the roles for the mentionned user";
    }
}
