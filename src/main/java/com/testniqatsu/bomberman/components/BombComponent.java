package com.testniqatsu.bomberman.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.testniqatsu.bomberman.BombermanType;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.onCollisionEnd;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.testniqatsu.bomberman.constants.GameConst.SIZE_BLOCK;

public class BombComponent extends Component {
    private ArrayList<Entity> listFire = new ArrayList<>();
    Entity wallBomb;

    private AnimatedTexture texture;
    private AnimationChannel animation;

    private List<Entity> listRightBlock = new ArrayList<>();
    private List<Entity> listLeftBlock = new ArrayList<>();
    private List<Entity> listAboveBlock = new ArrayList<>();
    private List<Entity> listBottomBlock = new ArrayList<>();

    public BombComponent() {
        onCollisionEnd(BombermanType.BOMB, BombermanType.PLAYER, (b, p) -> {
            if (entity != null) {
                wallBomb = spawn("wall_bomb", new SpawnData(entity.getX(), entity.getY()));
            }
        });

        animation = new AnimationChannel(image("sprites.png"), 16, SIZE_BLOCK, SIZE_BLOCK,
                Duration.seconds(0.5), 72, 74);
        texture = new AnimatedTexture(animation);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    public void explode() {
        int flameLength = geti("flame");

        listFire.add(spawn("centerFlame", new SpawnData(entity.getX(), entity.getY())));

        //haven't ate flame items
        if (flameLength == 1) {
            listFire.add(spawn("rightEFlame"
                    , new SpawnData(entity.getX() + SIZE_BLOCK * flameLength, entity.getY())));
            listFire.add(spawn("leftEFlame"
                    , new SpawnData(entity.getX() - SIZE_BLOCK * flameLength, entity.getY())));
            listFire.add(spawn("bottomEFlame"
                    , new SpawnData(entity.getX(), entity.getY() + SIZE_BLOCK * flameLength)));
            listFire.add(spawn("aboveEFlame"
                    , new SpawnData(entity.getX(), entity.getY() - SIZE_BLOCK * flameLength)));
            listFire.add(spawn("centerFlame"
                    , new SpawnData(entity.getX(), entity.getY())));
        } else {
            spawnRightF(flameLength);
            spawnLeftF(flameLength);
            spawnAboveF(flameLength);
            spawnBottomF(flameLength);
        }

        //clear Flame
        clearFlame();
    }

    public void clearFlame() {
        FXGL.getGameTimer().runOnceAfter(() -> {
            for (Entity value : listFire) {
                value.removeFromWorld();
            }
        }, Duration.seconds(0.2));
        if (wallBomb != null) wallBomb.removeFromWorld();
        entity.removeFromWorld();
    }

    // Spawn Flames

    public void getRightBlocks() {
        List<Entity> temp = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.WALL));
        for (Entity value : temp) {
            if (value.getY() == entity.getY() - 1 && value.getX() >= entity.getX()) {
                listRightBlock.add(value);
            }
        }

