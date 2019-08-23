package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGame;
import com.mygdx.game.controllers.StoreInputProcessor;
import com.mygdx.game.models.store.Store;
import com.mygdx.game.renderers.StoreRenderer;
import com.mygdx.game.resources.RDim;

public class StoreScreen implements Screen {
    private StoreInputProcessor storeInputProcessor;
    private StoreRenderer storeRenderer;
    private OrthographicCamera camera;
    public static Sprite backArrow;
    private Store store;
    public StoreScreen(MyGame game){
        camera = new OrthographicCamera(RDim.DEVICE_WIDTH, RDim.DEVICE_HEIGHT);
        setCamera(RDim.DEVICE_WIDTH / 2f, RDim.DEVICE_HEIGHT / 2f);
        //camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backArrow = new Sprite();
        backArrow.setBounds(10,10,
                80, 60);
        store = new Store();
        storeRenderer = new StoreRenderer(game, camera, store);
        storeInputProcessor = new StoreInputProcessor(game, this);
        Gdx.input.setInputProcessor(storeInputProcessor);
    }

    public void setCamera(float x, float y) {
        this.camera.position.set(x, y, 0);
        this.camera.update();
    }

    public Store getStore(){
        return store;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        storeRenderer.render();
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

    }
}
