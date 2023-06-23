package io.github.fourlastor.game.di.modules;

import dagger.Module;
import dagger.Provides;

@Module
public class LogModule {
    private final boolean logEnabled;

    public LogModule(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    @Provides
    @LogEnabled
    public boolean isLogEnabled() {
        return logEnabled;
    }
}
