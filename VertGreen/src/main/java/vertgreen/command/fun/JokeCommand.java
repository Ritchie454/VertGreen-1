package vertgreen.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IFunCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeCommand extends Command implements IFunCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            JSONObject object = Unirest.get("http://api.icndb.com/jokes/random").asJson().getBody().getObject();

            if (!"success".equals(object.getString("type"))) {
                throw new RuntimeException("Couldn't gather joke ;|");
            }
            
            String joke = object.getJSONObject("value").getString("joke");
            String remainder = message.getContent().substring(args[0].length()).trim();
            
            if(message.getMentionedUsers().size() > 0){
                joke = joke.replaceAll("Chuck Norris", "<@"+message.getMentionedUsers().get(0).getId()+">");
            } else if (remainder.length() > 0){
                joke = joke.replaceAll("Chuck Norris", remainder);
            }
            
            joke = joke.replaceAll("&quot;", "\"");
            
            channel.sendMessage(joke).queue();
        } catch (UnirestException ex) {
            Logger.getLogger(JokeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} @<username>\n#Tell a joke about a user.";
    }
}
