package vertgreen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class GitRepoState {

    private static final Logger log = LoggerFactory.getLogger(GitRepoState.class);

    private static GitRepoState gitRepoState;

    public static GitRepoState getGitRepositoryState() {
        if (gitRepoState == null) {
            Properties properties = new Properties();
            try {
                properties.load(GitRepoState.class.getClassLoader().getResourceAsStream("git.properties"));
                gitRepoState = new GitRepoState(properties);
            } catch (NullPointerException | IOException e) {
                log.error("Failed to load git repo information", e);
            }
        }
        return gitRepoState;
    }

    public final String tags;
    public final String branch;
    public final String dirty;
    public final String remoteOriginUrl;

    public final String commitId;
    public final String commitIdAbbrev;
    public final String describe;
    public final String describeShort;
    public final String commitUserName;
    public final String commitUserEmail;
    public final String commitMessageFull;
    public final String commitMessageShort;
    public final String commitTime;
    public final String closestTagName;
    public final String closestTagCommitCount;

    public final String buildUserName;
    public final String buildUserEmail;
    public final String buildTime;
    public final String buildHost;
    public final String buildVersion;

    @SuppressWarnings("ConstantConditions")
    public GitRepoState(Properties properties) {
        this.tags = String.valueOf(properties.get("git.tags"));
        this.branch = String.valueOf(properties.get("git.branch"));
        this.dirty = String.valueOf(properties.get("git.dirty"));
        this.remoteOriginUrl = String.valueOf(properties.get("git.remote.origin.url"));

        this.commitId = String.valueOf(properties.get("git.commit.id.full")); // OR properties.get("git.commit.id") depending on your configuration
        this.commitIdAbbrev = String.valueOf(properties.get("git.commit.id.abbrev"));
        this.describe = String.valueOf(properties.get("git.commit.id.describe"));
        this.describeShort = String.valueOf(properties.get("git.commit.id.describe-short"));
        this.commitUserName = String.valueOf(properties.get("git.commit.user.name"));
        this.commitUserEmail = String.valueOf(properties.get("git.commit.user.email"));
        this.commitMessageFull = String.valueOf(properties.get("git.commit.message.full"));
        this.commitMessageShort = String.valueOf(properties.get("git.commit.message.short"));
        this.commitTime = String.valueOf(properties.get("git.commit.time"));
        this.closestTagName = String.valueOf(properties.get("git.closest.tag.name"));
        this.closestTagCommitCount = String.valueOf(properties.get("git.closest.tag.commit.count"));

        this.buildUserName = String.valueOf(properties.get("git.build.user.name"));
        this.buildUserEmail = String.valueOf(properties.get("git.build.user.email"));
        this.buildTime = String.valueOf(properties.get("git.build.time"));
        this.buildHost = String.valueOf(properties.get("git.build.host"));
        this.buildVersion = String.valueOf(properties.get("git.build.version"));
    }
}
