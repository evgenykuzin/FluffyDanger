package com.mygdx.game.models.food;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.models.hud.Hud;
import com.mygdx.game.models.hud.Jeludok;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.models.player.MindCloud;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.Random;

public class BlockManager {
    private GameScreen gs;
    public ArrayList<Food> blockList;
    private Fluffy fluffy;
    private Hud hud;
    private ArrayList<Food> deadList;

    public BlockManager(GameScreen gs) {
        this.gs = gs;
        blockList = new ArrayList<Food>();
        deadList = new ArrayList<Food>();
        fluffy = gs.getFluffy();
        hud = gs.getHud();
    }

    public void drawBlocks(SpriteBatch batch) {
        if (!blockList.isEmpty()) {
            for (Food food : blockList) {
                food.drawFood(batch);
            }
        }
    }

    public void allFalls() {
        ArrayList<Food> deadList = new ArrayList<Food>();
        if (!blockList.isEmpty()) {
            for (Food food : blockList) {
                food.fall();
                if (food.position.y > RDim.DEVICE_HEIGHT) {
                    deadList.add(food);
                }
            }
            blockList.removeAll(deadList);
            deadList.clear();
        }
    }

    public void initBlocks() {
        Random random = new Random();
        float rx = random.nextInt(600);
        Food food = new Food(new Vector2(rx, RDim.DEVICE_HEIGHT));
        blockList.add(food);
    }

    public void overlaps() {
        Food eated = null;
        for (Food food : blockList) {
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
    }
}
