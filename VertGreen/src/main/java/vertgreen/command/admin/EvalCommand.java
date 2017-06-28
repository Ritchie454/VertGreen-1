package vertgreen.command.admin;

import vertgreen.audio.AbstractPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.core.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.*;

public class EvalCommand extends Command implements ICommandOwnerRestricted {
    
    private static final Logger log = LoggerFactory.getLogger(EvalCommand.class);

    //Thanks Dinos!
    private ScriptEngine engine;

    public EvalCommand() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");

        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member author, Message message, String[] args) {
        JDA jda = guild.getJDA();
        EmbedBuilder eb = new EmbedBuilder();
        channel.sendTyping().queue();
        Runtime rt = Runtime.getRuntime();
        final String source = message.getRawContent().substring(args[0].length() + 1);
        
        engine.put("jda", jda);
        engine.put("api", jda);
        engine.put("channel", channel);
        engine.put("vc", PlayerRegistry.getExisting(guild) != null ? PlayerRegistry.getExisting(guild).getChannel() : null);
        engine.put("author", author);
        engine.put("bot", jda.getSelfUser());
        engine.put("member", guild.getSelfMember());
        engine.put("message", message);
        engine.put("guild", guild);
        engine.put("player", PlayerRegistry.getExisting(guild));
        engine.put("pm", AbstractPlayer.getPlayerManager());
        engine.put("eb", eb);
        engine.put("rt", rt);
        
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = service.schedule(() -> {

            Object out = null;
            try {
                out = engine.eval(
                        "(function() {"
                        + "with (imports) {\n" + source + "\n}"
                        + "})();");

            } catch (Exception ex) {
                channel.sendMessage("`"+ex.getMessage()+"`").queue();
                log.error("Error occurred in eval", ex);
                return;
            }

            String outputS;
            if (out == null) {
                outputS = ":ok_hand:";
            } else if (out.toString().contains("\n")) {
                outputS = "\nEval: ```\n" + out.toString() + "```";
            } else {
                outputS = "\nEval: `" + out.toString() + "`";
            }

            channel.sendMessage("```java\n"+source+"```" + "\n" + outputS).queue();

        }, 0, TimeUnit.MILLISECONDS);

        Thread script = new Thread("Eval") {
            @Override
            public void run() {
                try {
                    future.get(10, TimeUnit.SECONDS);

                } catch (TimeoutException ex) {
                    future.cancel(true);
                    channel.sendMessage("Task exceeded time limit.").queue();
                } catch (Exception ex) {
                    channel.sendMessage("`"+ex.getMessage()+"`").queue();
                }
            }
        };
        script.start();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} <Java-code>\\n#Run the provided Java code.";
    }
}
