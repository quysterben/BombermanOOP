package com.testniqatsu.bomberman.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.testniqatsu.bomberman.BombermanType;
import com.testniqatsu.bomberman.components.enemy.*;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.testniqatsu.bomberman.constants.GameConst.SIZE_BLOCK;

public class FlameComponent extends Component {
    private final AnimatedTexture texture;


    public FlameComponent(int startF, int endF) {
        PhysicsWorld physics = getPhysicsWorld();

        onCollisionBegin(BombermanType.FLAME, BombermanType.WALL, (f, w) -> {
            f.removeFromWorld();
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.AROUND_WALL, (f, w) -> {
            f.removeFromWorld();
        });

        setCollisionBreak(BombermanType.BRICK, "brick_break");

        setCollisionBreak(BombermanType.GRASS, "grass_break");

        setCollisionBreak(BombermanType.CORAL, "coral_break");

        onCollisionBegin(BombermanType.FLAME, BombermanType.BALLOOM_E, (f, b) -> {
            double x = b.getX();
            double y = b.getY();
            b.getComponent(BalloomComponent.class).enemyDie();
            getGameTimer().runOnceAfter(() -> {
                b.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(0.3));
            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1.5));
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.ONEAL_E, (f, o) -> {
            double x = o.getX();
            double y = o.getY();
            o.getComponent(OnealComponent.class).enemyDie();
            getGameTimer().runOnceAfter(() -> {
                o.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(0.3));
            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1.5));
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.DORIA_E, (f, d) -> {
            double x = d.getX();
            double y = d.getY();
            d.getComponent(DoriaComponent.class).enemyDie();
            getGameTimer().runOnceAfter(() -> {
                d.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(0.3));
            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1.5));
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.DAHL_E, (f, d) -> {
            double x = d.getX();
            double y = d.getY();
            d.getComponent(DahlComponent.class).enemyDie();
            getGameTimer().runOnceAfter(() -> {
                d.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(0.3));
            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1.5));
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.OVAPE_E, (f, o) -> {
            double x = o.getX();
            double y = o.getY();
            o.getComponent(OvapeComponent.class).enemyDie();
            getGameTimer().runOnceAfter(() -> {
                o.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(0.3));

            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1.5));
        });

        onCollisionBegin(BombermanType.FLAME, BombermanType.PASS_E, (f, pa) -> {
            double x = pa.getX();
            double y = pa.getY();
            pa.getComponent(PassComponent.class).enemyDie();
            getGameTimer().runOnceAfter(pa::removeFromWorld, Duration.seconds(0.3));

            Entity entity = spawn("enemyDie", new SpawnData(x, y));
            getGameTimer().runOnceAfter(() -> {
                switch (randomInt()) {
                    case 1, 3 -> spawn("balloom_e", new SpawnData(pa.getX(), pa.getY()));
                    case 2 -> spawn("dahl_e", new SpawnData(pa.getX(), pa.getY()));
                    case 4, 5 -> spawn("ovape_e", new SpawnData(pa.getX(), pa.getY()));
                    default -> {}
                }
                entity.removeFromWorld();
                set("numOfEnemy", getEnemies());
            }, Duration.seconds(1.5));
        });

        AnimationChannel animationFlame = new AnimationChannel(image("sprites.png"), 16, SIZE_BLOCK, SIZE_BLOCK,
                Duration.seconds(0.4), startF, endF);


        texture = new AnimatedTexture(animationFlame);
        texture.loop();
    }

    private int getEnemies() {
        return getGameWorld().getGroup(BombermanType.ONEAL_E, BombermanType.PASS_E, BombermanType.BALLOOM_E,
                BombermanType.DAHL_E, BombermanType.DORIA_E, BombermanType.OVAPE_E).getSize();
    }

    private void setCollisionBreak(BombermanType type, String nameTypeBreakAnim) {
        onCollisionBegin(BombermanType.FLAME, type, (f, t) -> {
            int cellX = (int)((t.getX() + 24) / SIZE_BLOCK);
            int cellY = (int)((t.getY() + 24) / SIZE_BLOCK);

            AStarGrid grid = geto("grid");
            grid.get(cellX, cellY).setState(CellState.WALKABLE);
            set("grid", grid);

            Entity bBreak = spawn(nameTypeBreakAnim, new SpawnData(t.getX(), t.getY()));
            t.removeFromWorld();
            getGameTimer().runOnceAfter(bBreak::removeFromWorld, Duration.seconds(1));
        });
    }

    // random 1 - 5
    private int randomInt() {
        return (int) ((Math.random()) * 5 + 1);
    }


    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }
}
