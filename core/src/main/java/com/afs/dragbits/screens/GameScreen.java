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
import com.afs.dragbits.hud.CartelResultado;
import com.afs.dragbits.hud.Palanca;
import com.afs.dragbits.hud.Semaforo;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.mapas.Picodromo;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

    private OrthographicCamera camaraUI;
    private Viewport viewportUI;

    private Acelerador acelerador;
    private CajaDeCambios cajaDeCambios;
    private Semaforo semaforo;
    private Basicos hudBasicos;
    private Palanca hudPalanca;
    private CartelResultado cartelResultado;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    private EntradaJugador entradaJugador;

    public GameScreen(Game GAME, ProveedorMusica proveedorMusica) {
        this.GAME = GAME;
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

        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camaraUI);

        autoJugador = new AutoJugador(picodromo.getPosicionSpawnX(), 130f);
        autoRival = new AutoRival(
            picodromo.getPosicionSpawnX(),
            230f,
            155f,
            45f,
            70f,
            800,
            "sprites/Autos/renault 12-sheet.png"
        );

        controladorCarrera = new ControladorCarrera(picodromo, autoJugador, autoRival, datosJugador);
        camaraJugador = new SeguimientoJugador(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        acelerador = new Acelerador();
        cajaDeCambios = new CajaDeCambios();
        semaforo = new Semaforo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), picodromo.getPosicionSpawnX());
        hudBasicos = new Basicos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudPalanca = new Palanca(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cartelResultado = new CartelResultado(ANCHO_VIRTUAL, ALTO_VIRTUAL);
    }

    @Override
    public void render(float delta) {
        if (!controladorCarrera.isCarreraFinalizada()) {
            cajaDeCambios.actualizar(autoJugador, entradaJugador);
            acelerador.actualizar(autoJugador, entradaJugador, delta);
            autoJugador.actualizar(delta);

            autoRival.actualizarIA(delta, semaforo.getEstadoActual());
            semaforo.actualizar(autoJugador, delta);

            controladorCarrera.actualizar();
        } else {
            if (entradaJugador.consumoToque()) {
                Vector2 posTouch = entradaJugador.getCoordenadasToque(viewportUI);
                Vector3 mouseCoords = new Vector3(posTouch.x, posTouch.y, 0);

                if (cartelResultado.fueBotonTocado(mouseCoords)) {
                    GAME.setScreen(new MapaScreen(GAME, proveedorMusica));
                    return;
                }
            }
        }

        camaraJugador.actualizar(autoJugador);

        ScreenUtils.clear(0, 0, 0, 1);

        // Renderizar Mundo
        batch.begin();
        camaraJugador.aplicarACamara(batch);
        picodromo.dibujar(batch, Gdx.graphics.getHeight());
        autoJugador.dibujar(batch);
        autoRival.dibujar(batch);
        batch.end();

        // Renderizar HUD
        batch.begin();
        hudBasicos.dibujar(batch, autoJugador, Gdx.graphics.getWidth());
        hudPalanca.dibujar(batch, cajaDeCambios, Gdx.graphics.getWidth());
        semaforo.dibujar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Renderizar Cartel de Resultado
        if (controladorCarrera.isCarreraFinalizada()) {
            batch.begin();
            cartelResultado.dibujar(
                batch,
                controladorCarrera.isJugadorGano(),
                autoRival.getRecompensa(),
                datosJugador.getDinero(),
                ANCHO_VIRTUAL,
                ALTO_VIRTUAL
            );
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewportUI.update(width, height, true);
        if (camaraJugador != null) camaraJugador.resize(width, height);
        if (hudBasicos != null) hudBasicos.resize(width, height);
        if (hudPalanca != null) hudPalanca.resize(width, height);
        if (semaforo != null) semaforo.resize(width, height);
        if (cartelResultado != null) cartelResultado.resize(ANCHO_VIRTUAL, ALTO_VIRTUAL);
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
    }
}
