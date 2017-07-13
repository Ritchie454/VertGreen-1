/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package vertgreen.commandmeta.init;

import vertgreen.Config;
import vertgreen.agent.VoiceChannelCleanupAgent;
import vertgreen.command.admin.*;
import vertgreen.command.maintenance.*;
import vertgreen.command.moderation.ConfigCommand;
import vertgreen.command.moderation.PermissionsCommand;
import vertgreen.command.music.control.*;
import vertgreen.command.music.info.ExportCommand;
import vertgreen.command.music.info.GensokyoRadioCommand;
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
import vertgreen.perms.PermissionLevel;
import vertgreen.util.constant.DistributionEnum;
import vertgreen.util.rest.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicCommandInitializer {

    private static final Logger log = LoggerFactory.getLogger(MusicCommandInitializer.class);

    public static void initCommands() {
        //CONTROL
        CommandRegistry.registerCommand("destroy", new DestroyCommand());
        CommandRegistry.registerCommand("join", new JoinCommand(), "summon", "jn");
        CommandRegistry.registerCommand("leave", new LeaveCommand(), "lv");
        CommandRegistry.registerCommand("pause", new PauseCommand(), "pa", "ps");
        CommandRegistry.registerCommand("play", new PlayCommand(SearchUtil.SearchProvider.YOUTUBE), "yt", "youtube");
        CommandRegistry.registerCommand("sc", new PlayCommand(SearchUtil.SearchProvider.SOUNDCLOUD), "soundcloud");
        CommandRegistry.registerCommand("split", new PlaySplitCommand());
        CommandRegistry.registerCommand("repeat", new RepeatCommand(), "rep");
        CommandRegistry.registerCommand("reshuffle", new ReshuffleCommand(), "resh");
        CommandRegistry.registerCommand("select", new SelectCommand(), "sel");
        CommandRegistry.registerCommand("shuffle", new ShuffleCommand(), "sh");
        CommandRegistry.registerCommand("skip", new SkipCommand(), "sk");
        CommandRegistry.registerCommand("stop", new StopCommand(), "st");
        CommandRegistry.registerCommand("unpause", new UnpauseCommand(), "unp", "resume");
        CommandRegistry.registerCommand("volume", new VolumeCommand(), "vol");
        //INFO
        CommandRegistry.registerCommand("export", new ExportCommand(), "ex");
        CommandRegistry.registerCommand("gr", new GensokyoRadioCommand(), "gensokyo", "gensokyoradio");
        CommandRegistry.registerCommand("list", new ListCommand(), "queue", "q");
        CommandRegistry.registerCommand("nowplaying", new NowplayingCommand(), "np");
        //SEEKING
        CommandRegistry.registerCommand("forward", new ForwardCommand(), "fwd");
        CommandRegistry.registerCommand("restart", new RestartCommand());
        CommandRegistry.registerCommand("rewind", new RewindCommand(), "rew");
        CommandRegistry.registerCommand("seek", new SeekCommand());
        //UTIL
        CommandRegistry.registerCommand("music", new MusicHelpCommand(), "musichelp");
        
        new VoiceChannelCleanupAgent().start();
    }

}
