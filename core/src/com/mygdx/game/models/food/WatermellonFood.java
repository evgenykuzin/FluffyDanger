package com.mygdx.game.models.food;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.resources.RTextures;

public class WatermellonFood extends Food {

    public WatermellonFood(Vector2 position) {
        super(position);
    }

    @Override
    public void initialize() {
        speed = 0.5f;
        width = 32;
        height = 32;
        textureRegion = new TextureRegion(RTextures.watermellon);
    }
}
