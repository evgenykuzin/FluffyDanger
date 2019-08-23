package com.mygdx.game.models.hud;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.resources.Prefs;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;
import com.mygdx.game.screens.GameScreen;

public class Hud {
    private int money;
    private Jeludok jeludok;
    private boolean pauseViewShown;
    private Sprite pauseButton;
    private Sprite quitButton;
    private int health;
    private Prefs prefs;
    public Hud(GameScreen gs){
        prefs = gs.game.prefs;
        money = prefs.getPrefMoney();
        jeludok = new Jeludok(gs);
        health = 5;
        pauseViewShown = false;
        pauseButton = new Sprite(RTextures.hud_pause_btn);
        pauseButton.setBounds(10, 10, RDim.mm_start_width, RDim.mm_start_height);
        quitButton = new Sprite(RTextures.hud_quit);
        quitButton.setBounds(220, 200, 200, 100);
    }

    public int getMoney(){
        return money;
    }

    public void addMoney(int money){
        if (this.money + money >= 0) {
            this.money += money;
            prefs.setMoney(this.money);
        }

    }

    public Jeludok getJeludok(){
        return jeludok;
    }

    public void setPauseViewShown(boolean t) {
        pauseViewShown = t;
    }

    public boolean isPauseViewShown(){
        return pauseViewShown;
    }

    public Sprite getPauseButton(){
        return pauseButton;
    }

    public Sprite getQuitButton(){
        return quitButton;
    }

    public void missHealth(int h){
        health -= h;
    }

    public int getHealth(){
        return health;
    }

}
