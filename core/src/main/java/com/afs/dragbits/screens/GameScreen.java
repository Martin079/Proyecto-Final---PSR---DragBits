package com.afs.dragbits.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.afs.dragbits.HUD.Basicos;
import com.afs.dragbits.Main;
import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.camara.SeguimientoJugador;
import com.afs.dragbits.funcionalidades.Acelerador;
import com.afs.dragbits.funcionalidades.CajaDeCambios;
import com.afs.dragbits.mapas.Picodromo;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Picodromo picodromo;
    private AutoJugador autoJugador;
    private SeguimientoJugador camaraJugador;

    // Funcionalidades y HUD
    private Acelerador acelerador;
    private CajaDeCambios cajaDeCambios;
    private Basicos hudBasicos;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        picodromo = new Picodromo();
        autoJugador = new AutoJugador(50f, 100f);
        camaraJugador = new SeguimientoJugador(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializar transmisión y HUD
        acelerador = new Acelerador();
        cajaDeCambios = new CajaDeCambios();
        hudBasicos = new Basicos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        // 1. ENTRADA Y LÓGICA
        cajaDeCambios.actualizar(autoJugador, delta);
        acelerador.actualizar(autoJugador, delta);
        autoJugador.actualizar(delta);
        camaraJugador.actualizar(autoJugador);

        // 2. RENDERING
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();

        // A) Dibujar el juego (Mundo con cámara móvil)
        camaraJugador.aplicarACamara(batch);
        picodromo.dibujar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        autoJugador.dibujar(batch);

        // B) Dibujar la Interfaz (HUD estático abajo a la derecha)
        hudBasicos.dibujar(batch, autoJugador, Gdx.graphics.getWidth());

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (camaraJugador != null) camaraJugador.resize(width, height);
        if (hudBasicos != null) hudBasicos.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (picodromo != null) picodromo.dispose();
        if (autoJugador != null) autoJugador.dispose();
        if (hudBasicos != null) hudBasicos.dispose();
    }
}
