package com.mygdx.game.controllers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.MyGame;
import com.mygdx.game.renderers.MainMenuRenderer;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MainMenuScreen;
import com.mygdx.game.screens.SettingsScreen;
import com.mygdx.game.screens.StoreScreen;

import java.util.HashMap;
import java.util.Map;

public class MainMenuInputProcessor implements InputProcessor {
    private MyGame game;
    private Texture bgTexture;
    private int width, height;
    public OrthographicCamera cam;
    private MainMenuScreen mainMenuScreen;
    public float ppuX;    // pixels per unit on the X axis
    public float ppuY;    // pixels per unit on the Y axis
    float CAMERA_WIDTH = RDim.DEVICE_WIDTH;
    float CAMERA_HEIGHT = RDim.DEVICE_HEIGHT;
    public Map<String, Texture> textures;
    boolean startDown;
    boolean storeDown;
    boolean settingsDown;
    private MainMenuRenderer mainMenuRenderer;
    public MainMenuInputProcessor(MainMenuScreen mainMenuScreen, MainMenuRenderer mainMenuRenderer) {
        this.game = mainMenuScreen.game;
        textures = new HashMap<String, Texture>();
        width = (int) CAMERA_WIDTH;
        height = (int) CAMERA_HEIGHT;
        ppuX = (float) width / CAMERA_WIDTH;
        ppuY = (float) height / CAMERA_HEIGHT;
        startDown = false;
        storeDown = false;
        settingsDown = false;
        this.mainMenuScreen = mainMenuScreen;
        this.mainMenuRenderer = mainMenuRenderer;
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
        if (mainMenuScreen.startButton.getBoundingRectangle().contains(screenX, screenY)){
            startDown = true;
        } else if (mainMenuScreen.storeButton.getBoundingRectangle().contains(screenX, screenY)){
            storeDown = true;
        } else if (mainMenuScreen.settingsButton.getBoundingRectangle().contains(screenX, screenY))
            settingsDown = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (startDown) {
            mainMenuScreen.dispose();
            game.setScreen(new GameScreen(game));
            startDown = false;
        } else if (storeDown){
            mainMenuScreen.dispose();
            game.setScreen(new StoreScreen(game));
            storeDown = false;
        } else if (settingsDown){
            mainMenuScreen.dispose();
            game.setScreen(new SettingsScreen());
            settingsDown = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
