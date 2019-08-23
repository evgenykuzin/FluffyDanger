package com.mygdx.game.models.food;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.resources.RTextures;

import java.util.Random;

public class Food extends Sprite {
    TextureRegion textureRegion;
    Vector2 position;
    int width;
    int height;
    float speed;
    public enum TYPE{
        APPLE,
        WATERMELLON,
        ORANGE,
        BANANA
    }
    public Enum<TYPE> foodType;
    public Food(Vector2 position){
        this.position = position;
        foodType = null;
        textureRegion = new TextureRegion();
        initialize();
        setBounds(position.x, position.y, width, height);
        setTexture(textureRegion.getTexture());
    }

    public void initialize(){
        speed = 0.5f;
        width = 32;
        height = 32;
        Random random = new Random();
        while (foodType == null) {
            for (Enum<TYPE> type : TYPE.values()) {
                if (random.nextInt(7) == 1) {
                    foodType = type;
                    break;
                }
            }
        }
        if (foodType.equals(TYPE.APPLE)){
            textureRegion.setRegion(RTextures.apple);
        } else if (foodType.equals(TYPE.WATERMELLON)){
            textureRegion.setRegion(RTextures.watermellon);
        } else if (foodType.equals(TYPE.ORANGE)){
            textureRegion.setRegion(RTextures.orange);
        } else if (foodType.equals(TYPE.BANANA)){
            textureRegion.setRegion(RTextures.banana);
        }
    }

    public void drawFood(SpriteBatch batch){
        batch.draw(textureRegion, position.x, position.y, width, height);
    }

    public void fall(){
        speed += 0.005f;
        position.y -= speed;
    }

    public Texture getTexture(){
        return textureRegion.getTexture();
    }

//    @Override
//    public boolean equals(Object o){
//        if (o == this) return true;
//        if (o == null || o.getClass() != this.getClass()) return false;
//        Food f = (Food) o;
//        return f.foodType.equals(this.foodType);
//
//    }

    public Enum<TYPE> getFoodType(){
        return foodType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((foodType == null) ? 0 : foodType.hashCode());
        result = prime * result + width + height;
        result = prime * result + ((textureRegion == null) ? 0 : textureRegion.hashCode());
        return result;
    }


}
