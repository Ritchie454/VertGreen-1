package vertgreen.commandmeta.init;

import vertgreen.command.user.*;
import vertgreen.command.util.*;
import vertgreen.command.maintenance.*;
import vertgreen.command.fun.*;
import vertgreen.command.admin.*;
import vertgreen.command.moderation.*;
import vertgreen.command.image.*;

import vertgreen.commandmeta.CommandRegistry;

public class MainCommandInitializer {

    public static void initCommands() {
        CommandRegistry.registerCommand("help", new HelpCommand());

        CommandRegistry.registerCommand("commands", new CommandsCommand());
        CommandRegistry.registerAlias("commands", "comms");
        CommandRegistry.registerAlias("commands", "cmds");
        CommandRegistry.registerCommand("status", new StatusCommand());
        CommandRegistry.registerCommand("memory", new MemoryCommand());
        CommandRegistry.registerCommand("shards", new ShardsCommand());
        CommandRegistry.registerCommand("version", new VersionCommand());
        CommandRegistry.registerCommand("stats", new StatsCommand());
        CommandRegistry.registerCommand("serverinfo", new vertgreen.command.util.ServerInfoCommand());
        CommandRegistry.registerAlias("serverinfo", "guildinfo");
        CommandRegistry.registerCommand("invite", new InviteCommand());
        CommandRegistry.registerCommand("userinfo", new vertgreen.command.user.UserInfoCommand());
        CommandRegistry.registerCommand("gitinfo", new GitInfoCommand());
        CommandRegistry.registerAlias("gitinfo", "git");
        CommandRegistry.registerCommand("exit", new ExitCommand());
        CommandRegistry.registerCommand("avatar", new AvatarCommand());
        CommandRegistry.registerCommand("boast", new JokeCommand());
        CommandRegistry.registerCommand("update", new UpdateCommand());
        CommandRegistry.registerCommand("clear", new ClearCommand());
        CommandRegistry.registerCommand("talk", new TalkCommand());
        CommandRegistry.registerCommand("mal", new MALCommand());
        CommandRegistry.registerCommand("hardban", new HardbanCommand());
        CommandRegistry.registerCommand("kick", new KickCommand());
        CommandRegistry.registerCommand("softban", new SoftbanCommand());
        CommandRegistry.registerCommand("donate", new DonateCommand());
        CommandRegistry.registerAlias("donate", "patron");
        CommandRegistry.registerAlias("donate", "patreon");
        CommandRegistry.registerCommand("perms", new PermissionsCommand());
        CommandRegistry.registerCommand("roles", new RolesCommand());
        CommandRegistry.registerCommand("kservers", new KnownServersCommand());
        CommandRegistry.registerCommand("eval", new EvalCommand());
        CommandRegistry.registerCommand("revive", new ReviveCommand());
        CommandRegistry.registerCommand("slist", new ShardListCommand());
        CommandRegistry.registerAlias("slist", "shardlist");
        CommandRegistry.registerCommand("card", new CardCommand());
        CommandRegistry.registerCommand("feelswalletman", new WalletMemeCommand());
        CommandRegistry.registerCommand("rb1", new RB1Command());
        CommandRegistry.registerCommand("rb2", new RB2Command());
        CommandRegistry.registerCommand("rb3", new RB3Command());
        CommandRegistry.registerCommand("vii", new VIICommand());
        CommandRegistry.registerCommand("botrestart", new FullRestartCommand());
        CommandRegistry.registerCommand("sysinfo", new SysInfoCommand());
        CommandRegistry.registerCommand("setgame", new SetGameCommand());
    }

}
