package io.github.fourlastor.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.SnapshotArray;
import java.util.Comparator;

public class YSort extends Group {

    private static final Comparator<Actor> Y_COMPARATOR = (o1, o2) -> -Float.compare(o1.getY(), o2.getY());

    @Override
    public void act(float delta) {
        super.act(delta);
        SnapshotArray<Actor> children = getChildren();
        children.sort(Y_COMPARATOR);
    }
}
