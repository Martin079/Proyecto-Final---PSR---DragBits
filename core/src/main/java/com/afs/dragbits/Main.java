package com.afs.dragbits;

import com.badlogic.gdx.Game;
import com.afs.dragbits.audio.GestorDeAudio;
import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.screens.MainMenuScreen;

public class Main extends Game implements ProveedorMusica {

    private GestorDeAudio gestorDeAudio;
    private float volumenMusica = 1.0f;
    private float volumenGameplay = 1.0f;

    @Override
    public void create() {
        gestorDeAudio = new GestorDeAudio();
        gestorDeAudio.setVolumenMusicaGlobal(volumenMusica);
        gestorDeAudio.reproducirMusica();

        this.setScreen(new MainMenuScreen(this, this));
    }

    @Override
    public GestorDeAudio getGestorDeAudio() {
        return gestorDeAudio;
    }

    public float getVolumenMusica() {
        return volumenMusica;
    }

    public void setVolumenMusica(float volumenMusica) {
        this.volumenMusica = Math.max(0.0f, Math.min(1.0f, volumenMusica));
        if (gestorDeAudio != null) {
            gestorDeAudio.setVolumenMusicaGlobal(this.volumenMusica);
        }
    }

    public float getVolumenGameplay() {
        return volumenGameplay;
    }

    public void setVolumenGameplay(float volumenGameplay) {
        this.volumenGameplay = Math.max(0.0f, Math.min(1.0f, volumenGameplay));
        if (gestorDeAudio != null) {
            gestorDeAudio.setVolumenGameplay(this.volumenGameplay);
        }
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
