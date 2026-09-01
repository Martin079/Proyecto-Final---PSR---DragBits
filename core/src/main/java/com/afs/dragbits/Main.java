package com.afs.dragbits;


import com.badlogic.gdx.Game;
import com.afs.dragbits.screens.MainMenuScreen;

public class Main extends Game {

    @Override
    public void create() {

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
