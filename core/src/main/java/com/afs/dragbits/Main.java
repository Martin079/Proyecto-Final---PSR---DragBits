package com.afs.dragbits;

import com.afs.dragbits.screens.MapaScreen;
import com.badlogic.gdx.Game;
import com.afs.dragbits.screens.GameScreen;


public class Main extends Game {

    @Override
    public void create() {
        setScreen(new MapaScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
