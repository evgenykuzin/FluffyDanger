package com.mygdx.game.models.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.models.food.Food;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

import java.util.ArrayList;

public class MindCloud {
    private Vector2 position;
    private int width;
    private int height;
    private Fluffy fluffy;
    private ArrayList<Food> foods;
    public MindCloud(Fluffy fluffy){
        this.fluffy = fluffy;
        width = 150;
        height = 100;
        position = new Vector2();
        foods = new ArrayList<Food>();
        generate();
    }
    public void generate(){
       // ArrayList<Food> foods = new ArrayList<Food>();
        foods.clear();
         if (foods.size() < 4) {
            for (int i = 0; i < 4; i++) {
                Food food = new Food(new Vector2());
                foods.add(food);
            }
        }
    }

    public ArrayList<Food> getFoods(){
        return foods;
    }

    public void drawMindCloud(SpriteBatch batch){
        position.x = fluffy.getPosition().x - RDim.FLUFFY_WIDTH/2 - 60;
        position.y = fluffy.getPosition().y - RDim.FLUFFY_HEIGHT/2 - 30;
        batch.draw(RTextures.mindCloud, position.x , position.y, width, height);
        float posX = position.x + 5;
        for (Food food: foods){
            posX += 25;
            batch.draw(food.getTexture(), posX, position.y + 30
                    , width/5,height/5);
        }
    }
}
