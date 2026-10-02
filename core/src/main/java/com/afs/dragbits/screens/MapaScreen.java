package com.afs.dragbits.screens;

import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.ciudad.Burbuja;
import com.afs.dragbits.ciudad.Interfaz;
import com.afs.dragbits.funcionalidades.EntradaJugador;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.menurivales.TipoCarrera;
import com.afs.dragbits.menurivales.VentanaSeleccionRival;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class MapaScreen implements Screen {

    private final Game game;
    private final ProveedorMusica proveedorMusica;

    private SpriteBatch batch;
    private OrthographicCamera camara;
    private Viewport viewport;

    private Texture mapaTexture;
    private Texture burbujasSheet;

    private ArrayList<Burbuja> burbujas;

    private Jugador jugador;
    private final RepositorioJugador repositorioJugador;
    private Interfaz interfazCiudad;

    private VentanaSeleccionRival ventanaRival;
    private EntradaJugador entradaJugador;
    private InputMultiplexer multiplexer;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public MapaScreen(Game game, ProveedorMusica proveedorMusica) {
        this.game = game;
        this.proveedorMusica = proveedorMusica;
        this.repositorioJugador = new RepositorioJugador();
        this.jugador = repositorioJugador.cargarJugador();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        entradaJugador = new EntradaJugador();

        if (proveedorMusica != null && proveedorMusica.getGestorDeAudio() != null) {
            proveedorMusica.getGestorDeAudio().setModificadorPantalla(0.4f);
        }

        if (jugador != null) {
            repositorioJugador.cargarProgreso(jugador);
        }

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);

        interfazCiudad = new Interfaz(batch, jugador);

        ventanaRival = new VentanaSeleccionRival(game, proveedorMusica, viewport, () -> {
            Gdx.input.setInputProcessor(multiplexer);
        });

        // Multiplexer que combina los toques de Stage con EntradaJugador
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(interfazCiudad.getStage());
        multiplexer.addProcessor(entradaJugador);
        Gdx.input.setInputProcessor(multiplexer);

        mapaTexture = new Texture(Gdx.files.internal("sprites/Ciudad/Mapa.png"));
        mapaTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        burbujasSheet = new Texture(Gdx.files.internal("sprites/Ciudad/Burbuja mapa-sheet.png"));
        burbujasSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] tmp = TextureRegion.split(burbujasSheet, 31, 45);

        TextureRegion frameLegales   = tmp[0][0];
        TextureRegion frameIlegales  = tmp[0][1];
        TextureRegion frameMejoras   = tmp[0][2];
        TextureRegion frameAutos     = tmp[0][3];
        TextureRegion frameOnline    = tmp[0][4];

        burbujas = new ArrayList<>();

        float anchoBurbuja = 62f;
        float altoBurbuja = 90f;
        float offsetX = anchoBurbuja / 2f;
        float offsetY = altoBurbuja / 2f;

        burbujas.add(new Burbuja(100f - offsetX, 170f - offsetY, anchoBurbuja, altoBurbuja, frameLegales, () -> {
            abrirVentanaRival(TipoCarrera.LEGAL, 0);
        }));

        burbujas.add(new Burbuja(580f - offsetX, 590f - offsetY, anchoBurbuja, altoBurbuja, frameIlegales, () -> {
            abrirVentanaRival(TipoCarrera.ILEGAL, 0);
        }));

        burbujas.add(new Burbuja(1015f - offsetX, 390f - offsetY, anchoBurbuja, altoBurbuja, frameMejoras, () -> {}));
        burbujas.add(new Burbuja(350f - offsetX, 594f - offsetY, anchoBurbuja, altoBurbuja, frameAutos, () -> {}));
        burbujas.add(new Burbuja(1010f - offsetX, 180f - offsetY, anchoBurbuja, altoBurbuja, frameOnline, () -> {}));
    }

    private void abrirVentanaRival(TipoCarrera tipo, int maxDesbloqueado) {
        ventanaRival.mostrar(tipo, maxDesbloqueado);
        Gdx.input.setInputProcessor(ventanaRival.getStage());
    }

    @Override
    public void render(float delta) {
        if (!ventanaRival.isVisible() && Gdx.input.getInputProcessor() == ventanaRival.getStage()) {
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (!ventanaRival.isVisible()) {
            if (entradaJugador.consumoToque()) {
                Vector2 coords = entradaJugador.getCoordenadasToque(viewport);
                for (Burbuja b : burbujas) {
                    if (b.verificarClic(coords.x, coords.y)) {
                        break;
                    }
                }
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        batch.draw(mapaTexture, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        for (Burbuja b : burbujas) {
            b.dibujar(batch);
        }
        batch.end();

        interfazCiudad.render();
        ventanaRival.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (interfazCiudad != null) interfazCiudad.resize(width, height);
        if (ventanaRival != null) ventanaRival.resize(width, height);
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
        if (mapaTexture != null) mapaTexture.dispose();
        if (burbujasSheet != null) burbujasSheet.dispose();
        if (interfazCiudad != null) interfazCiudad.dispose();
        if (ventanaRival != null) ventanaRival.dispose();
    }
}
