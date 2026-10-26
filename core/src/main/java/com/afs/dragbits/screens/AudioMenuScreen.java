package com.afs.dragbits.screens;

import com.afs.dragbits.Main;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AudioMenuScreen implements Screen {

    private final Game GAME;
    private final Main MAIN_GAME;

    private OrthographicCamera camara;
    private Viewport viewport;
    private SpriteBatch batch;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteOpciones;
    private GlyphLayout layoutTexto;

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    private static final float ESCALA_NORMAL = 2.0f;
    private static final float ESCALA_SELECCIONADA = 2.5f;

    private int indiceSeleccionado = 0;
    private static final int OPCION_VOL_MUSICA = 0;
    private static final int OPCION_VOL_GAMEPLAY = 1;
    private static final int OPCION_VOLVER = 2;
    private static final int TOTAL_OPCIONES = 3;

    private float[] escalasActuales = {ESCALA_NORMAL, ESCALA_NORMAL, ESCALA_NORMAL};

    public AudioMenuScreen(Game game, Main mainGame) {
        this.GAME = game;
        this.MAIN_GAME = mainGame;

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL, camara);
        batch = new SpriteBatch();
        layoutTexto = new GlyphLayout();

        fuenteTitulo = new BitmapFont();
        fuenteOpciones = new BitmapFont();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.UP) {
                    indiceSeleccionado--;
                    if (indiceSeleccionado < 0) indiceSeleccionado = TOTAL_OPCIONES - 1;
                    return true;
                }
                if (keycode == Input.Keys.DOWN) {
                    indiceSeleccionado++;
                    if (indiceSeleccionado >= TOTAL_OPCIONES) indiceSeleccionado = 0;
                    return true;
                }
                if (keycode == Input.Keys.LEFT) {
                    ajustarVolumen(-0.1f);
                    return true;
                }
                if (keycode == Input.Keys.RIGHT) {
                    ajustarVolumen(0.1f);
                    return true;
                }
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.ESCAPE) {
                    if (indiceSeleccionado == OPCION_VOLVER) {
                        GAME.setScreen(new MainMenuScreen(GAME, MAIN_GAME));
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 toque = new Vector2(screenX, screenY);
                viewport.unproject(toque);

                float posicionYInicial = ALTO_VIRTUAL * 0.45f;
                float espacioEntreOpciones = 80f;

                for (int i = 0; i < TOTAL_OPCIONES; i++) {
                    float opcionY = posicionYInicial - (i * espacioEntreOpciones);
                    float hitboxHeight = 60f;

                    if (toque.y >= opcionY - hitboxHeight && toque.y <= opcionY) {
                        if (i == OPCION_VOLVER) {
                            GAME.setScreen(new MainMenuScreen(GAME, MAIN_GAME));
                        } else {
                            indiceSeleccionado = i;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void ajustarVolumen(float delta) {
        switch (indiceSeleccionado) {
            case OPCION_VOL_MUSICA:
                float nuevoVolMusica = MathUtils.clamp(MAIN_GAME.getVolumenMusica() + delta, 0.0f, 1.0f);
                MAIN_GAME.setVolumenMusica(nuevoVolMusica);
                break;
            case OPCION_VOL_GAMEPLAY:
                float nuevoVolGameplay = MathUtils.clamp(MAIN_GAME.getVolumenGameplay() + delta, 0.0f, 1.0f);
                MAIN_GAME.setVolumenGameplay(nuevoVolGameplay);
                break;
        }
    }

    @Override
    public void render(float delta) {
        for (int i = 0; i < TOTAL_OPCIONES; i++) {
            float escalaObjetivo = (i == indiceSeleccionado) ? ESCALA_SELECCIONADA : ESCALA_NORMAL;
            escalasActuales[i] = MathUtils.lerp(escalasActuales[i], escalaObjetivo, delta * 12f);
        }

        viewport.apply();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camara.update();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        fuenteTitulo.getData().setScale(4.0f);
        layoutTexto.setText(fuenteTitulo, "AUDIO");
        float tituloX = (ANCHO_VIRTUAL - layoutTexto.width) / 2;
        float tituloY = ALTO_VIRTUAL * 0.75f;
        fuenteTitulo.draw(batch, "AUDIO", tituloX, tituloY);

        float posicionYInicial = ALTO_VIRTUAL * 0.45f;
        float espacioEntreOpciones = 80f;

        String[] etiquetas = {
            "MUSICA: " + String.format("%.0f%%", MAIN_GAME.getVolumenMusica() * 100),
            "GAMEPLAY: " + String.format("%.0f%%", MAIN_GAME.getVolumenGameplay() * 100),
            "VOLVER"
        };

        for (int i = 0; i < TOTAL_OPCIONES; i++) {
            fuenteOpciones.getData().setScale(escalasActuales[i]);
            layoutTexto.setText(fuenteOpciones, etiquetas[i]);

            float opcionX = (ANCHO_VIRTUAL - layoutTexto.width) / 2;
            float opcionY = posicionYInicial - (i * espacioEntreOpciones);

            fuenteOpciones.draw(batch, etiquetas[i], opcionX, opcionY);

            if (i == indiceSeleccionado) {
                float flechaX = opcionX - 60f;
                fuenteOpciones.draw(batch, ">", flechaX, opcionY);

                if (i < OPCION_VOLVER) {
                    float flechaIzqX = opcionX + layoutTexto.width + 20f;
                    float flechaDerX = opcionX + layoutTexto.width + 60f;
                    fuenteOpciones.draw(batch, "<", flechaIzqX, opcionY);
                    fuenteOpciones.draw(batch, ">", flechaDerX, opcionY);
                }
            }
        }

        fuenteOpciones.getData().setScale(1.5f);
        layoutTexto.setText(fuenteOpciones, "Usa ← → para ajustar volumen");
        float instruccionesX = (ANCHO_VIRTUAL - layoutTexto.width) / 2;
        float instruccionesY = ALTO_VIRTUAL * 0.15f;
        fuenteOpciones.draw(batch, "Usa ← → para ajustar volumen", instruccionesX, instruccionesY);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        if (fuenteTitulo != null) fuenteTitulo.dispose();
        if (fuenteOpciones != null) fuenteOpciones.dispose();
    }
}