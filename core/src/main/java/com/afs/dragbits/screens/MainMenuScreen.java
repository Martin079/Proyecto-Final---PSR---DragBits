package com.afs.dragbits.screens;

import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.funcionalidades.EntradaJugador;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {

    private final Game GAME;
    private final ProveedorMusica PROVEEDOR_MUSICA;

    private OrthographicCamera camara;
    private Viewport viewport;
    private SpriteBatch batch;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteOpciones;
    private GlyphLayout layoutTexto;

    private static final String TEXTO_TITULO = "DRAG BITS";
    private static final String[] OPCIONES_MENU = {"START GAME", "CONTROLES", "EXIT"};
    private int indiceSeleccionado = 0;

    private static final float ESCALA_NORMAL = 2.0f;
    private static final float ESCALA_SELECCIONADA = 2.5f;
    private float[] escalasActuales = {ESCALA_NORMAL, ESCALA_NORMAL, ESCALA_NORMAL};

    private float[] posicionesYOpciones = new float[3];
    private float[] posicionesXOpciones = new float[3];
    private float[] anchosOpciones = new float[3];

    private static final float ALTURA_HITBOX_OPCION = 60f;

    private Texture texturaControles;
    private boolean estaMostrandoControles = false;

    private EntradaJugador entradaJugador;

    public MainMenuScreen(Game game, ProveedorMusica proveedorMusica) {
        this.GAME = game;
        this.PROVEEDOR_MUSICA = proveedorMusica;

        camara = new OrthographicCamera();
        viewport = new FitViewport(1280f, 720f, camara);
        batch = new SpriteBatch();
        layoutTexto = new GlyphLayout();

        fuenteTitulo = new BitmapFont();
        fuenteOpciones = new BitmapFont();

        texturaControles = new Texture(Gdx.files.internal("sprites/MenuPrincipal/Controles.png"));
    }

    @Override
    public void show() {
        entradaJugador = new EntradaJugador();
        Gdx.input.setInputProcessor(entradaJugador);

        if (PROVEEDOR_MUSICA != null && PROVEEDOR_MUSICA.getGestorDeAudio() != null) {
            PROVEEDOR_MUSICA.getGestorDeAudio().setModificadorPantalla(1.0f);
            PROVEEDOR_MUSICA.getGestorDeAudio().reproducirMusica();
        }
    }

    private void actualizarSeleccionPorMouse(float x, float y) {
        for (int i = 0; i < OPCIONES_MENU.length; i++) {
            float topeY = posicionesYOpciones[i];
            float baseY = topeY - ALTURA_HITBOX_OPCION;

            float minX = posicionesXOpciones[i];
            float maxX = minX + anchosOpciones[i];

            if (y <= topeY && y >= baseY && x >= minX && x <= maxX) {
                indiceSeleccionado = i;
            }
        }
    }

    @Override
    public void render(float delta) {
        procesarEntrada();

        if (GAME.getScreen() != this) return;

        viewport.apply();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        if (estaMostrandoControles) {
            dibujarPantallaControles();
        } else {
            dibujarMenuPrincipal(delta);
        }

        batch.end();
    }

    private void dibujarPantallaControles() {
        float anchoTextura = texturaControles.getWidth();
        float altoTextura = texturaControles.getHeight();

        float centroX = (viewport.getWorldWidth() - anchoTextura) / 2f;
        float centroY = (viewport.getWorldHeight() - altoTextura) / 2f;

        batch.draw(texturaControles, centroX, centroY, anchoTextura, altoTextura);

        fuenteOpciones.getData().setScale(ESCALA_NORMAL);
        layoutTexto.setText(fuenteOpciones, "MENU");

        float margenDerecho = 30f;
        float margenSuperior = 30f;
        float botonMenuX = viewport.getWorldWidth() - layoutTexto.width - margenDerecho;
        float botonMenuY = viewport.getWorldHeight() - margenSuperior;

        fuenteOpciones.draw(batch, "MENU", botonMenuX, botonMenuY);
    }

    private void dibujarMenuPrincipal(float delta) {
        for (int i = 0; i < OPCIONES_MENU.length; i++) {
            float escalaObjetivo = (i == indiceSeleccionado) ? ESCALA_SELECCIONADA : ESCALA_NORMAL;
            escalasActuales[i] = MathUtils.lerp(escalasActuales[i], escalaObjetivo, delta * 12f);
        }

        fuenteTitulo.getData().setScale(4.0f);
        layoutTexto.setText(fuenteTitulo, TEXTO_TITULO);
        float tituloX = (viewport.getWorldWidth() - layoutTexto.width) / 2;
        float tituloY = viewport.getWorldHeight() * 0.75f;
        fuenteTitulo.draw(batch, TEXTO_TITULO, tituloX, tituloY);

        float posicionYInicial = viewport.getWorldHeight() * 0.45f;
        float espacioEntreOpciones = 80f;

        for (int i = 0; i < OPCIONES_MENU.length; i++) {
            fuenteOpciones.getData().setScale(escalasActuales[i]);
            layoutTexto.setText(fuenteOpciones, OPCIONES_MENU[i]);

            float opcionX = (viewport.getWorldWidth() - layoutTexto.width) / 2;
            float opcionY = posicionYInicial - (i * espacioEntreOpciones);

            // Guardar datos de limites
            posicionesYOpciones[i] = opcionY;
            posicionesXOpciones[i] = opcionX;
            anchosOpciones[i] = layoutTexto.width;

            fuenteOpciones.draw(batch, OPCIONES_MENU[i], opcionX, opcionY);

            if (i == indiceSeleccionado) {
                float flechaX = opcionX - 60f;
                fuenteOpciones.draw(batch, ">", flechaX, opcionY);
            }
        }
    }

    private void procesarEntrada() {
        if (estaMostrandoControles) {
            if (entradaJugador.consumirCancelar() || entradaJugador.consumirConfirmar()) {
                estaMostrandoControles = false;
            }
            if (entradaJugador.consumoToque()) {
                Vector2 pos = entradaJugador.getCoordenadasToque(viewport);
                float areaBotonX = viewport.getWorldWidth() - 200f;
                float areaBotonY = viewport.getWorldHeight() - 100f;
                if (pos.x > areaBotonX && pos.y > areaBotonY) {
                    estaMostrandoControles = false;
                }
            }
        } else {
            if (entradaJugador.consumirArriba()) {
                indiceSeleccionado--;
                if (indiceSeleccionado < 0) indiceSeleccionado = OPCIONES_MENU.length - 1;
            }
            if (entradaJugador.consumirAbajo()) {
                indiceSeleccionado++;
                if (indiceSeleccionado >= OPCIONES_MENU.length) indiceSeleccionado = 0;
            }
            if (entradaJugador.consumirConfirmar()) {
                ejecutarOpcionSeleccionada();
                if (GAME.getScreen() != this) return;
            }
            if (entradaJugador.consumoMovimientoMouse()) {
                Vector2 pos = entradaJugador.getCoordenadasMouse(viewport);
                actualizarSeleccionPorMouse(pos.x, pos.y);
            }
            if (entradaJugador.consumoToque()) {
                Vector2 pos = entradaJugador.getCoordenadasToque(viewport);
                for (int i = 0; i < OPCIONES_MENU.length; i++) {
                    float topeY = posicionesYOpciones[i];
                    float baseY = topeY - ALTURA_HITBOX_OPCION;

                    float minX = posicionesXOpciones[i];
                    float maxX = minX + anchosOpciones[i];

                    if (pos.y <= topeY && pos.y >= baseY && pos.x >= minX && pos.x <= maxX) {
                        indiceSeleccionado = i;
                        ejecutarOpcionSeleccionada();
                        break;
                    }
                }
                if (GAME.getScreen() != this) return;
            }
        }
    }

    private void ejecutarOpcionSeleccionada() {
        switch (indiceSeleccionado) {
            case 0:
                GAME.setScreen(new MapaScreen(GAME, PROVEEDOR_MUSICA));
                break;
            case 1:
                estaMostrandoControles = true;
                break;
            case 2:
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (fuenteTitulo != null) fuenteTitulo.dispose();
        if (fuenteOpciones != null) fuenteOpciones.dispose();
        if (texturaControles != null) texturaControles.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
}
