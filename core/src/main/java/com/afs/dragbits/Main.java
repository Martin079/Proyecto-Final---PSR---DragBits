package com.afs.dragbits;

import com.badlogic.gdx.Game;
import com.afs.dragbits.screens.GameScreen;


public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
