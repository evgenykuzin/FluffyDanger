package com.mygdx.game.models.hud;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.models.food.Food;
import com.mygdx.game.resources.RTextures;
import com.mygdx.game.screens.GameScreen;

import java.util.ArrayList;

public class Jeludok {
    private Vector2 position;
    private int width;
    private int height;
    ArrayList<Food> foods;
    GameScreen gs;
    public Jeludok(GameScreen gs) {
        position = new Vector2(230,-25);
        width = 150;
        height = 100;
        foods = new ArrayList<Food>();
        this.gs = gs;
    }

    public void drawJeludok(SpriteBatch batch){
        batch.draw(RTextures.jeludok, position.x, position.y, width, height);
        int posX = 5;
        for (Food food: foods){
            posX+=25;
            batch.draw(food.getTexture(), position.x+posX, position.y+25, width/5, width/5);
        }
    }

    public void add(Food food){
        int count = 0;
        if (foods.size() <= 2) {
            foods.add(food);
        } else {
            foods.add(food);
            for (int i = 0; i<foods.size(); i++){
                if (gs.getMindCloud().getFoods().get(i).foodType.equals(foods.get(i).foodType)){
                    count++;
                }
            }
            if (count == 4) {
                gs.getHud().addMoney(25);
                gs.getMindCloud().generate();
            } else {
                gs.getHud().addMoney(-25);
                gs.getMindCloud().generate();
            }
            foods.clear();
        }
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public void clear(){
        foods.clear();
    }

}
