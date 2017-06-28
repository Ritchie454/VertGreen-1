package vertgreen.command.fun;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IFunCommand;
import vertgreen.feature.togglz.FeatureFlags;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringEscapeUtils;

//TODO fix JCA and reintroduce this command
public class TalkCommand extends Command implements IFunCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        String question = message.getRawContent().substring(Config.CONFIG.getPrefix().length() + 5);

        talk(invoker, channel, question);
    }

    public static void talk(Member member, TextChannel channel, String question) {
        //Cleverbot integration
        if (FeatureFlags.CHATBOT.isActive()) {
            String response = VertGreen.jca.getResponse(question);
            response = StringEscapeUtils.unescapeHtml4(response);
            channel.sendMessage(response).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} <text> OR @{2} <text>\n#Talk to the Cleverbot AI.";
    }
}
