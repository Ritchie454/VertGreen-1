package vertgreen.command.image;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.command.fun.RandomImageCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IImageCommand;
import net.dv8tion.jda.core.EmbedBuilder;

public class RB3Command extends Command implements IImageCommand{
    private RandomImageCommand CG = new RandomImageCommand("https://imgur.com/a/1lVnQ");
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();  
        eb.setImage(CG.getRandomImageUrl());
        eb.setFooter(CG.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "<<rb3 \n#Display a random image";
    }
}
