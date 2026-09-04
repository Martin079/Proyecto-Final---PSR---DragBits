package com.afs.dragbits.screens;

import com.afs.dragbits.Main;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends InputAdapter implements Screen {


    private final Main juego;


    private OrthographicCamera camara;
    private Viewport viewport;
    private SpriteBatch batch;


    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteOpciones;
    private GlyphLayout layoutTexto;


    private final String textoTitulo = "DRAG BITS";
    private final String[] opcionesMenu = {"START GAME", "CONTROLES", "EXIT"};
    private int indiceSeleccionado = 0;


    private final float ESCALA_NORMAL = 2.0f;
    private final float ESCALA_SELECCIONADA = 2.5f;
    private float[] escalasActuales = {ESCALA_NORMAL, ESCALA_NORMAL, ESCALA_NORMAL};


    private float[] posicionesYOpciones = new float[3];
    private final float ALTURA_HITBOX_OPCION = 60f;


    private Texture texturaControles;
    private boolean estaMostrandoControles = false;

    public MainMenuScreen(Main juego) {
        this.juego = juego;


        camara = new OrthographicCamera();
        viewport = new FitViewport(1280f, 720f, camara);
        batch = new SpriteBatch();
        layoutTexto = new GlyphLayout();


        fuenteTitulo = new BitmapFont();
        fuenteOpciones = new BitmapFont();


        texturaControles = new Texture(Gdx.files.internal("Sprites/MenuPrincipal/Controles.png"));
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(this);


        if (juego.getMusicaFondo() != null) {
            juego.getMusicaFondo().setVolume(1.0f);
            if (!juego.getMusicaFondo().isPlaying()) {
                juego.getMusicaFondo().play();
            }
        }
    }

    @Override
    public void render(float delta) {

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

        for (int i = 0; i < opcionesMenu.length; i++) {
            float escalaObjetivo = (i == indiceSeleccionado) ? ESCALA_SELECCIONADA : ESCALA_NORMAL;
            escalasActuales[i] = MathUtils.lerp(escalasActuales[i], escalaObjetivo, delta * 12f);
        }


        fuenteTitulo.getData().setScale(4.0f);
        layoutTexto.setText(fuenteTitulo, textoTitulo);
        float tituloX = (viewport.getWorldWidth() - layoutTexto.width) / 2;
        float tituloY = viewport.getWorldHeight() * 0.75f;
        fuenteTitulo.draw(batch, textoTitulo, tituloX, tituloY);


        float posicionYInicial = viewport.getWorldHeight() * 0.45f;
        float espacioEntreOpciones = 80f;

        for (int i = 0; i < opcionesMenu.length; i++) {
            fuenteOpciones.getData().setScale(escalasActuales[i]);
            layoutTexto.setText(fuenteOpciones, opcionesMenu[i]);

            float opcionX = (viewport.getWorldWidth() - layoutTexto.width) / 2;
            float opcionY = posicionYInicial - (i * espacioEntreOpciones);


            posicionesYOpciones[i] = opcionY;

            fuenteOpciones.draw(batch, opcionesMenu[i], opcionX, opcionY);


            if (i == indiceSeleccionado) {
                float flechaX = opcionX - 60f;
                fuenteOpciones.draw(batch, ">", flechaX, opcionY);
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (estaMostrandoControles) {

            if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.ENTER) {
                estaMostrandoControles = false;
            }
            return true;
        }


        if (keycode == Input.Keys.UP) {
            indiceSeleccionado--;
            if (indiceSeleccionado < 0) {
                indiceSeleccionado = opcionesMenu.length - 1;
            }
        } else if (keycode == Input.Keys.DOWN) {
            indiceSeleccionado++;
            if (indiceSeleccionado >= opcionesMenu.length) {
                indiceSeleccionado = 0;
            }
        } else if (keycode == Input.Keys.ENTER) {
            ejecutarOpcionSeleccionada();
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!estaMostrandoControles) {

            Vector3 coordenadasMundo = viewport.unproject(new Vector3(screenX, screenY, 0));

            for (int i = 0; i < opcionesMenu.length; i++) {
                float topeY = posicionesYOpciones[i];
                float baseY = topeY - ALTURA_HITBOX_OPCION;

                if (coordenadasMundo.y <= topeY && coordenadasMundo.y >= baseY) {
                    indiceSeleccionado = i;
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            if (estaMostrandoControles) {

                Vector3 coordenadasMundo = viewport.unproject(new Vector3(screenX, screenY, 0));

                float areaBotonX = viewport.getWorldWidth() - 200f;
                float areaBotonY = viewport.getWorldHeight() - 100f;

                if (coordenadasMundo.x > areaBotonX && coordenadasMundo.y > areaBotonY) {
                    estaMostrandoControles = false;
                }
            } else {
                ejecutarOpcionSeleccionada();
            }
        }
        return true;
    }

    private void ejecutarOpcionSeleccionada() {

        switch (indiceSeleccionado) {
            case 0:

                juego.setScreen(new MapaScreen(juego));
                this.dispose();
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
    }

    @Override
    public void dispose() {

        batch.dispose();
        fuenteTitulo.dispose();
        fuenteOpciones.dispose();
        texturaControles.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
}

