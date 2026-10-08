package com.afs.dragbits.screens;

import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.autos.AutoRival;
import com.afs.dragbits.camara.SeguimientoJugador;
import com.afs.dragbits.funcionalidades.Acelerador;
import com.afs.dragbits.funcionalidades.CajaDeCambios;
import com.afs.dragbits.funcionalidades.ControladorCarrera;
import com.afs.dragbits.funcionalidades.EntradaJugador;
import com.afs.dragbits.hud.Basicos;
import com.afs.dragbits.hud.CartelPausa;
import com.afs.dragbits.hud.CartelResultado;
import com.afs.dragbits.hud.EstadoJuego;
import com.afs.dragbits.hud.Palanca;
import com.afs.dragbits.hud.Semaforo;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.mapas.Picodromo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    private final Game GAME;
    private ProveedorMusica proveedorMusica;

    private SpriteBatch batch;
    private Picodromo picodromo;
    private AutoJugador autoJugador;
    private AutoRival autoRival;
    private SeguimientoJugador camaraJugador;

    private ControladorCarrera controladorCarrera;
    private Jugador datosJugador;

    private Viewport viewportMundo;
    private Viewport viewportHUD;

    private Acelerador acelerador;
    private CajaDeCambios cajaDeCambios;
    private Semaforo semaforo;
    private Basicos hudBasicos;
    private Palanca hudPalanca;
    private CartelResultado cartelResultado;
    private CartelPausa cartelPausa;
    private EstadoJuego estadoJuego;

    private static final float ANCHO_VIRTUAL = 1920f;
    private static final float ALTO_VIRTUAL = 1080f;
    private static final float Y_JUGADOR = 199f;
    private static final float Y_RIVAL = 352f;

    private EntradaJugador entradaJugador;

    public GameScreen(Game game, ProveedorMusica proveedorMusica) {
        this.GAME = game;
        this.proveedorMusica = proveedorMusica;
    }

    @Override
    public void show() {
        entradaJugador = new EntradaJugador();
        Gdx.input.setInputProcessor(entradaJugador);

        batch = new SpriteBatch();

        if (proveedorMusica != null && proveedorMusica.getGestorDeAudio() != null) {
            proveedorMusica.getGestorDeAudio().setModificadorPantalla(0.2f);
        }

        picodromo = new Picodromo();
        RepositorioJugador repositorioJugador = new RepositorioJugador();
        datosJugador = repositorioJugador.cargarJugador();

        camaraJugador = new SeguimientoJugador(ANCHO_VIRTUAL, ALTO_VIRTUAL);
        viewportMundo = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camaraJugador.getCamara());
        viewportHUD = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL);

        autoJugador = new AutoJugador(picodromo.getPosicionSpawnX(), Y_JUGADOR);
        autoRival = new AutoRival(
            picodromo.getPosicionSpawnX(),
            Y_RIVAL,
            155f,
            45f,
            70f,
            800,
            "sprites/Autos/Legales/renault 12-sheet.png"
        );

        controladorCarrera = new ControladorCarrera(picodromo, autoJugador, autoRival, datosJugador);

        acelerador = new Acelerador();
        cajaDeCambios = new CajaDeCambios();
        semaforo = new Semaforo(picodromo.getPosicionSpawnX());
        hudBasicos = new Basicos(ANCHO_VIRTUAL, ALTO_VIRTUAL);
        hudPalanca = new Palanca();
        cartelResultado = new CartelResultado(ANCHO_VIRTUAL, ALTO_VIRTUAL);
        cartelPausa = new CartelPausa(ANCHO_VIRTUAL, ALTO_VIRTUAL);
        estadoJuego = EstadoJuego.EN_CURSO;
    }

    @Override
    public void render(float delta) {
        if (entradaJugador.consumirCancelar()) {
            if (estadoJuego == EstadoJuego.EN_CURSO) {
                estadoJuego = EstadoJuego.PAUSADO;
            } else if (estadoJuego == EstadoJuego.PAUSADO) {
                estadoJuego = EstadoJuego.EN_CURSO;
            }
        }

        if (estadoJuego == EstadoJuego.EN_CURSO && entradaJugador.consumoToque()) {
            Vector2 toque = entradaJugador.getCoordenadasToque(viewportHUD);
            if (hudBasicos.fueBotonPausaTocado(toque.x, toque.y)) {
                estadoJuego = EstadoJuego.PAUSADO;
            } else {
                entradaJugador.descartarToque();
            }
        }

        if (estadoJuego == EstadoJuego.PAUSADO) {
            if (entradaJugador.consumirCancelar()) {
                estadoJuego = EstadoJuego.EN_CURSO;
            }

            if (entradaJugador.consumoToque()) {
                Vector2 toque = entradaJugador.getCoordenadasToque(viewportHUD);

                if (cartelPausa.fueBotonReanudarTocado(toque.x, toque.y)) {
                    estadoJuego = EstadoJuego.EN_CURSO;
                } else if (cartelPausa.fueBotonAbandonarTocado(toque.x, toque.y)) {
                    Gdx.app.postRunnable(() -> GAME.setScreen(new MapaScreen(GAME, proveedorMusica)));
                    return;
                }
            }
        }

        if (!controladorCarrera.isCarreraFinalizada() && estadoJuego == EstadoJuego.EN_CURSO) {
            cajaDeCambios.actualizar(autoJugador, entradaJugador);
            acelerador.actualizar(autoJugador, entradaJugador, delta);
            autoJugador.actualizar(delta);

            autoRival.actualizarIA(delta, semaforo.getEstadoActual());
            semaforo.actualizar(autoJugador, delta);

            controladorCarrera.actualizar();
            if (controladorCarrera.isCarreraFinalizada()) {
                estadoJuego = EstadoJuego.FINALIZADO;
            }
            entradaJugador.descartarToque();
            entradaJugador.consumirConfirmar();
        } else if (estadoJuego == EstadoJuego.FINALIZADO) {
            if (entradaJugador.consumoToque()) {
                Vector2 toque = entradaJugador.getCoordenadasToque(viewportHUD);
                if (cartelResultado.fueBotonTocado(toque.x, toque.y)) {
                    Gdx.app.postRunnable(() -> GAME.setScreen(new MapaScreen(GAME, proveedorMusica)));
                    return;
                } else {
                    entradaJugador.descartarToque();
                }
            }
        }

        camaraJugador.actualizar(autoJugador);

        ScreenUtils.clear(0, 0, 0, 1);

        viewportMundo.apply();
        camaraJugador.aplicarACamara(batch);
        batch.begin();
        picodromo.dibujar(batch, ALTO_VIRTUAL);
        autoJugador.dibujar(batch);
        autoRival.dibujar(batch);
        batch.end();

        viewportHUD.apply();
        batch.setProjectionMatrix(viewportHUD.getCamera().combined);
        batch.begin();
        hudBasicos.dibujar(batch, autoJugador, ANCHO_VIRTUAL, estadoJuego != EstadoJuego.FINALIZADO);
        hudPalanca.dibujar(batch, cajaDeCambios, ANCHO_VIRTUAL);
        semaforo.dibujar(batch, ANCHO_VIRTUAL, ALTO_VIRTUAL);
        if (controladorCarrera.isCarreraFinalizada()) {
            cartelResultado.dibujar(batch, controladorCarrera.isJugadorGano(),
                autoRival.getRecompensa(), datosJugador.getDinero(), ANCHO_VIRTUAL, ALTO_VIRTUAL);
        }
        batch.end();

        if (estadoJuego == EstadoJuego.PAUSADO) {
            batch.begin();
            cartelPausa.dibujar(batch, ANCHO_VIRTUAL, ALTO_VIRTUAL);
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewportMundo.update(width, height);
        viewportHUD.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (picodromo != null) picodromo.dispose();
        if (autoJugador != null) autoJugador.dispose();
        if (autoRival != null) autoRival.dispose();
        if (hudBasicos != null) hudBasicos.dispose();
        if (hudPalanca != null) hudPalanca.dispose();
        if (semaforo != null) semaforo.dispose();
        if (cartelResultado != null) cartelResultado.dispose();
        if (cartelPausa != null) cartelPausa.dispose();
    }
}
