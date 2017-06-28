/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vertgreen.command.fun;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IFunCommand;

/**
 *
 * @author Ritchie__
 */
public class WalletMemeCommand extends Command implements IFunCommand {
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage("http://i.imgur.com/ZCTfWV7.jpg").queue();
    }
    
    @Override
    public String help(Guild guild) {
        return "FeelsWalletMan";
    }
}
