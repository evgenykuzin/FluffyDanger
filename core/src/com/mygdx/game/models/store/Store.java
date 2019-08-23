package com.mygdx.game.models.store;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.resources.Prefs;
import com.mygdx.game.resources.RDim;
import com.mygdx.game.resources.RTextures;

import java.util.ArrayList;

public class Store {

    private ArrayList<Vitrine> vitrines;
    public static final Vector2 currentVitrinePosition =
            new Vector2(RDim.DEVICE_WIDTH / 4, RDim.DEVICE_HEIGHT / 6);
    public Store() {
        float space = RDim.DEVICE_WIDTH / 2 + RDim.DEVICE_WIDTH / 2 / 4;
        vitrines = new ArrayList<Vitrine>();
        vitrines.add(new Vitrine(0,"Green Fluffy", RTextures.fluffyOne,
                new Vector2().set(currentVitrinePosition)));
        vitrines.add(new Vitrine(1,"Pink Fluffyed", RTextures.fluffyTwo,
                new Vector2(space + RDim.DEVICE_WIDTH / 4, RDim.DEVICE_HEIGHT / 6)));
        vitrines.add(new Vitrine(2,"Orange Eater", RTextures.fluffyThree,
                new Vector2(2 * space + RDim.DEVICE_WIDTH / 4, RDim.DEVICE_HEIGHT / 6)));
        vitrines.add(new Vitrine(3,"Blue Dabudy", RTextures.fluffyFour,
                new Vector2(3 * space + RDim.DEVICE_WIDTH / 4, RDim.DEVICE_HEIGHT / 6)));
    }

    public Vector2 getCurrentVitrinePosition() {
        return currentVitrinePosition;
    }

    public void scrollLeft() {
        for (Vitrine vitrine : vitrines) {
            if (vitrines.get(vitrines.size() - 1).getPosition().x != currentVitrinePosition.x) {
                    vitrine.setLeftMove(true);

            } else {
                vitrine.setLeftMove(false);
                vitrine.setRightMove(false);
            }
        }
    }

    public void scrollRight() {
        for (Vitrine vitrine : vitrines) {
            if (vitrines.get(0).getPosition().x != currentVitrinePosition.x) {
                    vitrine.setRightMove(true);

            } else {
                vitrine.setRightMove(false);
                vitrine.setLeftMove(false);
            }
        }
    }

    public Vitrine getCurrentVitrine() {
        Vitrine currentVitrine = null;
        for (Vitrine vitrine : vitrines) {
            if (vitrine.getPosition().x == currentVitrinePosition.x) {
                currentVitrine = vitrine;
            }
        }
        return currentVitrine;
    }

    public ArrayList<Vitrine> getVitrines() {
        return vitrines;
    }

    public void drawVitrines(SpriteBatch batch) {
        for (Vitrine vitrine : vitrines) {
            vitrine.setMovable();
            vitrine.drawVitrine(batch);
        }
    }

    public void buy(){
        getCurrentVitrine().buy();
    }

    public void select(){
        for (Vitrine vitrine : vitrines){
            vitrine.unselect();
        }
        getCurrentVitrine().select();
        Prefs prefs = new Prefs();
        prefs.setFluffy(getCurrentVitrine().getFluffyInteger());
    }

}
