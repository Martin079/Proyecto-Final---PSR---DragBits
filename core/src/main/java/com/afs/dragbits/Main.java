package com.afs.dragbits;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.afs.dragbits.screens.MainMenuScreen;

public class Main extends Game {

    private Music musicaFondo;

    @Override
    public void create() {
        // musica de fondo global
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/Musica/musica 1.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(1.0f);
        musicaFondo.play();

        this.setScreen(new MainMenuScreen(this));
    }

    public Music getMusicaFondo() {
        return musicaFondo;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
    }
}
