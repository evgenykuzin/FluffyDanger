package com.mygdx.game.controllers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGame;
import com.mygdx.game.models.player.Fluffy;
import com.mygdx.game.models.hud.Hud;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MainMenuScreen;

public class PlayerInputProcessor implements InputProcessor {
    Vector2 lastTouch;
    Fluffy fluffy;
    boolean pauseTouched;
    boolean pauseViewShown;
    boolean quitTouched;
    MyGame game;
    Hud hud;
    public PlayerInputProcessor(MyGame game, GameScreen gs, Fluffy fluffy){
        this.game = game;
        hud = gs.getHud();
        lastTouch = new Vector2();
        this.fluffy = fluffy;
        pauseTouched = false;
        pauseViewShown = false;
        quitTouched = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("X: " + screenX + " : : " + "Y: " + screenY);
        //fluffy.move(screenX);
        if (hud.getPauseButton().getBoundingRectangle().contains(screenX, screenY)){
            pauseTouched = true;
        } else if (hud.getQuitButton().getBoundingRectangle().contains(screenX, screenY)){
            quitTouched = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pauseTouched){
            if (!pauseViewShown) {
                //game.setScreen(new MainMenuScreen(game));
                pauseTouched = false;
                pauseViewShown = true;
                hud.setPauseViewShown(true);
            } else {

                pauseTouched = false;
                pauseViewShown = false;
                hud.setPauseViewShown(false);
            }
        } else if (quitTouched) {
            quitTouched = false;
            game.setScreen(new MainMenuScreen(game));
        } else {
            lastTouch.set(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 newTouch = new Vector2(screenX, screenY);
        Vector2 delta = newTouch.cpy().sub(lastTouch);
        lastTouch = newTouch;

        boolean right = delta.x > 0;
        boolean left = delta.x < 0;

        if (right) {
            fluffy.moveRight();
        } else if (left){
            fluffy.moveLeft();
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
