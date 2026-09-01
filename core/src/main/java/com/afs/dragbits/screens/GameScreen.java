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
import com.afs.dragbits.Jugador.Jugador;
import com.afs.dragbits.Main;
import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.autos.AutoRival;
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
    private AutoRival autoRival; // Bot
    private SeguimientoJugador camaraJugador;

    // Datos del Jugador
    private Jugador datosJugador;

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
    private boolean jugadorGano;
    private boolean recompensaOtorgada;

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

        // Cargar progreso del Jugador
        datosJugador = new Jugador();
        datosJugador.cargarProgreso();

        // Camara de interfaz fija
        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camaraUI);
        mouseCoords = new Vector3();

        // Instanciar auto del jugador
        autoJugador = new AutoJugador(picodromo.getPosicionSpawnX(), 130f);

        // Instanciar Bot Rival (V-Max 155, Acel 45, Tracción 70, $800 recompensa, carril superior)
        autoRival = new AutoRival(
            picodromo.getPosicionSpawnX(),
            230f,
            155f,
            45f,
            70f,
            800,
            "Sprites/Autos/auto 1-sheet.png"
        );

        camaraJugador = new SeguimientoJugador(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        acelerador = new Acelerador();
        cajaDeCambios = new CajaDeCambios();
        semaforo = new Semaforo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), picodromo.getPosicionSpawnX());
        hudBasicos = new Basicos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudPalanca = new Palanca(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        carreraFinalizada = false;
        jugadorGano = false;
        recompensaOtorgada = false;

        crearCartelUI();
    }

    private void crearCartelUI() {
        // Fondo del cartel mas amplio para alojar textos de recompensa
        Pixmap pixmapFondo = new Pixmap(460, 240, Pixmap.Format.RGBA8888);
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
        float btnY = (ALTO_VIRTUAL - 240f) / 2f + 20f;
        boundsBoton = new Rectangle(btnX, btnY, 220f, 50f);
    }

    @Override
    public void render(float delta) {
        // Lógica de carrera
        if (!carreraFinalizada) {
            cajaDeCambios.actualizar(autoJugador, delta);
            acelerador.actualizar(autoJugador, delta);
            autoJugador.actualizar(delta);

            // Actualizar lógica del Bot y semáforo
            autoRival.actualizarIA(delta, semaforo.getEstadoActual());
            semaforo.actualizar(autoJugador, delta);

            // Detección de fin de carrera
            float metaX = picodromo.getPosicionLineaMeta();
            boolean jugadorCruzo = autoJugador.getFrenteX() >= metaX;
            boolean botCruzo = autoRival.getFrenteX() >= metaX;

            if (jugadorCruzo || botCruzo) {
                carreraFinalizada = true;

                // Determinamos el ganador comparando la posición de la trompa
                jugadorGano = autoJugador.getFrenteX() >= autoRival.getFrenteX();

                // Otorgar y guardar dinero una sola vez
                if (jugadorGano && !recompensaOtorgada) {
                    datosJugador.sumarDinero(autoRival.getRecompensa());
                    datosJugador.guardarProgreso();
                    recompensaOtorgada = true;
                }
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
        autoRival.dibujar(batch); // Dibujar bot
        batch.end();

        // 2. Renderizar HUD e Interfaz fija
        batch.begin();
        hudBasicos.dibujar(batch, autoJugador, Gdx.graphics.getWidth());
        hudPalanca.dibujar(batch, cajaDeCambios, Gdx.graphics.getWidth());
        semaforo.dibujar(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // 3. Renderizar Cartel de Resultado y Recompensa
        if (carreraFinalizada) {
            viewportUI.apply();
            batch.setProjectionMatrix(camaraUI.combined);
            batch.begin();

            float cartelX = (ANCHO_VIRTUAL - 460f) / 2f;
            float cartelY = (ALTO_VIRTUAL - 240f) / 2f;

            // Panel de fondo
            batch.draw(texturaCartel, cartelX, cartelY);

            // Mensajes según la victoria o derrota
            fuenteTexto.getData().setScale(2f);
            if (jugadorGano) {
                fuenteTexto.setColor(Color.GOLD);
                fuenteTexto.draw(batch, "¡VICTORIA!", cartelX + 140f, cartelY + 200f);

                fuenteTexto.getData().setScale(1.2f);
                fuenteTexto.setColor(Color.GREEN);
                fuenteTexto.draw(batch, "Recompensa: +$" + autoRival.getRecompensa(), cartelX + 130f, cartelY + 140f);
            } else {
                fuenteTexto.setColor(Color.RED);
                fuenteTexto.draw(batch, "DERROTA", cartelX + 160f, cartelY + 200f);

                fuenteTexto.getData().setScale(1.2f);
                fuenteTexto.setColor(Color.WHITE);
                fuenteTexto.draw(batch, "Recompensa: +$0", cartelX + 165f, cartelY + 140f);
            }

            // Mostrar el total del dinero acumulado (desde la clase Jugador)
            fuenteTexto.setColor(Color.WHITE);
            fuenteTexto.draw(batch, "Total: $" + datosJugador.getDinero(), cartelX + 175f, cartelY + 100f);

            // Botón Volver al Mapa
            batch.draw(texturaBoton, boundsBoton.x, boundsBoton.y, boundsBoton.width, boundsBoton.height);
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
        if (autoRival != null) autoRival.dispose();
        if (hudBasicos != null) hudBasicos.dispose();
        if (hudPalanca != null) hudPalanca.dispose();
        if (semaforo != null) semaforo.dispose();
        if (texturaCartel != null) texturaCartel.dispose();
        if (texturaBoton != null) texturaBoton.dispose();
        if (fuenteTexto != null) fuenteTexto.dispose();
    }
}
