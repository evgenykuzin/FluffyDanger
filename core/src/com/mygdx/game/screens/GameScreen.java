package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGame;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.models.player.MindCloud;
import com.mygdx.game.models.food.FoodManager;
import com.mygdx.game.models.hud.Hud;
import com.mygdx.game.renderers.HudRenderer;
import com.mygdx.game.renderers.ScenRenderer;
import com.mygdx.game.resources.RDim;

public class GameScreen implements Screen {
    public MyGame game;
    private OrthographicCamera camera;
    private ScenRenderer scenRenderer;
    private HudRenderer hudRenderer;
    private Fluffy fluffy;
    private Hud hud;
    private FoodManager foodManager;
    private MindCloud mindCloud;
    public GameScreen(MyGame game){
        this.game = game;
        camera = new OrthographicCamera(RDim.DEVICE_WIDTH, RDim.DEVICE_HEIGHT);
        setCamera(RDim.DEVICE_WIDTH / 2f, RDim.DEVICE_HEIGHT / 2f);
        fluffy = new Fluffy(new Vector2().set(RDim.DEVICE_WIDTH / 2 - RDim.FLUFFY_WIDTH / 2, 64));
        mindCloud = new MindCloud(fluffy);
        hud = new Hud(this);
        foodManager = new FoodManager(this);
        scenRenderer = new ScenRenderer(game, camera, this);
        hudRenderer = new HudRenderer(game, camera, this);
    }

    public void setCamera(float x, float y) {
        this.camera.position.set(x, y, 0);
        this.camera.update();
    }

    public void endGame(){
        if (hud.getHealth() <= 0){
            game.setScreen(new MainMenuScreen(game));
        }
    }

    public Fluffy getFluffy(){
        return fluffy;
    }

    public Hud getHud(){
        return hud;
    }

    public MindCloud getMindCloud(){
        return mindCloud;
    }

    public FoodManager getFoodManager(){
        return foodManager;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        scenRenderer.render();
        hudRenderer.render();
        endGame();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        scenRenderer.dispose();
        hudRenderer.dispose();
    }
}
