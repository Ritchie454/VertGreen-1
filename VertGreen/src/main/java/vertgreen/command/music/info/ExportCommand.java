package vertgreen.command.music.info;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.List;

public class ExportCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        
        if(player.getRemainingTracks().isEmpty()){
            throw new MessagingException(I18n.get(guild).getString("exportEmpty"));
        }
        
        List<AudioTrackContext> tracks = player.getRemainingTracks();
        String out = "";
        
        for(AudioTrackContext atc : tracks){
            AudioTrack at = atc.getTrack();
            if(at instanceof YoutubeAudioTrack){
                out = out + "https://www.youtube.com/watch?v=" + at.getIdentifier() + "\n";
            } else {
                out = out + at.getIdentifier() + "\n";
            }
        }
        
        try {
            String url = TextUtils.postToHastebin(out, true) + ".vertplist";
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("exportPlaylistResulted"), url)).queue();
        } catch (UnirestException ex) {
            throw new MessagingException(I18n.get(guild).getString("exportPlaylistFail"));
        }
        
        
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpExportCommand");
    }
}
