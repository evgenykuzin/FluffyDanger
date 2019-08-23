package com.mygdx.game.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;
import com.mygdx.game.models.hud.Hud;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RFonts;
import com.mygdx.game.resources.RTextures;
import com.mygdx.game.screens.GameScreen;

public class HudRenderer extends Renderer {
    private Hud hud;
    BitmapFont bitmapFont;
    public HudRenderer(MyGame game, OrthographicCamera camera, GameScreen gs) {
        super(game, camera);
        batch = new SpriteBatch();
        hud = gs.getHud();
        bitmapFont = new BitmapFont(RFonts.px25);
        bitmapFont.setColor(Color.GOLD);
    }

    @Override
    public void render() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        bitmapFont.draw(batch, "score " + hud.getMoney(), 15, 20);
        hud.getJeludok().drawJeludok(batch);
        batch.draw(RTextures.hud_pause_btn, 10, RDim.DEVICE_HEIGHT-RDim.hud_pause_height, RDim.hud_pause_width, RDim.hud_pause_height);
        if (hud.isPauseViewShown()){
            batch.draw(RTextures.hud_pause_view, RDim.DEVICE_WIDTH/4, RDim.FLUFFY_HEIGHT, 300, 400);
            batch.draw(RTextures.hud_quit, 220, 200, 200, 100);
        }
        int posX = 400;
        for (int i = 0; i < hud.getHealth(); i++){
            posX += 40;
            batch.draw(RTextures.heart, posX, 5, 32, 32);
        }
        batch.end();
    }

    @Override
    public void dispose(){
        bitmapFont.dispose();
        batch.dispose();
    }

}

