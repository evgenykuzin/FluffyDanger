package com.mygdx.game.controllers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MyGame;
import com.mygdx.game.models.store.Store;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.screens.StoreScreen;

public class StoreInputProcessor implements InputProcessor {
    private MyGame game;
    private boolean backArrowDown;
    private StoreScreen storeScreen;
    private Store store;
    Vector2 lastTouch;
    private boolean buyBtnDown;
    private boolean selectBtnDown;
    public StoreInputProcessor(MyGame game, StoreScreen storeScreen){
        this.game = game;
        backArrowDown = false;
        buyBtnDown = false;
        selectBtnDown = false;
        this.storeScreen = storeScreen;
        store = storeScreen.getStore();
        lastTouch = new Vector2();
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
        if (!store.getVitrines().get(0).isMooving()) {
            if (screenX < RDim.DEVICE_WIDTH / 2 - RDim.vitrine_width/2) store.scrollRight();
            else if (screenX > RDim.DEVICE_WIDTH / 2 + RDim.vitrine_width/2) store.scrollLeft();

        }
        if (store.getCurrentVitrine() != null) {
            if (StoreScreen.backArrow.getBoundingRectangle().contains(screenX, screenY)) {
                backArrowDown = true;
            } else if (store.getCurrentVitrine().buyButton.isClicked(screenX, screenY, store)) {
                buyBtnDown = true;
            } else if (store.getCurrentVitrine().selectButton.isClicked(screenX, screenY, store)) {
                selectBtnDown = true;
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (store.getCurrentVitrine() != null) {
            System.out.println(store.getCurrentVitrine().name);
        }
        if (backArrowDown){
            game.setScreen(new MainMenuScreen(game));
            backArrowDown = false;
        } else if (buyBtnDown && store.getCurrentVitrine() != null) {
            System.out.println("buy");
            store.buy();
            buyBtnDown = false;
            store.getCurrentVitrine().buyButton.setClickable(false);
        } else if (selectBtnDown && store.getCurrentVitrine() != null) {
            System.out.println("select");
            store.select();
            selectBtnDown = false;
            store.getCurrentVitrine().selectButton.setClickable(false);
        } else {
            lastTouch.set(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 newTouch = new Vector2(screenX, screenY);
        Vector2 delta = newTouch.cpy().sub(lastTouch);
        lastTouch = newTouch;

        boolean right = delta.x > 0;
        boolean left = delta.x < 0;

//        if (right && !store.getVitrines().get(0).isMooving()) {
//            store.scrollRight();
//        }
//        if (left && !store.getVitrines().get(0).isMooving()){
//            store.scrollLeft();
//        }
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
