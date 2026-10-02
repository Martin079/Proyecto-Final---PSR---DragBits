package com.afs.dragbits.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class GestorDeAudio implements Disposable {

    private Music musicaFondo;
    private final ObjectMap<String, Sound> sonidos;

    private float volumenMaestro = 1.0f;       // Configurable por el jugador (0.0 a 1.0)
    private float modificadorPantalla = 1.0f;  // Ajuste contextual (0.2 carrera, 0.4 mapa, 1.0 menú)
    private boolean muteado = false;
    private float volumenPrevioMute = 1.0f;

    public GestorDeAudio() {
        sonidos = new ObjectMap<>();
        cargarMusicaPrincipal();
        cargarEfectos();
    }

    private void cargarMusicaPrincipal() {
        if (Gdx.files.internal("audio/Musica/musica 1.mp3").exists()) {
            musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/Musica/musica 1.mp3"));
            musicaFondo.setLooping(true);
        }
    }

    private void cargarEfectos() {
        // Carga defensiva: comprueba la existencia de archivos antes de instanciarlos
        cargarSonidoSiExiste("cambio_marcha", "audio/Sonidos/cambio_marcha.wav");
        cargarSonidoSiExiste("motor", "audio/Sonidos/motor.wav");
        cargarSonidoSiExiste("patinaje", "audio/Sonidos/patinaje.wav");
        cargarSonidoSiExiste("victoria", "audio/Sonidos/victoria.wav");
        cargarSonidoSiExiste("derrota", "audio/Sonidos/derrota.wav");
    }

    private void cargarSonidoSiExiste(String clave, String ruta) {
        if (Gdx.files.internal(ruta).exists()) {
            sonidos.put(clave, Gdx.audio.newSound(Gdx.files.internal(ruta)));
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

    public void reproducirEfecto(String clave) {
        if (muteado) return;
        Sound sonido = sonidos.get(clave);
        if (sonido != null) {
            sonido.play(getVolumenEfectivo());
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

    public float getVolumenEfectivo() {
        if (muteado) return 0.0f;
        return volumenMaestro * modificadorPantalla;
    }

    public float getVolumenMaestro() { return volumenMaestro; }
    public boolean isMuteado() { return muteado; }

    @Override
    public void dispose() {
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
        for (Sound sonido : sonidos.values()) {
            sonido.dispose();
        }
        sonidos.clear();
    }
}
