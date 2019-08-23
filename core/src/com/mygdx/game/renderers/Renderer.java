package com.mygdx.game.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;

public abstract class Renderer {
    MyGame game;
    OrthographicCamera camera;
    SpriteBatch batch;
    public Renderer(MyGame game, OrthographicCamera camera){
        this.game = game;
        this.camera = camera;
       // batch = game.batch;
    }
    public abstract void render();

    public abstract void dispose();
}
