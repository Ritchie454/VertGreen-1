package vertgreen.util.ratelimit;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.audio.queue.PlaylistInfo;
import vertgreen.command.maintenance.ShardsCommand;
import vertgreen.command.music.control.SkipCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.util.DiscordUtil;
import vertgreen.util.Tuple2;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Ratelimiter {

    private static final int RATE_LIMIT_HITS_BEFORE_BLACKLIST = 10;


    //one ratelimiter for all running shards
    private static Ratelimiter ratelimiterSingleton;

    public static Ratelimiter getRatelimiter() {
        if (ratelimiterSingleton == null)
            ratelimiterSingleton = new Ratelimiter();

        return ratelimiterSingleton;
    }


    private final List<Ratelimit> ratelimits;
    private Blacklist autoBlacklist = null;

    private Ratelimiter() {
        Set<Long> whitelist = new ConcurrentHashSet<>();

        //it is ok to use the jda of any shard as long as we aren't using it for guild specific stuff
        JDA jda = VertGreen.getFirstJDA();
        whitelist.add(Long.valueOf(DiscordUtil.getOwnerId(jda)));
        whitelist.add(jda.getSelfUser().getIdLong());
        //only works for those admins who are added with their userId and not through a roleId
        for (String admin : Config.CONFIG.getAdminIds())
            whitelist.add(Long.valueOf(admin));


        //Create all the rate limiters we want
        ratelimits = new ArrayList<>();

        if (Config.CONFIG.useAutoBlacklist())
            autoBlacklist = new Blacklist(whitelist, RATE_LIMIT_HITS_BEFORE_BLACKLIST);

        //sort these by harsher limits coming first
        ratelimits.add(new Ratelimit(whitelist, Ratelimit.Scope.USER, 2, 30000, ShardsCommand.class));
        ratelimits.add(new Ratelimit(whitelist, Ratelimit.Scope.USER, 5, 20000, SkipCommand.class));
        ratelimits.add(new Ratelimit(whitelist, Ratelimit.Scope.USER, 5, 10000, Command.class));

        ratelimits.add(new Ratelimit(whitelist, Ratelimit.Scope.GUILD, 1000, 120000, PlaylistInfo.class));
        ratelimits.add(new Ratelimit(whitelist, Ratelimit.Scope.GUILD, 10, 10000, Command.class));
    }

    public Tuple2<Boolean, Class> isAllowed(Member invoker, Object command, int weight, TextChannel blacklistCallback) {
        for (Ratelimit ratelimit : ratelimits) {
            if (ratelimit.getClazz().isInstance(command)) {
                boolean allowed;
                //don't blacklist guilds
                if (ratelimit.scope == Ratelimit.Scope.GUILD) {
                    allowed = ratelimit.isAllowed(invoker, weight);
                } else {
                    allowed = ratelimit.isAllowed(invoker, weight, autoBlacklist, blacklistCallback);
                }
                if (!allowed) return new Tuple2<>(false, ratelimit.getClazz());
            }
        }
        return new Tuple2<>(true, null);
    }

    public boolean isBlacklisted(long id) {
        return autoBlacklist != null && autoBlacklist.isBlacklisted(id);
    }

    public void liftLimitAndBlacklist(long id) {
        for (Ratelimit ratelimit : ratelimits) {
            ratelimit.liftLimit(id);
        }
        if (autoBlacklist != null)
            autoBlacklist.liftBlacklist(id);
    }
}
