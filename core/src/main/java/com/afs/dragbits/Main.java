package com.afs.dragbits;

import com.badlogic.gdx.Game;
import com.afs.dragbits.audio.GestorDeAudio;
import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.screens.MainMenuScreen;

public class Main extends Game implements ProveedorMusica {

    private GestorDeAudio gestorDeAudio;

    @Override
    public void create() {
        gestorDeAudio = new GestorDeAudio();
        gestorDeAudio.reproducirMusica();

        this.setScreen(new MainMenuScreen(this, this));
    }

    @Override
    public GestorDeAudio getGestorDeAudio() {
        return gestorDeAudio;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gestorDeAudio != null) {
            gestorDeAudio.dispose();
        }
    }
}
