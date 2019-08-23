package com.mygdx.game.models.store;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.models.Button;
import com.mygdx.game.resources.Prefs;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RFonts;
import com.mygdx.game.resources.RTextures;

public class Vitrine {
    private Texture FluffyTexture;
    private Vector2 position;
    private float width;
    private float height;
    private float speed;
    private boolean rightMove;
    private boolean leftMove;
    private float xLeft;
    private float xRight;
    private BitmapFont bitmapFont;
    public String name;
    public Button buyButton;
    public Button selectButton;
    private boolean isBought;
    private boolean isSelected;
    private int fluffyInteger;
    public Vitrine(int fluffyInteger, String name, Texture fluffyTexture, Vector2 position){
        this.fluffyInteger = fluffyInteger;
        this.name = name;
        this.FluffyTexture = fluffyTexture;
        this.position = position;
        width = RDim.DEVICE_WIDTH/2;
        height = RDim.DEVICE_HEIGHT/2+RDim.DEVICE_HEIGHT/6;
        this.speed = 10f;
        rightMove = false;
        leftMove = false;
        xLeft = 0;
        xRight = 0;
        bitmapFont = new BitmapFont(RFonts.norm_px20);
        bitmapFont.setColor(Color.CORAL);
        buyButton = new Button(RTextures.buy_btn,
                new Vector2(Store.currentVitrinePosition.x + width/3 + width/20,
                        Store.currentVitrinePosition.y + height/10),
                width/4, height/4);
        selectButton = new Button(RTextures.select_btn,
                new Vector2(Store.currentVitrinePosition.x + width/3 + width/20,
                        Store.currentVitrinePosition.y + height/10),
                width/4, height/4);
        Prefs prefs = new Prefs();
        isSelected = prefs.getSelected(fluffyInteger);
        isBought = false;
    }

    public void drawVitrine(SpriteBatch batch){
        batch.draw(RTextures.store_vitrine, position.x, position.y,
                width, height);
        batch.draw(FluffyTexture, position.x+width/3, position.y+height/3,
                width/3, height/3);
        //bitmapFont.draw(batch, name, , );
        if (isSelected){
            batch.draw(RTextures.selected, position.x + width/3 + width/20,
                    position.y+height/10, width/4, height/4);
        } else if (isBought){
            selectButton.drawButton(batch, position.x + width/3 + width/20,
                    position.y + height/10);

        } else {
            buyButton.drawButton(batch, position.x + width/3 + width/20,
                    position.y + height/10);
        }
    }

    public int getFluffyInteger(){
        return fluffyInteger;
    }

    public void setSize(float width, float height){
        this.width = width;
        this.height = height;
    }

    public boolean isMooving(){
        return leftMove || rightMove;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition(){
        return position;
    }

    public void setRightMove(boolean t){
        if ((leftMove) && t) leftMove = false;
        rightMove = t;
    }

    public void setLeftMove(boolean t){
        if ((rightMove) && t) rightMove = false;
        leftMove = t;
    }

    public void moveLeft(){
        if (leftMove) {
            if (position.x > xLeft) {
                position.x -= speed;
            } else {
                setLeftMove(false);
            }
        } else if (!rightMove){
            xLeft = position.x - width - width/4;
        }
    }



    public void moveRight(){
        if (rightMove) {
            if (position.x < xRight) {
                position.x += speed;
            } else {
                setRightMove(false);
            }

        } else if (!leftMove){
            xRight = position.x + width + width/4;
        }
    }

    public void setMovable(){
        moveLeft();
        moveRight();
    }

    public void buy(){
        isBought = true;
    }

    public void select(){
        isSelected = true;
    }

    public void unselect(){
        isSelected = false;
        selectButton.setClickable(true);
    }
}
