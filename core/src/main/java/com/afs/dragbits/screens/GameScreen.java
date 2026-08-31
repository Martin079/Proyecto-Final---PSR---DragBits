package com.afs.dragbits.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.afs.dragbits.HUD.Basicos;
import com.afs.dragbits.HUD.Palanca;
import com.afs.dragbits.Main;
import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.camara.SeguimientoJugador;
import com.afs.dragbits.funcionalidades.Acelerador;
import com.afs.dragbits.funcionalidades.CajaDeCambios;
import com.afs.dragbits.funcionalidades.Semaforo;
import com.afs.dragbits.mapas.Picodromo;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Picodromo picodromo;
    private AutoJugador autoJugador;
    private SeguimientoJugador camaraJugador;

    // Viewport de interfaz fija
    private OrthographicCamera camaraUI;
    private Viewport viewportUI;
    private Vector3 mouseCoords;

    // Funcionalidades y HUD
    private Acelerador acelerador;
    private CajaDeCambios cajaDeCambios;
    private Semaforo semaforo;
    private Basicos hudBasicos;
    private Palanca hudPalanca;

    // Estado de carrera y UI Fin de Carrera
    private boolean carreraFinalizada;
    private Texture texturaCartel;
    private Texture texturaBoton;
    private BitmapFont fuenteTexto;
    private Rectangle boundsBoton;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        picodromo = new Picodromo();

        // Camara de interfaz fija
        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camaraUI);
        mouseCoords = new Vector3();

        // Spawn del auto a 3/4 de la imagen de largada
        autoJugador = new AutoJugador(picodromo.getPosicionSpawnX(), 130f);
        camaraJugador = new SeguimientoJugador(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        acelerador = new Acelerador();
        cajaDeCambios = new CajaDeCambios();
        semaforo = new Semaforo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), picodromo.getPosicionSpawnX());
        hudBasicos = new Basicos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudPalanca = new Palanca(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        carreraFinalizada = false;
        crearCartelUI();
    }

    private void crearCartelUI() {
        // Fondo del cartel
        Pixmap pixmapFondo = new Pixmap(420, 200, Pixmap.Format.RGBA8888);
        pixmapFondo.setColor(0, 0, 0, 0.85f);
        pixmapFondo.fill();
        texturaCartel = new Texture(pixmapFondo);
        pixmapFondo.dispose();

        // Textura del botón
        Pixmap pixmapBoton = new Pixmap(220, 50, Pixmap.Format.RGBA8888);
        pixmapBoton.setColor(Color.valueOf("27ae60")); // Verde
        pixmapBoton.fill();
        texturaBoton = new Texture(pixmapBoton);
        pixmapBoton.dispose();

        fuenteTexto = new BitmapFont();
        fuenteTexto.setColor(Color.WHITE);

        // Posicionar el botón en el centro de la pantalla virtual
        float btnX = (ANCHO_VIRTUAL - 220f) / 2f;
        float btnY = (ALTO_VIRTUAL - 200f) / 2f + 20f;
        boundsBoton = new Rectangle(btnX, btnY, 220f, 50f);
    }

    @Override
    public void render(float delta) {
        // Lógica de carrera
        if (!carreraFinalizada) {
            cajaDeCambios.actualizar(autoJugador, delta);
            acelerador.actualizar(autoJugador, delta);
            autoJugador.actualizar(delta);
            semaforo.actualizar(autoJugador, delta);

            // Cruce meta
            if (autoJugador.getFrenteX() >= picodromo.getPosicionLineaMeta()) {
                carreraFinalizada = true;
            }
        } else {
            // Manejo de clic para regresar al MapaScreen
            if (Gdx.input.justTouched()) {
                mouseCoords.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                viewportUI.unproject(mouseCoords);

                if (boundsBoton.contains(mouseCoords.x, mouseCoords.y)) {
                    game.setScreen(new MapaScreen(game));
                    return;
                }
            }
        }

        camaraJugador.actualizar(autoJugador);

        // Rendering
        ScreenUtils.clear(0, 0, 0, 1);

        // 1. Renderizar Mundo
        batch.begin();
        camaraJugador.aplicarACamara(batch);
        picodromo.dibujar(batch, Gdx.graphics.getHeight());
        autoJugador.dibujar(batch);
        batch.end();

        // 2. Renderizar HUD e Interfaz fija
        batch.begin();
        hudBasicos.dibujar(batch, autoJugador, Gdx.graphics.getWidth());
        hudPalanca.dibujar(batch, cajaDeCambios, Gdx.graphics.getWidth());
        semaforo.dibujar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // 3. Renderizar Cartel con Botón Clickeable
        if (carreraFinalizada) {
            viewportUI.apply();
            batch.setProjectionMatrix(camaraUI.combined);
            batch.begin();

            float cartelX = (ANCHO_VIRTUAL - 420f) / 2f;
            float cartelY = (ALTO_VIRTUAL - 200f) / 2f;

            // Dibujar panel de fondo
            batch.draw(texturaCartel, cartelX, cartelY);

            // Texto de título
            fuenteTexto.getData().setScale(2f);
            fuenteTexto.draw(batch, "¡CARRERA FINALIZADA!", cartelX + 35f, cartelY + 160f);

            // Dibujar Botón Volver al Mapa
            batch.draw(texturaBoton, boundsBoton.x, boundsBoton.y, boundsBoton.width, boundsBoton.height);

            // Texto del Botón
            fuenteTexto.getData().setScale(1.2f);
            fuenteTexto.draw(batch, "VOLVER AL MAPA", boundsBoton.x + 35f, boundsBoton.y + 32f);

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
        if (hudPalanca != null) hudPalanca.dispose();
        if (semaforo != null) semaforo.dispose();
        if (texturaCartel != null) texturaCartel.dispose();
        if (texturaBoton != null) texturaBoton.dispose();
        if (fuenteTexto != null) fuenteTexto.dispose();
    }
}
