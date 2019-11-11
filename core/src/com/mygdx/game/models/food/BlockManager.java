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
    private ArrayList<Food> towerList;
    private Food topBlock;
    private float towerSpeed;
    public BlockManager(GameScreen gs) {
        this.gs = gs;
        blockList = new ArrayList<Food>();
        deadList = new ArrayList<Food>();
        towerList = new ArrayList<Food>();
        towerSpeed = 10;
        fluffy = gs.getFluffy();
        hud = gs.getHud();
        topBlock = new Food(new Vector2(300,70));
        towerList.add(topBlock);
    }

    public void drawBlocks(SpriteBatch batch) {
        if (!blockList.isEmpty()) {
            for (Food food : blockList) {
                food.drawFood(batch);
            }
        }
        for (Food food : towerList){
            food.drawFood(batch);
        }
        //topBlock.drawFood(batch);
    }

    public void allFalls() {
        ArrayList<Food> deadList = new ArrayList<Food>();
        if (!blockList.isEmpty()) {
            for (Food food : blockList) {
                food.fall();
                if (food.position.y <= 0) {
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
        Food food = new Food(new Vector2(rx,RDim.DEVICE_HEIGHT + topBlock.position.y));
        blockList.add(food);
    }

    public void overlaps() {
        Food added = null;
        for (Food food : blockList) {
            if (!deadList.contains(food)) {
                if (food.position.x >= topBlock.position.x
                        && food.position.y >= topBlock.position.y
                        && food.position.x <= topBlock.position.x + topBlock.getWidth()
                        && food.position.y <= topBlock.position.y + topBlock.getHeight()
                ) {
                   food.stop();
                   towerList.add(food);
                   topBlock = food;
                    added = food;
                    gs.getCamera().position.y = topBlock.position.y;

                    break;
                }
            }
        }
        blockList.remove(added);
    }



    public void moveLeft(){
        for (Food food : towerList){
            food.moveLeft(towerSpeed);
        }
        //topBlock.moveLeft();
    }

    public void moveRight(){
        for (Food food : towerList){
            food.moveRight(towerSpeed);
        }
       // topBlock.moveRight();
    }

}
