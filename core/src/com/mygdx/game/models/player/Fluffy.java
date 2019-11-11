package com.mygdx.game.models.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.resources.Prefs;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

import java.util.Random;

public class Fluffy extends Sprite {
    private TextureRegion textureRegion;
    private Vector2 position;
    private int width;
    private int height;
    private float speed;
    private Prefs prefs;
    public Fluffy(Vector2 position){
        this.position = position;
        prefs = new Prefs();
        //textureRegion = new TextureRegion(RTextures.fluffyOne);
        textureRegion = new TextureRegion(prefs.getFluffyTexture());
        width = RDim.FLUFFY_WIDTH;
        height = RDim.FLUFFY_HEIGHT;
        speed = 10.0f;
        setBounds(position.x, position.y, width, height);
        setTexture(textureRegion.getTexture());
    }


    public void changeTexture(boolean b){
            if (b) {
                textureRegion = new TextureRegion(RTextures.openmouthplayer);
            } else {
                //System.out.println("change 2");
                textureRegion = new TextureRegion(RTextures.closemouthplayer);
            }
    }

    public Vector2 getPosition(){
        return position;
    }

    public void drawFluffy(SpriteBatch batch){
        batch.draw(textureRegion, position.x, position.y, width, height);
    }

    public void moveRight(){
        if (position.x < RDim.DEVICE_WIDTH-width) {
            position.x += speed;
        }
    }

    public void moveLeft(){
        if (position.x > 0) {
            position.x -= speed;
        }
    }

    public void move(float x){
        position.x = x;
    }

}
