package com.afs.dragbits.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.afs.dragbits.Main;
import com.afs.dragbits.ciudad.Burbuja;
import com.afs.dragbits.ciudad.Interfaz;
import com.afs.dragbits.menurivales.VentanaSeleccionRival;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;

import java.util.ArrayList;
import java.util.List;

public class MapaScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private Viewport viewport;

    private Texture mapaTexture;
    private Texture burbujasSheet;

    private List<Burbuja> burbujas;
    private Vector3 mouseCoordsVirtuales;

    // Jugador, Repositorio y HUD
    private Jugador jugador;
    private final RepositorioJugador repositorioJugador;
    private Interfaz interfazCiudad;

    // ventana Seleccionar Rival
    private VentanaSeleccionRival ventanaRival;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public MapaScreen(Main game) {
        this.game = game;
        this.repositorioJugador = new RepositorioJugador();

        // Cargar el progreso del Jugador mediante el repositorio
        this.jugador = repositorioJugador.cargarJugador();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // volumen en el mapa
        if (game.getMusicaFondo() != null) {
            game.getMusicaFondo().setVolume(0.4f);
            if (!game.getMusicaFondo().isPlaying()) {
                game.getMusicaFondo().play();
            }
        }

        // recargar progreso por si cambio al volver de otra pantalla
        if (jugador != null) {
            repositorioJugador.cargarProgreso(jugador);
        }

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);

        mouseCoordsVirtuales = new Vector3();

        // carga del HUD de la Ciudad
        interfazCiudad = new Interfaz(batch, jugador);

        // instancia de la ventana de Selección de Rival
        ventanaRival = new VentanaSeleccionRival(game, viewport, () -> ventanaRival.ocultar());

        // Texturas
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

        // Carreras Legales
        burbujas.add(new Burbuja(100f - offsetX, 170f - offsetY, anchoBurbuja, altoBurbuja, frameLegales, () -> {
            abrirVentanaRival(VentanaSeleccionRival.TipoCarrera.LEGAL, 0);
        }));

        // Carreras Ilegales
        burbujas.add(new Burbuja(580f - offsetX, 590f - offsetY, anchoBurbuja, altoBurbuja, frameIlegales, () -> {
            abrirVentanaRival(VentanaSeleccionRival.TipoCarrera.ILEGAL, 0);
        }));

        // Tienda de Mejoras
        burbujas.add(new Burbuja(1015f - offsetX, 390f - offsetY, anchoBurbuja, altoBurbuja, frameMejoras, () -> {
            System.out.println("Entrando a Tienda de Mejoras...");
        }));

        // Tienda de Autos
        burbujas.add(new Burbuja(350f - offsetX, 594f - offsetY, anchoBurbuja, altoBurbuja, frameAutos, () -> {
            System.out.println("Entrando a Tienda de Autos...");
        }));

        // Modo Online
        burbujas.add(new Burbuja(1010f - offsetX, 180f - offsetY, anchoBurbuja, altoBurbuja, frameOnline, () -> {
            System.out.println("Entrando a Modo Online...");
        }));
    }

    private void abrirVentanaRival(VentanaSeleccionRival.TipoCarrera tipo, int maxDesbloqueado) {
        ventanaRival.mostrar(tipo, maxDesbloqueado);
        Gdx.input.setInputProcessor(ventanaRival.getStage());
    }

    @Override
    public void render(float delta) {
        if (!ventanaRival.isVisible() && Gdx.input.getInputProcessor() == ventanaRival.getStage()) {
            Gdx.input.setInputProcessor(interfazCiudad.getStage());
        }

        if (!ventanaRival.isVisible()) {
            mouseCoordsVirtuales.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(mouseCoordsVirtuales);

            if (Gdx.input.justTouched()) {
                for (Burbuja b : burbujas) {
                    if (b.verificarClic(mouseCoordsVirtuales.x, mouseCoordsVirtuales.y)) {
                        break;
                    }
                }
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        batch.setProjectionMatrix(camara.combined);

        // DIBUJADO DE LA CIUDAD Y OBJETOS
        batch.begin();
        batch.draw(mapaTexture, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        for (Burbuja b : burbujas) {
            b.dibujar(batch);
        }
        batch.end();

        // DIBUJADO DEL HUD
        interfazCiudad.render();

        // DIBUJADO DE LA VENTANA
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
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (mapaTexture != null) mapaTexture.dispose();
        if (burbujasSheet != null) burbujasSheet.dispose();
        if (interfazCiudad != null) interfazCiudad.dispose();
        if (ventanaRival != null) ventanaRival.dispose();
    }
}
