package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGame;
import com.mygdx.game.controllers.MainMenuInputProcessor;
import com.mygdx.game.renderers.MainMenuRenderer;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

public class MainMenuScreen implements Screen {
    public MyGame game;
    private OrthographicCamera camera;
    private MainMenuRenderer mainMenuRenderer;
    public float start_x = RDim.mm_start_width;
    public float start_y = 300;
    public float store_x = RDim.mm_store_width;
    public float store_y = 200;
    public float settings_x = RDim.mm_settings_widdth;
    public float settings_y = 100;
    public Sprite startButton;
    public Sprite storeButton;
    public Sprite settingsButton;
    public MainMenuScreen(MyGame game){
        this.game = game;
        camera = new OrthographicCamera(game.deviceWidth, game.deviceHeight);
        setCamera(game.deviceWidth / 2f, game.deviceHeight / 2f);
        mainMenuRenderer = new MainMenuRenderer(game, camera);
        startButton = new Sprite();
        storeButton = new Sprite();
        settingsButton = new Sprite();
        startButton.setBounds(start_x, RDim.DEVICE_HEIGHT - RDim.mm_start_height - start_y,
                RDim.mm_start_width, RDim.mm_start_height);

        storeButton.setBounds(store_x, RDim.DEVICE_HEIGHT - RDim.mm_store_height - store_y,
                RDim.mm_store_width, RDim.mm_store_height);

        settingsButton.setBounds(settings_x, RDim.DEVICE_HEIGHT - RDim.mm_settings_height - settings_y,
                RDim.mm_settings_widdth, RDim.mm_settings_height);

        MainMenuInputProcessor mainMenuInputProcessor
                = new MainMenuInputProcessor(this, mainMenuRenderer);
        Gdx.input.setInputProcessor(mainMenuInputProcessor);
    }

    public void setCamera(float x, float y) {
        this.camera.position.set(x, y, 0);
        this.camera.update();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();


        mainMenuRenderer.render();
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
        Gdx.input.setInputProcessor(null);
        mainMenuRenderer.dispose();
    }
}
