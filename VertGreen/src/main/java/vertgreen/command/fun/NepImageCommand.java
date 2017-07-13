package vertgreen.command.fun;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.Config;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IFunCommand;
import net.dv8tion.jda.core.EmbedBuilder;

public class NepImageCommand extends Command implements IFunCommand{

    RandomImageCommand rb1 = new RandomImageCommand("https://imgur.com/a/mhGBq");
    RandomImageCommand rb2 = new RandomImageCommand("https://imgur.com/a/uNtgL");
    RandomImageCommand rb3 = new RandomImageCommand("https://imgur.com/a/hlQfZ");
    RandomImageCommand vii = new RandomImageCommand("https://imgur.com/a/MEymV");
    RandomImageCommand megatag = new RandomImageCommand("https://imgur.com/a/dGsfZ");
    RandomImageCommand superd = new RandomImageCommand("https://imgur.com/a/VR5Ae");
    RandomImageCommand card = new RandomImageCommand("https://imgur.com/a/b2cbk");
    RandomImageCommand adf = new RandomImageCommand("https://imgur.com/a/Mx5ai");
    String msgcontent;
    String nepList = ("\n```\nHere Is a list of available Image types:\n```Markdown\n"
                        + "[rb1]:­\n#Display an Image from Hyperdimension Neptunia Re;Birth 1\n"
                        + "[rb2]:­\n#Display an Image from Hyperdimension Neptunia Re;Birth 2\n"
                        + "[rb3]:­\n#Display an Image from Hyperdimension Neptunia Re;Birth 3\n"
                        + "[vii]:­\n#Display an Image from Megadimension Neptunia VII\n"
                        + "[megatag]:­\n#Display an Image from Megatagmension Blanc + Neptune VS Zombies\n"
                        + "[super]:­\n#Display an Image from Superdimension Neptune VS Sega Hard Girls\n"
                        + "[card]:­\n#Display an Image from the old Nep mobile card app\n"
                        + "[adf]:­\n#Display an Image from Fairy Fencer F Advent Dark Force");

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        msgcontent = message.getRawContent().replace(Config.CONFIG.getPrefix(), "");
        EmbedBuilder eb = new EmbedBuilder();
        switch (msgcontent) {
            case "rb1":
                eb.setImage(rb1.getRandomImageUrl());
                eb.setFooter(rb1.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "rb2":
                eb.setImage(rb2.getRandomImageUrl());
                eb.setFooter(rb2.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "rb3":
                eb.setImage(rb3.getRandomImageUrl());
                eb.setFooter(rb3.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "vii":
                eb.setImage(vii.getRandomImageUrl());
                eb.setFooter(vii.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "megatag":
                eb.setImage(megatag.getRandomImageUrl());
                eb.setFooter(megatag.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "super":
                eb.setImage(superd.getRandomImageUrl());
                eb.setFooter(superd.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "card":
                eb.setImage(card.getRandomImageUrl());
                eb.setFooter(card.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
            case "adf":
                eb.setImage(adf.getRandomImageUrl());
                eb.setFooter(adf.getRandomImageUrl(), "https://assets.materialup.com/uploads/9d3f5836-c935-4b55-bc97-4595e1d6f4d9/preview.jpg");
                break;
        }
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return Config.CONFIG.getPrefix() + "<Image Name> \n#Display a random image" + nepList;
    }
}
