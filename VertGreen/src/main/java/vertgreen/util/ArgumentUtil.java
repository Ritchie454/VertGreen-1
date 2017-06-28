package vertgreen.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ArgumentUtil {

    private ArgumentUtil() {}

    public static List<Member> fuzzyMemberSearch(Guild guild, String term) {
        ArrayList<Member> list = new ArrayList<>();

        term = term.toLowerCase();

        for(Member mem : guild.getMembers()) {
            if((mem.getUser().getName().toLowerCase() + "#" + mem.getUser().getDiscriminator()).contains(term)
                    | (mem.getEffectiveName().toLowerCase().contains(term))
                    | term.contains(mem.getUser().getId())) {
                list.add(mem);
            }
        }

        return list;
    }

    public static Member checkSingleFuzzySearchResult(TextChannel tc, String term) {
        List<Member> list = fuzzyMemberSearch(tc.getGuild(), term);

        switch (list.size()) {
            case 0:
                tc.sendMessage("No members found for `" + term + "`.").queue();
                return null;
            case 1:
                return list.get(0);
            default:
                String msg = "Multiple users were found. Did you mean any of these users?\n```";

                for (int i = 0; i < 5; i++){
                    if(list.size() == i) break;
                    msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
                }

                msg = list.size() > 5 ? msg + "\n[...]" : msg;
                msg = msg + "```";

                tc.sendMessage(msg).queue();
                return null;
        }
    }
}
