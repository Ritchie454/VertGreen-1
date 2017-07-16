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

import vertgreen.command.admin.*;
import vertgreen.command.fun.*;
import vertgreen.command.maintenance.*;
import vertgreen.command.moderation.*;
import vertgreen.command.util.*;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.perms.PermissionLevel;

public class MainCommandInitializer {

    public static void initCommands() {
        //ADMIN
        CommandRegistry.registerCommand("announce", new AnnounceCommand());
        CommandRegistry.registerCommand("botrestart", new BotRestartCommand());
        CommandRegistry.registerCommand("compile", new CompileCommand());
        CommandRegistry.registerCommand("eval", new EvalCommand());
        CommandRegistry.registerCommand("exit", new ExitCommand());
        CommandRegistry.registerCommand("revive", new ReviveCommand());
        CommandRegistry.registerCommand("update", new UpdateCommand());
        //FUN
        CommandRegistry.registerCommand("hug", new HugCommand("https://imgur.com/a/jHJOc"));
        CommandRegistry.registerCommand("joke", new JokeCommand(), "jk");
        CommandRegistry.registerCommand("rb1", new NepImageCommand(), "rb2", "rb3", "vii", "super", "megatag", "adf", "card");
        CommandRegistry.registerCommand("pat", new PatCommand("https://imgur.com/a/WiPTl"));
        CommandRegistry.registerCommand("talk", new TalkCommand());
        CommandRegistry.registerCommand("s", new TextCommand("¯\\_(ツ)_/¯"), "shrug");
        CommandRegistry.registerCommand("lenny", new TextCommand("( ͡° ͜ʖ ͡°)"));
        CommandRegistry.registerCommand("faceofdisapproval", new TextCommand("ಠ_ಠ"), "fod", "disapproving");
        CommandRegistry.registerCommand("sendenergy", new TextCommand("༼ つ ◕_◕ ༽つ"));
        CommandRegistry.registerCommand("wallet", new WalletCommand("http://imgur.com/a/PpAqX"));
        //MAINTENANCE
        CommandRegistry.registerCommand("fuzzy", new FuzzyUserSearchCommand());
        CommandRegistry.registerCommand("gitinfo", new GitInfoCommand(), "git");
        CommandRegistry.registerCommand("shards", new ShardsCommand());
        CommandRegistry.registerCommand("stats", new StatsCommand());
        //MODERATION
        CommandRegistry.registerCommand("clear", new ClearCommand());
        CommandRegistry.registerCommand("config", new ConfigCommand(), "cfg");
        CommandRegistry.registerCommand("hardban", new HardbanCommand());
        CommandRegistry.registerCommand("kick", new KickCommand());
        CommandRegistry.registerCommand("admin", new PermissionsCommand(PermissionLevel.ADMIN));
        CommandRegistry.registerCommand("dj", new PermissionsCommand(PermissionLevel.DJ));
        CommandRegistry.registerCommand("user", new PermissionsCommand(PermissionLevel.USER));
        CommandRegistry.registerCommand("softban", new SoftbanCommand());
        //UTILITY
        CommandRegistry.registerCommand("avatar", new AvatarCommand(), "ava");
        CommandRegistry.registerCommand("brainfuck", new BrainfuckCommand());
        CommandRegistry.registerCommand("commands", new CommandsCommand(), "comms", "cmds");
        CommandRegistry.registerCommand("donate", new DonateCommand(), "patron", "patreon");
        CommandRegistry.registerCommand("help", new HelpCommand(), "info");
        CommandRegistry.registerCommand("invite", new InviteCommand());
        CommandRegistry.registerCommand("mal", new MALCommand());
        CommandRegistry.registerCommand("serverinfo", new vertgreen.command.util.ServerInfoCommand(), "guildinfo");
        CommandRegistry.registerCommand("userinfo", new vertgreen.command.util.UserInfoCommand(), "memberinfo");
        CommandRegistry.registerCommand("steamgame", new SteamGameCommand());
    }

}
