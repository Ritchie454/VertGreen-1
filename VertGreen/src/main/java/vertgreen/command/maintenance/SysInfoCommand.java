package vertgreen.command.maintenance;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;
import java.io.File;

public class SysInfoCommand extends Command implements IMaintenanceCommand {
    String nameOS = "os.name";  
    String versionOS = "os.version";  
    String architectureOS = "os.arch";
    String verJava = "java.version";
    String homeJava = "java.home";
    String nameUser = "user.name";
    String homeUser = "user.home";
    String dirUser = "user.dir";
    String OSName;
    String OSVer;
    String OSArch;
    String JavaVer;
    String JavaDir;
    String UsrName;
    String UsrHome;
    String UsrDir;
    String CPU;
    String Mem;
    String TotSpace;
    String FreeSpace;
    String UsableSpace;
    String HDD;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        File[] roots = File.listRoots();
        for (File root : roots) {
            TotSpace = ((root.getTotalSpace() / 1000000) / 1024) + "GB";
            FreeSpace = ((root.getFreeSpace() / 1000000) / 1024) + "GB";
            UsableSpace = ((root.getUsableSpace() / 1000000) / 1024) + "GB";
        }
        
        OSName = "OS Name: " + System.getProperty(nameOS) + "\n";
        OSVer = "OS Version: " + System.getProperty(versionOS) + "\n";
        OSArch = "OS Architecture: " + System.getProperty(architectureOS) + "\n";
        JavaVer = "Java Version: " + System.getProperty(verJava) + "\n";
        JavaDir = "Java Directory: " + System.getProperty(homeJava) + "\n";
        UsrName = "Username: " + System.getProperty(nameUser) + "\n";
        UsrHome = "User directory: " + System.getProperty(homeUser) + "\n";
        UsrDir = "Current directory: " + System.getProperty(dirUser) + "\n";
        CPU = "Available Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
        Mem = "Installed RAM: " + (Runtime.getRuntime().maxMemory() / 1000000) / 1024 + "GB\n";
        HDD = "Total Disk space: " + TotSpace + "\nFree Disk space: " + FreeSpace + "\nUsable Disk space: " + UsableSpace + "\n";
        
        eb.setTitle("System Information for VertCore");
        eb.setColor(BotConstants.VERTGREEN);
        eb.addField("OS Information", OSName + OSVer + OSArch, true);
        eb.addField("Java Information", JavaVer + JavaDir, true);
        eb.addField("User Information", UsrName + UsrHome + UsrDir, true);
        eb.addBlankField(true);
        eb.addField("Hardware Information", CPU + Mem + HDD, true);
        
        channel.sendMessage(eb.build()).queue();
    }
        
    @Override
    public String help(Guild guild) {
        return "View System Information";
    }
}
