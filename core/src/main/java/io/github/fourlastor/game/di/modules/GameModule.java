package io.github.fourlastor.game.di.modules;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import squidpony.squidmath.GWTRNG;

@Module
public class GameModule {

    @Provides
    @Singleton
    public GWTRNG random() {
        return new GWTRNG();
    }
}