        List<Entity> temp1 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.BRICK));
        for (Entity value : temp1) {
            if (value.getY() == entity.getY() - 1 && value.getX() >= entity.getX()) {
                listRightBlock.add(value);
            }
        }

        List<Entity> temp2 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.CORAL));
        for (Entity value : temp2) {
            if (value.getY() == entity.getY() - 1 && value.getX() >= entity.getX()) {
                listRightBlock.add(value);
            }
        }

        List<Entity> temp3 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.GRASS));
        for (Entity value : temp3) {
            if (value.getY() == entity.getY() - 1 && value.getX() >= entity.getX()) {
                listRightBlock.add(value);
            }
        }
    }

    private void spawnRightF(int flameLength) {
        getRightBlocks();
        for (int i = 1; i <= flameLength; i++) {
            if (i == flameLength) {
                listFire.add(spawn("rightEFlame"
                        , new SpawnData(entity.getX() + SIZE_BLOCK * i, entity.getY())));
                break;
            }

            boolean isContinued = true;
            Entity checkF = spawn("check_flame"
                    , new SpawnData(entity.getX() + SIZE_BLOCK * (i - 1), entity.getY()));

            for (Entity value : listRightBlock) {
                if (checkF.isColliding(value)) {
                    isContinued = false;
                    break;
                }
            }

            checkF.removeFromWorld();

            if (isContinued) {
                listFire.add(spawn("horizontalFlame"
                        , new SpawnData(entity.getX() + SIZE_BLOCK * i, entity.getY())));
            } else {
                listFire.add(spawn("rightEFlame"
                        , new SpawnData(entity.getX() + SIZE_BLOCK * i, entity.getY())));
                break;
            }
        }
    }

    private void getLeftBlocks() {
        List<Entity> temp = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.WALL));
        for (Entity value : temp) {
            if (value.getY() == entity.getY() - 1 && value.getX() < entity.getX()) {
                listLeftBlock.add(value);
            }
        }

        List<Entity> temp1 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.BRICK));
        for (Entity value : temp1) {
            if (value.getY() == entity.getY() - 1 && value.getX() < entity.getX()) {
                listLeftBlock.add(value);
            }
        }

        List<Entity> temp2 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.CORAL));
        for (Entity value : temp2) {
            if (value.getY() == entity.getY() - 1 && value.getX() <entity.getX()) {
                listLeftBlock.add(value);
            }
        }

        List<Entity> temp3 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.GRASS));
        for (Entity value : temp3) {
            if (value.getY() == entity.getY() - 1 && value.getX() < entity.getX()) {
                listLeftBlock.add(value);
            }
        }
    }

    private void spawnLeftF(int flameLength) {
        getLeftBlocks();
        for (int i = 1; i <= flameLength; i++) {
            if (i == flameLength) {
                listFire.add(spawn("leftEFlame"
                        , new SpawnData(entity.getX() - SIZE_BLOCK * i, entity.getY())));
                break;
            }

            boolean isContinued = true;
            Entity checkF = spawn("check_flame"
                    , new SpawnData(entity.getX() - SIZE_BLOCK * (i), entity.getY()));

            for (Entity value : listLeftBlock) {
                if (checkF.isColliding(value)) {
                    isContinued = false;
                    break;
                }
            }

            checkF.removeFromWorld();

            if (isContinued) {
                listFire.add(spawn("horizontalFlame"
                        , new SpawnData(entity.getX() - SIZE_BLOCK * i, entity.getY())));
            } else {
                listFire.add(spawn("leftEFlame"
                        , new SpawnData(entity.getX() - SIZE_BLOCK * i, entity.getY())));
                break;
            }
        }
    }

    private void getBottomBlocks() {
        List<Entity> temp = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.WALL));
        for (Entity value : temp) {
            if (value.getX() == entity.getX() - 1 && value.getY() > entity.getY()) {
                listBottomBlock.add(value);
            }
        }

        List<Entity> temp1 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.BRICK));
        for (Entity value : temp1) {
            if (value.getX() == entity.getX() - 1 && value.getY() > entity.getY()) {
                listBottomBlock.add(value);
            }
        }

        List<Entity> temp2 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.CORAL));
        for (Entity value : temp2) {
            if (value.getX() == entity.getX() - 1 && value.getY() > entity.getY()) {
                listBottomBlock.add(value);
            }
        }

        List<Entity> temp3 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.GRASS));
        for (Entity value : temp3) {
            if (value.getX() == entity.getX() - 1 && value.getY() > entity.getY()) {
                listBottomBlock.add(value);
            }
        }
    }

    private void spawnBottomF(int flameLength) {
        getBottomBlocks();
        for (int i = 1; i <= flameLength; i++) {
            if (i == flameLength) {
                listFire.add(spawn("bottomEFlame"
                        , new SpawnData(entity.getX(), entity.getY() + SIZE_BLOCK * flameLength)));
                break;
            }

            boolean isContinued = true;
            Entity checkF = spawn("check_flame"
                    , new SpawnData(entity.getX(), entity.getY() + SIZE_BLOCK * (i - 1)));

            for (Entity value : listBottomBlock) {
                if (checkF.isColliding(value)) {
                    isContinued = false;
                    break;
                }
            }

            checkF.removeFromWorld();

            if (isContinued) {
                listFire.add(spawn("verticalFlame"
                        , new SpawnData(entity.getX(), entity.getY() + SIZE_BLOCK * i)));
            } else {
                listFire.add(spawn("bottomEFlame"
                        , new SpawnData(entity.getX(), entity.getY() + SIZE_BLOCK * i)));
                break;
            }
        }
    }

    private void getAboveBlocks() {
        List<Entity> temp = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.WALL));
        for (Entity value : temp) {
            if (value.getX() == entity.getX() - 1 && value.getY() < entity.getY()) {
                listAboveBlock.add(value);
            }
        }

        List<Entity> temp1 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.BRICK));
        for (Entity value : temp1) {
            if (value.getX() == entity.getX() - 1 && value.getY() < entity.getY()) {
                listAboveBlock.add(value);
            }
        }

        List<Entity> temp2 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.CORAL));
        for (Entity value : temp2) {
            if (value.getX() == entity.getX() - 1 && value.getY() < entity.getY()) {
                listAboveBlock.add(value);
            }
        }

        List<Entity> temp3 = new ArrayList<>(getGameWorld().getEntitiesByType(BombermanType.GRASS));
        for (Entity value : temp3) {
            if (value.getX() == entity.getX() - 1 && value.getY() < entity.getY()) {
                listAboveBlock.add(value);
            }
        }
    }

    private void spawnAboveF(int flameLength) {
        getAboveBlocks();

        for (int i = 1; i <= flameLength; i++) {
            if (i == flameLength) {
                listFire.add(spawn("aboveEFlame"
                        , new SpawnData(entity.getX(), entity.getY() - SIZE_BLOCK * flameLength)));
                break;
            }

            boolean isContinued = true;
            Entity checkF = spawn("check_flame"
                    , new SpawnData(entity.getX(), entity.getY() - SIZE_BLOCK * (i - 1) - SIZE_BLOCK / 2));

            for (Entity value : listAboveBlock) {
                if (checkF.isColliding(value)) {
                    isContinued = false;
                    break;
                }
            }

            if (isContinued && checkF.getY() > 97 + SIZE_BLOCK) {
                listFire.add(spawn("verticalFlame"
                        , new SpawnData(entity.getX(), entity.getY() - SIZE_BLOCK * i)));
                checkF.removeFromWorld();
            } else {
                listFire.add(spawn("aboveEFlame"
                        , new SpawnData(entity.getX(), entity.getY() - SIZE_BLOCK * i)));
                checkF.removeFromWorld();
                break;
            }

        }
    }
}