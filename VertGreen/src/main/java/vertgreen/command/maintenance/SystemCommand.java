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
 *
 */

package vertgreen.command.maintenance;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;
import vertgreen.Config;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import oshi.hardware.*;
import oshi.util.FormatUtil;

import java.awt.*;
import java.text.DecimalFormat;

public class SystemCommand extends Command implements IMaintenanceCommand {
    EmbedBuilder eb;
    SystemInfo si;
    HardwareAbstractionLayer hw;
    OperatingSystem os;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        String msg = message.getContent().replace(Config.CONFIG.getPrefix() + "system", "");
        si = new SystemInfo();
        eb = new EmbedBuilder();
        hw = si.getHardware();
        os = si.getOperatingSystem();
        eb.setTitle("System Information");
        eb.setColor(Color.GREEN);
        switch (msg){
            case "":
                osInfo();
                getCPU();
                getMemory();
                getDisks();
                break;
            case ".os":
                osInfo();
                break;
            case ".cpu":
                getCPU();
                break;
            case ".memory":
                getMemory();
                break;
            case ".disks":
                getDisks();
                break;
            default:
                eb.setTitle("Invalid syntax!");
                eb.setColor(Color.RED);
                break;
        }
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return ("{0}{1} <Stat Type>\n#Show some information about the system.\n```\nSpecific Details can be viewed using the following syntax:\n```Markdown\n"
                + "[.os]:­\n#Show OS information\n"
                + "[.cpu]:­\n#Show CPU information\n"
                + "[.memory]:­\n#Show Memory information\n"
                + "[.disks]:­\n#Show disk information\n"
                + "\nOr just do {0}{1} for generic information");
    }

    public void getCPU() {
        CentralProcessor cpu = hw.getProcessor();
        String phyNum = "Physical Cores: " + cpu.getPhysicalProcessorCount();
        String logNum = "Logical Cores: " + cpu.getLogicalProcessorCount();
        String model = "CPU Model: " + cpu.getName();
        Double ld = (cpu.getSystemCpuLoad() * 100);
        DecimalFormat df = new DecimalFormat("#.##");
        String load = "Current Load: " + (df.format(ld)) + "%";
        String upTime = "CPU Uptime: " + FormatUtil.formatElapsedSecs(cpu.getSystemUptime());
        String field = (model + "\n" +
                        phyNum + "\n" +
                        logNum + "\n" +
                        load + "\n" +
                        upTime);
        eb.addField(":robot: Processor Information", field, false);
    }

    public void getMemory() {
        GlobalMemory mem = hw.getMemory();
        String totMem = "Total Memory: " + FormatUtil.formatBytes(mem.getTotal());
        String availMem = "Available Memory: " + FormatUtil.formatBytes(mem.getAvailable());
        String usedMem = "Used Memory: " + FormatUtil.formatBytes(mem.getTotal() - mem.getAvailable());
        String totSwap = "Total Swap: " + FormatUtil.formatBytes(mem.getSwapTotal());
        String usedSwap = "Used Swap: " + FormatUtil.formatBytes(mem.getSwapUsed());
        String availSwap = "Available Swap: " + FormatUtil.formatBytes(mem.getSwapTotal() - mem.getSwapUsed());
        String field = (totMem + "\n" +
                        usedMem + "\n" +
                        availMem + "\n" +
                        totSwap + "\n" +
                        usedSwap + "\n" +
                        availSwap);
        eb.addField(":floppy_disk: Memory Information", field, false);
    }

    public void getDisks() {
        HWDiskStore disk[] = hw.getDiskStores();
        String field = new String();
        for (HWDiskStore dsk : disk) {
            String name = dsk.getName() + " information";
            String model = " - Disk Model: " + dsk.getModel();
            String size = " - Disk Size: " + FormatUtil.formatBytesDecimal(dsk.getSize());
            String writes = " - Disk Writes: " + FormatUtil.formatBytes(dsk.getReadBytes());
            String reads = " - Disk Reads: " + FormatUtil.formatBytes(dsk.getWriteBytes());
            String tranTime = " - Disk Transfer Time: " + dsk.getTransferTime();
            field = (name + "\n" +
                            model + "\n" +
                            size + "\n" +
                            writes + "\n" +
                            reads + "\n" +
                            tranTime);
        }
        eb.addField(":cd: Disk Information", field, false);
    }

    public void osInfo() {
        String family = "OS Family: " + os.getFamily();
        String ver = "OS Version: " + os.getVersion();
        String manu = "OS Manufacturer: " + os.getManufacturer();
        String procNum = "Running Processes: " + OperatingSystem.ProcessSort.values().length;
        String field = (family + "\n" +
                        ver + "\n" +
                        manu + "\n" +
                        procNum);
        eb.addField("<:partner:336195782589808641> OS Information", field, false);
    }

}
