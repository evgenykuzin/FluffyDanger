package com.mygdx.game.models.food;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.models.player.MindCloud;
import com.mygdx.game.models.hud.Hud;
import com.mygdx.game.models.hud.Jeludok;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.Random;

public class FoodManager {
    private GameScreen gs;
    public ArrayList<Food> foodList;
    private Fluffy fluffy;
    private Hud hud;
    private ArrayList<Food> deadList;

    public FoodManager(GameScreen gs) {
        this.gs = gs;
        foodList = new ArrayList<Food>();
        deadList = new ArrayList<Food>();
        fluffy = gs.getFluffy();
        hud = gs.getHud();
    }

    public void drawFoods(SpriteBatch batch) {
        if (!foodList.isEmpty()) {
            for (Food food : foodList) {
                food.drawFood(batch);
            }
        }
    }

    public void allFalls() {
        ArrayList<Food> deadList = new ArrayList<Food>();
        if (!foodList.isEmpty()) {
            for (Food food : foodList) {
                food.fall();
                if (food.position.y > RDim.DEVICE_HEIGHT) {
                    deadList.add(food);
                }
            }
            foodList.removeAll(deadList);
            deadList.clear();
        }
    }

    public void initFood() {
        Random random = new Random();
        float rx = random.nextInt(600);
        Food food = new Food(new Vector2(rx, RDim.DEVICE_HEIGHT));
        foodList.add(food);
    }

    public void overlaps() {
        Food eated = null;
        for (Food food : foodList) {
            if (!deadList.contains(food)) {
                if (food.position.x >= fluffy.getPosition().x
                        && food.position.y >= fluffy.getPosition().y
                        && food.position.x <= fluffy.getPosition().x + fluffy.getWidth()
                        && food.position.y <= fluffy.getPosition().y + fluffy.getHeight()
                ) {
                    eated = food;
                    Jeludok jeludok = gs.getHud().getJeludok();
                    MindCloud mindCloud = gs.getMindCloud();
                    int indx = 0;
                    if (!jeludok.getFoods().isEmpty()) {
                        indx = jeludok.getFoods().size();
                    }
                    if (!mindCloud.getFoods().get(jeludok.getFoods().size()).foodType.equals(eated.foodType)) {
                        hud.missHealth(1);
                        jeludok.clear();
                        mindCloud.generate();
                    } else {
                        jeludok.add(food);
                    }

                    break;
                }
            }
        }
        foodList.remove(eated);
    }
}
