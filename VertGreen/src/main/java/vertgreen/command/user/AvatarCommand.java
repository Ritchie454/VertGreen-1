package vertgreen.command.user;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUserCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.MessagingException;
import vertgreen.Config;
import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class AvatarCommand extends Command implements IUserCommand {
    Member target;
    String msgcontent;
    String searchterm;
    List<Member> list;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        msgcontent = message.getRawContent();
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
        searchterm = searchterm.toLowerCase();
        List<Member> list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
        if (args.length == 1) {
            getAvatarSelf(channel, invoker);
            sendToSite(channel);
        } else {
            if (list.size() == 0) {
               searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "avatar ", "");
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                getAvatarTarget(channel);
                sendToSite(channel);
            } else if (list.size() >= 2){
                multiFuzzyResults(channel);
            } 
        }
    }
    
    private void getAvatarTarget(TextChannel channel){
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "avatar ", "");
        searchterm = searchterm.toLowerCase();
        list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
        target = list.get(0);
        eb.setColor(target.getColor());
        eb.setTitle("Avatar for " + target.getEffectiveName());
        eb.setImage(target.getUser().getAvatarUrl() + "?size=1024");
        channel.sendMessage(eb.build()).queue();
    }
    
    private void getAvatarSelf(TextChannel channel, Member invoker){
        target = invoker;
        eb.setColor(target.getColor());
        eb.setTitle("Avatar for " + target.getEffectiveName());
        eb.setImage(target.getUser().getAvatarUrl() + "?size=1024");
        channel.sendMessage(eb.build()).queue();
    }
    
    private void sendToSite(TextChannel channel){
        try {
            String comurl = TextUtils.postToHastebin(target.getUser().getAvatarUrl() + "?size=1024", true) + ".avatar";       
            channel.sendMessage("If you can't see embeds, use the link below:\n" + comurl).queue();
        }
        catch (UnirestException ex) {
            throw new MessagingException("Couldn't upload avatar to hastebin :(");
        }
    }
    
    private void multiFuzzyResults(TextChannel channel){
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "avatar ", "");
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
    
    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} @<username>\n#";
        return usage + I18n.get(guild).getString("helpAvatarCommand");
    }
}
