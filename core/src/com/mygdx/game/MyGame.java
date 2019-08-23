package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.resources.Prefs;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MainMenuScreen;

public class MyGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public GameScreen gameScreen;
	public MainMenuScreen mainMenuScreen;
	public int deviceWidth;
	public int deviceHeight;
	public Prefs prefs;
	@Override
	public void create () {
		prefs = new Prefs();
		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		batch = new SpriteBatch();
		font = new BitmapFont();
		gameScreen = new GameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		this.setScreen(mainMenuScreen);
		//this.setScreen(gameScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		font.dispose();
		gameScreen.dispose();
		mainMenuScreen.dispose();
		//batch.dispose();
	}


}
