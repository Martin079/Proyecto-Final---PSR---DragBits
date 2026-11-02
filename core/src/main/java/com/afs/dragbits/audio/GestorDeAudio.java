package com.afs.dragbits.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class GestorDeAudio implements Disposable {

    private Music musicaFondo;

    private float volumenMaestro = 1.0f;
    private float modificadorPantalla = 1.0f;
    private float volumenGameplay = 1.0f;
    private boolean muteado = false;
    private float volumenPrevioMute = 1.0f;

    public GestorDeAudio() {
        cargarMusicaPrincipal();
    }

    private void cargarMusicaPrincipal() {
        if (Gdx.files.internal("audio/Musica/musica 1.mp3").exists()) {
            musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/Musica/musica 1.mp3"));
            musicaFondo.setLooping(true);
        }
    }


    public void reproducirMusica() {
        if (musicaFondo != null) {
            actualizarVolumenMusica();
            if (!musicaFondo.isPlaying()) {
                musicaFondo.play();
            }
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }


    public void setModificadorPantalla(float modificador) {
        this.modificadorPantalla = Math.max(0.0f, Math.min(1.0f, modificador));
        actualizarVolumenMusica();
    }

    public void setVolumenMaestro(float volumen) {
        this.volumenMaestro = Math.max(0.0f, Math.min(1.0f, volumen));
        if (muteado && volumen > 0.0f) {
            muteado = false;
        }
        actualizarVolumenMusica();
    }

    public void alternarMute() {
        if (muteado) {
            muteado = false;
            volumenMaestro = volumenPrevioMute;
        } else {
            volumenPrevioMute = volumenMaestro;
            volumenMaestro = 0.0f;
            muteado = true;
        }
        actualizarVolumenMusica();
    }

    private void actualizarVolumenMusica() {
        if (musicaFondo != null) {
            musicaFondo.setVolume(getVolumenEfectivo());
        }
    }

    public void setVolumenMusicaGlobal(float volumen) {
        this.volumenMaestro = Math.max(0.0f, Math.min(1.0f, volumen));
        actualizarVolumenMusica();
    }

    public float getVolumenMusicaGlobal() {
        return volumenMaestro;
    }

    public float getVolumenEfectivo() {
        if (muteado) return 0.0f;
        return volumenMaestro * modificadorPantalla * volumenGameplay;
    }

    public float getVolumenMaestro() { return volumenMaestro; }
    public boolean isMuteado() { return muteado; }

    public void setVolumenGameplay(float volumen) {
        this.volumenGameplay = Math.max(0.0f, Math.min(1.0f, volumen));
    }

    public float getVolumenGameplay() {
        return volumenGameplay;
    }

    @Override
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
    }
}
