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
import com.afs.dragbits.Ciudad.Burbuja;
import com.afs.dragbits.Ciudad.HUD;
import com.afs.dragbits.Jugador.Jugador;

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

    // Integración de Jugador y HUD
    private Jugador jugador;
    private HUD hudCiudad;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public MapaScreen(Main game) {
        this.game = game;
        // Instancia temporal del jugador si no viene de 'game'
        this.jugador = new Jugador();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);

        mouseCoordsVirtuales = new Vector3();

        // Carga del HUD de la Ciudad
        hudCiudad = new HUD(batch, jugador);

        // Carga de Texturas
        mapaTexture = new Texture(Gdx.files.internal("Sprites/Ciudad/Mapa.png"));
        mapaTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        burbujasSheet = new Texture(Gdx.files.internal("Sprites/Ciudad/Burbuja mapa-sheet.png"));
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

        // 1. Carreras Legales -> Cambia a la pantalla de la carrera (GameScreen)
        burbujas.add(new Burbuja(100f - offsetX, 170f - offsetY, anchoBurbuja, altoBurbuja, frameLegales, () -> {
            game.setScreen(new GameScreen(game));
        }));

        // 2. Carreras Ilegales
        burbujas.add(new Burbuja(580f - offsetX, 590f - offsetY, anchoBurbuja, altoBurbuja, frameIlegales, () -> {
            System.out.println("Entrando a Carreras Ilegales...");
        }));

        // 3. Tienda de Mejoras
        burbujas.add(new Burbuja(1015f - offsetX, 390f - offsetY, anchoBurbuja, altoBurbuja, frameMejoras, () -> {
            System.out.println("Entrando a Tienda de Mejoras...");
        }));

        // 4. Tienda de Autos
        burbujas.add(new Burbuja(350f - offsetX, 594f - offsetY, anchoBurbuja, altoBurbuja, frameAutos, () -> {
            System.out.println("Entrando a Tienda de Autos...");
        }));

        // 5. Modo Online
        burbujas.add(new Burbuja(1010f - offsetX, 180f - offsetY, anchoBurbuja, altoBurbuja, frameOnline, () -> {
            System.out.println("Entrando a Modo Online...");
        }));
    }

    @Override
    public void render(float delta) {
        // Actualizar coordenadas traducidas del ratón para detectar clics
        mouseCoordsVirtuales.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouseCoordsVirtuales);

        if (Gdx.input.justTouched()) {
            for (Burbuja b : burbujas) {
                if (b.verificarClic(mouseCoordsVirtuales.x, mouseCoordsVirtuales.y)) {
                    break;
                }
            }
        }

        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        batch.setProjectionMatrix(camara.combined);

        // --- 1. DIBUJADO DE LA CIUDAD Y OBJETOS ---
        batch.begin();

        // Dibujar Mapa de Fondo
        batch.draw(mapaTexture, 0, 0, ANCHO_VIRTUAL, ALTO_VIRTUAL);

        // Dibujar Burbujas
        for (Burbuja b : burbujas) {
            b.dibujar(batch);
        }

        batch.end();

        // --- 2. DIBUJADO DEL HUD (SUPERPUESTO) ---
        hudCiudad.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (hudCiudad != null) {
            hudCiudad.resize(width, height);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (mapaTexture != null) mapaTexture.dispose();
        if (burbujasSheet != null) burbujasSheet.dispose();
        if (hudCiudad != null) hudCiudad.dispose();
    }
}
