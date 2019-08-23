package com.mygdx.game.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;
import com.mygdx.game.models.store.Store;
import com.mygdx.game.models.store.Vitrine;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

public class StoreRenderer extends Renderer {
    private SpriteBatch batch;
    Store store;
    public StoreRenderer(MyGame game, OrthographicCamera camera, Store store) {
        super(game, camera);
        batch = new SpriteBatch();
        this.store = store;
    }

    @Override
    public void render() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
//        batch.draw(RTextures.store_vitrine, RDim.DEVICE_WIDTH/4, RDim.DEVICE_HEIGHT/6,
//                RDim.DEVICE_WIDTH/2, RDim.DEVICE_HEIGHT/2+RDim.DEVICE_HEIGHT/6);
        store.drawVitrines(batch);
        batch.draw(RTextures.back_arrow, 10, RDim.DEVICE_HEIGHT-60, 80, 60);
        batch.end();
    }

    @Override
    public void dispose() {

    }
}
