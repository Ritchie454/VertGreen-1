package vertgreen.commandmeta.init;

import vertgreen.command.music.control.SkipCommand;
import vertgreen.command.music.control.PlayCommand;
import vertgreen.command.music.control.PauseCommand;
import vertgreen.command.music.control.StopCommand;
import vertgreen.command.music.control.PlaySplitCommand;
import vertgreen.command.music.control.JoinCommand;
import vertgreen.command.music.control.ReshuffleCommand;
import vertgreen.command.music.control.LeaveCommand;
import vertgreen.command.music.control.VolumeCommand;
import vertgreen.command.music.control.UnpauseCommand;
import vertgreen.command.music.control.SelectCommand;
import vertgreen.command.music.control.RepeatCommand;
import vertgreen.command.music.control.ShuffleCommand;
import vertgreen.command.maintenance.AudioDebugCommand;
import vertgreen.command.admin.AnnounceCommand;
import vertgreen.command.admin.PlayerDebugCommand;
import vertgreen.agent.VoiceChannelCleanupAgent;
import vertgreen.command.config.ConfigCommand;
import vertgreen.command.config.LanguageCommand;
import vertgreen.command.music.info.ExportCommand;
import vertgreen.command.music.info.ListCommand;
import vertgreen.command.music.info.NowplayingCommand;
import vertgreen.command.music.seeking.ForwardCommand;
import vertgreen.command.music.seeking.RestartCommand;
import vertgreen.command.music.seeking.RewindCommand;
import vertgreen.command.music.seeking.SeekCommand;
import vertgreen.command.util.CommandsCommand;
import vertgreen.command.util.HelpCommand;
import vertgreen.command.util.MusicHelpCommand;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicCommandInitializer {

    private static final Logger log = LoggerFactory.getLogger(MusicCommandInitializer.class);

    public static void initCommands() {
        CommandRegistry.registerCommand("help", new HelpCommand());
        CommandRegistry.registerAlias("help", "info");

        CommandRegistry.registerCommand("play", new PlayCommand(SearchUtil.SearchProvider.YOUTUBE));
        CommandRegistry.registerAlias("play", "yt");
        CommandRegistry.registerCommand("sc", new PlayCommand(SearchUtil.SearchProvider.SOUNDCLOUD));
        CommandRegistry.registerAlias("sc", "soundcloud");
        CommandRegistry.registerCommand("skip", new SkipCommand());
        CommandRegistry.registerCommand("join", new JoinCommand());
        CommandRegistry.registerAlias("join", "summon");
        CommandRegistry.registerCommand("nowplaying", new NowplayingCommand());
        CommandRegistry.registerAlias("nowplaying", "np");
        CommandRegistry.registerCommand("leave", new LeaveCommand());
        CommandRegistry.registerCommand("list", new ListCommand());
        CommandRegistry.registerAlias("list", "queue");
        CommandRegistry.registerCommand("select", new SelectCommand());
        CommandRegistry.registerCommand("stop", new StopCommand());
        CommandRegistry.registerCommand("pause", new PauseCommand());
        CommandRegistry.registerCommand("unpause", new UnpauseCommand());
        CommandRegistry.registerCommand("shuffle", new ShuffleCommand());
        CommandRegistry.registerCommand("reshuffle", new ReshuffleCommand());
        CommandRegistry.registerCommand("repeat", new RepeatCommand());
        CommandRegistry.registerCommand("volume", new VolumeCommand());
        CommandRegistry.registerAlias("volume", "vol");
        CommandRegistry.registerCommand("restart", new RestartCommand());
        CommandRegistry.registerCommand("export", new ExportCommand());
        CommandRegistry.registerCommand("playerdebug", new PlayerDebugCommand());
        CommandRegistry.registerCommand("music", new MusicHelpCommand());
        CommandRegistry.registerAlias("music", "musichelp");
        CommandRegistry.registerCommand("commands", new CommandsCommand());
        CommandRegistry.registerAlias("commands", "comms");
        CommandRegistry.registerCommand("split", new PlaySplitCommand());
        CommandRegistry.registerCommand("config", new ConfigCommand());
        CommandRegistry.registerCommand("lang", new LanguageCommand());
        CommandRegistry.registerCommand("adebug", new AudioDebugCommand());
        CommandRegistry.registerCommand("announce", new AnnounceCommand());

        CommandRegistry.registerCommand("seek", new SeekCommand());
        CommandRegistry.registerCommand("forward", new ForwardCommand());
        CommandRegistry.registerAlias("forward", "fwd");
        CommandRegistry.registerCommand("rewind", new RewindCommand());
        CommandRegistry.registerAlias("rewind", "rew");

        new VoiceChannelCleanupAgent().start();

    }

}
