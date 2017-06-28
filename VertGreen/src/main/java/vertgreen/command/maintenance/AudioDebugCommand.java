package vertgreen.command.maintenance;

import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.audio.AudioLossCounter;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.util.BotConstants;

public class AudioDebugCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        String msg = "";
        GuildPlayer guildPlayer = PlayerRegistry.getExisting(guild);

        if(guildPlayer == null) {
            msg = msg + "No GuildPlayer found.\n";
            channel.sendMessage(msg);
        } else {
            int deficit = AudioLossCounter.EXPECTED_PACKET_COUNT_PER_MIN - (guildPlayer.getAudioLossCounter().getLastMinuteLoss() + guildPlayer.getAudioLossCounter().getLastMinuteSuccess());
EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(BotConstants.VERTGREEN);
            eb.setTitle("Debugging information");
            eb.addField("Last minute's packet stats:", "Packets sent:   " + guildPlayer.getAudioLossCounter().getLastMinuteSuccess() + "\n"
                + "Null packets:   " + guildPlayer.getAudioLossCounter().getLastMinuteLoss() + "\n"
                + "Packet deficit: " + deficit, true);
            Integer loss = guildPlayer.getAudioLossCounter().getLastMinuteLoss();
            String status;
            if (loss >= 3000) {
                status = " | Warning, High packet loss!";
            } else if (loss >= 1500){
                status = " | Moderate packet loss";
            } else {
                status = " | Low packet loss 👌";
            }
            eb.setFooter(status, "http://images.clipartpanda.com/bolt-20clip-20art-4cbzq4ncg.png");
            channel.sendMessage(eb.build()).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show audio related debug information.";
    }
}
