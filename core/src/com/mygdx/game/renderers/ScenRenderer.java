package com.mygdx.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;
import com.mygdx.game.controllers.PlayerInputProcessor;
import com.mygdx.game.models.food.BlockManager;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.models.player.MindCloud;
import com.mygdx.game.models.food.FoodManager;
import com.mygdx.game.screens.GameScreen;

import java.util.Random;

public class ScenRenderer extends Renderer {
    public MyGame game;
    private Fluffy fluffy;
    FoodManager foodManager;
    BlockManager blockManager;
    PlayerInputProcessor playerInputProcessor;
    MindCloud mindCloud;
    public ScenRenderer(MyGame game, OrthographicCamera camera, GameScreen gs) {
        super(game, camera);
        batch = new SpriteBatch();
        fluffy = gs.getFluffy();
        mindCloud = gs.getMindCloud();
        playerInputProcessor = new PlayerInputProcessor(game, gs, fluffy);
        Gdx.input.setInputProcessor(playerInputProcessor);
        foodManager = gs.getFoodManager();
        blockManager = gs.getBlockManager();
    }

    public Fluffy getFluffy() {
        return fluffy;
    }

    @Override
    public void render() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        fluffy.drawFluffy(batch);
        mindCloud.drawMindCloud(batch);
        if (new Random().nextInt(50) == 1) {
            foodManager.initFood();

        }
        foodManager.drawFoods(batch);
        foodManager.overlaps();
        foodManager.allFalls();
        batch.end();
    }

    @Override
    public void dispose(){
        Gdx.input.setInputProcessor(null);
        batch.dispose();
    }

}
