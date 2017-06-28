package vertgreen.feature.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum FeatureFlags implements Feature {

    //ratelimiter + auto blacklisting features
    @Label("Rate Limiter")
    @EnabledByDefault
    RATE_LIMITER,

    //using the chatbot class
    @Label("Chatbot")
    @EnabledByDefault
    CHATBOT;

    public boolean isActive() {
        return FeatureConfig.getTheFeatureManager().isActive(this);
    }
}
