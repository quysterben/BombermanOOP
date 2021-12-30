package com.testniqatsu.bomberman;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.testniqatsu.bomberman.components.PlayerComponent;
import com.testniqatsu.bomberman.menus.BombermanGameMenu;
import com.testniqatsu.bomberman.menus.BombermanMenu;
import com.testniqatsu.bomberman.ui.BombermanHUD;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getPhysicsWorld;
import static com.testniqatsu.bomberman.constants.GameConst.*;

public class BombermanApp extends GameApplication {
    private static final int VIEW_WIDTH = 1080;
    private static final int VIEW_HEIGHT = 720;

    private static final String TITLE = "BOMBERMAN";
    private static final String VERSION = "1.0";

    private static final String FONT = "Retro Gaming.ttf";

    private static final int TIME_PER_LEVEL = 300;
    private static final int START_LEVEL = 0;

    public static boolean isSoundEnabled = true;
    private boolean requestNewGame = false;

    private static Entity getPlayer() {
        return FXGL.getGameWorld().getSingleton(BombermanType.PLAYER);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeight(VIEW_HEIGHT);
        settings.setWidth(VIEW_WIDTH);
        settings.setTitle(TITLE);
        settings.setVersion(VERSION);
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setManualResizeEnabled(true);
        settings.setDeveloperMenuEnabled(true);
        settings.setFontUI(FONT);
        settings.setSceneFactory(new SceneFactory() {
            @NotNull
            @Override
            public FXGLMenu newMainMenu() {
                return new BombermanMenu();
            }

            @NotNull
            @Override
            public FXGLMenu newGameMenu() {
                return new BombermanGameMenu();
            }
        });
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new BombermanFactory());
        loadNextLevel();
        FXGL.spawn("background");
        startCountDownTimer();
        getWorldProperties().<Integer>addListener("time", this::killPlayerWhenTimeUp);
    }

    private void startCountDownTimer() {
        run(() -> inc("time", -1), Duration.seconds(1));
    }

    private void killPlayerWhenTimeUp(int old, int now) {
        if (now == 0) {
            onPlayerKilled();
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("bomb", 1);
        vars.put("flame", 1);
        vars.put("score", 0);
        vars.put("speed", SPEED);
        vars.put("time", TIME_PER_LEVEL);
        vars.put("level", START_LEVEL);
        vars.put("immortality", false);
        vars.put("numOfEnemy", 10);
        vars.put("life", 3);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(isSoundEnabled ? 0.05 : 0.0);
        getSettings().setGlobalSoundVolume(isSoundEnabled ? 0.4 : 0.0);
        loopBGM("title_screen.mp3");
    }

    @Override
    protected void onUpdate(double tpf) {

        if (geti("time") == 0) {
            showMessage("Game Over leu leu!!!", () -> getGameController().gotoMainMenu());
        }

        if (requestNewGame) {
            requestNewGame = false;
            getPlayer().getComponent(PlayerComponent.class).die();
            getGameTimer().runOnceAfter(() -> getGameScene().getViewport().fade(() -> {
                if (geti("life") <= 0) {
                    showMessage("Game Over leu leu!!!",
                            () -> getGameController().gotoMainMenu());
                }
                setLevel();
                set("immortality", false);
            }), Duration.seconds(0.5));
        }
    }

    @Override
    protected void initUI() {
        var hud = new BombermanHUD();
        var leftMargin = 0;
        var topMargin = 0;
        getGameTimer().runOnceAfter(() -> FXGL.addUINode(hud.getHUD(), leftMargin, topMargin), Duration.seconds(3));

    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                getPlayer().getComponent(PlayerComponent.class).up();
            }

            @Override
            protected void onActionEnd() {
                getPlayer().getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                getPlayer().getComponent(PlayerComponent.class).down();
            }

            @Override
            protected void onActionEnd() {
                getPlayer().getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                getPlayer().getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                getPlayer().getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                getPlayer().getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                getPlayer().getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Place Bomb") {
            @Override
            protected void onActionBegin() {
                getPlayer().getComponent(PlayerComponent.class).placeBomb();
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();
        physics.setGravity(0, 0);

        onCollisionBegin(BombermanType.PLAYER, BombermanType.PORTAL, this::endLevel);
        onCollisionBegin(BombermanType.PLAYER, BombermanType.FLAME, (p, f) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.BALLOOM_E, (p, b) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.DAHL_E, (p, dh) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.ONEAL_E, (p, o) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.DORIA_E, (p, d) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.OVAPE_E, (p, o) -> onPlayerKilled());
        onCollisionBegin(BombermanType.PLAYER, BombermanType.PASS_E, (p, pa) -> onPlayerKilled());
    }

    private void endLevel(Entity player, Entity portal) {
        if (geti("numOfEnemy") > 0) return;
        play("next_level.wav");
        getPlayer().getComponent(PlayerComponent.class).setExploreCancel(true);
        var timer = FXGL.getGameTimer();
        timer.runOnceAfter(this::fadeToNextLevel, Duration.seconds(1));
    }

    private void fadeToNextLevel() {
        var gameScene = FXGL.getGameScene();
        var viewPort = gameScene.getViewport();
        viewPort.fade(this::loadNextLevel);
    }

    private void onPlayerKilled() {
        if (!getb("immortality")) {
            set("immortality", true);
            if (geti("life") > 0) inc("life", -1);
            set("score", 0);
            getPlayer().getComponent(PlayerComponent.class).setExploreCancel(true);
            requestNewGame = true;
        }
    }

    private void loadNextLevel() {
        if (FXGL.geti("level") >= MAX_LEVEL) {
            showMessage("You Win! bum bum bum!!!", () -> getGameController().gotoMainMenu());
        } else {
            getSettings().setGlobalMusicVolume(0);
            getInput().setProcessInput(false);

            play("stage_start.wav");
            inc("level", +1);
            AnchorPane pane = creStartStage();
            FXGL.addUINode(pane);
            getGameTimer().runOnceAfter(() -> {
                FXGL.removeUINode(pane);
                getSettings().setGlobalMusicVolume(0.05);
                getInput().setProcessInput(true);
                setLevel();
            }, Duration.seconds(3));
        }
    }

    private AnchorPane creStartStage() {
        AnchorPane pane = new AnchorPane();
        Shape shape = new Rectangle(1080, 720, Color.BLACK);

        var text = FXGL.getUIFactoryService().newText("STAGE " + geti("level"), Color.WHITE, 40);
        text.setTranslateX((SCREEN_WIDTH >> 1) - 80);
        text.setTranslateY((SCREEN_HEIGHT >> 1) - 20);
        pane.getChildren().addAll(shape, text);

        return pane;
    }

    private void setLevel() {
        FXGL.setLevelFromMap("bbm_level" + FXGL.geti("level") + ".tmx");

        Viewport viewport = getGameScene().getViewport();
        viewport.setBounds(0, 0, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
        viewport.bindToEntity(
                getPlayer(),
                FXGL.getAppWidth() / 2.0f,
                FXGL.getAppHeight() / 2.0f);
        viewport.setLazy(true);
        set("time", TIME_PER_LEVEL);
        set("bomb", 1);
        set("flame", 1);
        set("numOfEnemy", 10);
        setGridForAi();
    }

    private void setGridForAi() {
        AStarGrid grid = AStarGrid.fromWorld(getGameWorld(), 31, 15,
                SIZE_BLOCK, SIZE_BLOCK, (type) -> {
                    if (type == BombermanType.BRICK
                            || type == BombermanType.WALL
                            || type == BombermanType.GRASS
                            || type == BombermanType.CORAL
                            || type == BombermanType.AROUND_WALL) {
                        return CellState.NOT_WALKABLE;
                    } else {
                        return CellState.WALKABLE;
                    }
                });

        AStarGrid _grid = AStarGrid.fromWorld(getGameWorld(), 31, 15,
                SIZE_BLOCK, SIZE_BLOCK, (type) -> {
                    if (type == BombermanType.AROUND_WALL || type == BombermanType.WALL) {
                        return CellState.NOT_WALKABLE;
                    } else {
                        return CellState.WALKABLE;
                    }
                });

        set("grid", grid);
        set("_grid", _grid);
    }
}

