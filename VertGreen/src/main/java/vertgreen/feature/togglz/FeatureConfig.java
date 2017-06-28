package vertgreen.feature.togglz;

import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.spi.FeatureManagerProvider;
import org.togglz.core.user.NoOpUserProvider;

import java.io.File;

public class FeatureConfig implements FeatureManagerProvider {

    private static FeatureManager featureManager;

    @Override
    public FeatureManager getFeatureManager() {
        return getTheFeatureManager();
    }

    public static FeatureManager getTheFeatureManager() {
        if (featureManager == null) {
            featureManager = new FeatureManagerBuilder()
                    .featureEnum(FeatureFlags.class)
                    .stateRepository(new FileBasedStateRepository(new File("./feature_flags.properties")))
                    .userProvider(new NoOpUserProvider())
                    .build();
        }
        return featureManager;
    }

    @Override
    public int priority() {
        return 0;
    }
}
