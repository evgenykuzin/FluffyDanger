package com.mygdx.game.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

public class MainMenuRenderer extends Renderer {

    public float start_x = RDim.mm_start_width;
    public float start_y = 300;
    public float store_x = RDim.mm_store_width;
    public float store_y = 200;
    public float settings_x = RDim.mm_settings_widdth;
    public float settings_y = 100;

    public MainMenuRenderer(MyGame game, OrthographicCamera camera) {
        super(game, camera);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.draw(RTextures.mm_bg, 0, 0, RDim.mm_bg_width, RDim.mm_bg_height);
        batch.draw(RTextures.mm_start_btn, start_x, start_y, RDim.mm_start_width, RDim.mm_start_height);
        batch.draw(RTextures.mm_store_btn, store_x, store_y, RDim.mm_store_width, RDim.mm_store_height);
        batch.draw(RTextures.mm_settings_btn, settings_x, settings_y, RDim.mm_settings_widdth, RDim.mm_settings_height);
        batch.end();
    }

    @Override
    public void dispose(){
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        RTextures.mm_bg.dispose();
//        RTextures.mm_start_btn.dispose();
//        RTextures.mm_store_btn.dispose();
//        RTextures.mm_settings_btn.dispose();
        //batch.dispose();
    }
}
