package com.afs.dragbits.ciudad;

import com.afs.dragbits.jugador.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class interfaz implements Disposable {

    private Stage stage;
    private Jugador jugador;

    // Texturas y Frames
    private Texture fondoGrisTexture;
    private Texture billeteTexture;
    private Texture nivelSheetTexture;
    private TextureRegion[] framesNivel;

    // Elementos UI
    private Image imgIconoNivel;
    private Label labelDinero;
    private Label labelNivel;
    private BitmapFont font;

    // Variables para control de Animación de Level Up
    private int nivelAnterior;
    private boolean animandoSubidaNivel = false;
    private float tiempoAnimacion = 0f;

    // Tiempos de animación
    private static final float DURACION_CICLO_XP = 0.3f; // 0.3s para recorrer frames 0 a 7
    private static final float DURACION_PAUSA_FRAME9 = 0.5f; // Pausa mostrando el frame 8 (icono 9)
    private static final float DURACION_TOTAL_ANIM = DURACION_CICLO_XP + DURACION_PAUSA_FRAME9;

    public interfaz(SpriteBatch batch, Jugador jugador) {
        this.jugador = jugador;
        this.nivelAnterior = jugador.getNivel();
        this.stage = new Stage(new ScreenViewport(), batch);

        cargarRecursos();
        crearInterfaz();
    }

    private void cargarRecursos() {
        // 1. Fondo gris oscuro semitransparente
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.15f, 0.15f, 0.15f, 0.75f));
        pixmap.fill();
        fondoGrisTexture = new Texture(pixmap);
        pixmap.dispose();

        // 2. Cargar Texturas
        billeteTexture = new Texture(Gdx.files.internal("sprites/Ciudad/Billete.png"));
        nivelSheetTexture = new Texture(Gdx.files.internal("sprites/Ciudad/Nivel-sheet.png"));

        // Extraer los 9 frames de 41x41 px (asumiendo que están en fila horizontal)
        TextureRegion[][] tmp = TextureRegion.split(nivelSheetTexture, 41, 41);
        framesNivel = new TextureRegion[9];
        for (int i = 0; i < 9; i++) {
            framesNivel[i] = tmp[0][i];
        }

        // Font por defecto
        font = new BitmapFont();
        font.getData().setScale(1.2f);
    }

    private void crearInterfaz() {
        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.top();

        Table barraHud = new Table();
        barraHud.setBackground(new Image(fondoGrisTexture).getDrawable());

        // --- SECCIÓN DINERO ---
        Image imgBillete = new Image(billeteTexture);

        Label.LabelStyle estiloTexto = new Label.LabelStyle(font, Color.WHITE);
        labelDinero = new Label("$" + jugador.getDinero(), estiloTexto);
        labelDinero.setAlignment(Align.left);

        // --- SECCIÓN NIVEL ---
        imgIconoNivel = new Image(framesNivel[0]);
        labelNivel = new Label(String.valueOf(jugador.getNivel()), estiloTexto);
        labelNivel.setAlignment(Align.center);

        Table contenedorNivel = new Table();
        contenedorNivel.add(imgIconoNivel).size(41, 41);

        Table overlayTextoNivel = new Table();
        overlayTextoNivel.add(labelNivel).center();

        Table contenido = new Table();
        contenido.add(imgBillete).size(30, 30).padRight(8);
        contenido.add(labelDinero).width(110).padRight(40);
        contenido.stack(contenedorNivel, overlayTextoNivel).size(41, 41);

        barraHud.add(contenido).expandX().center().padLeft(80);
        tablaPrincipal.add(barraHud).growX().height(50);

        stage.addActor(tablaPrincipal);
    }


    public void actualizar(float delta) {
        // Actualizar dinero siempre
        labelDinero.setText("$" + jugador.getDinero());

        // Detección de subida de nivel
        if (jugador.getNivel() > nivelAnterior && !animandoSubidaNivel) {
            animandoSubidaNivel = true;
            tiempoAnimacion = 0f;
        }

        if (animandoSubidaNivel) {
            tiempoAnimacion += delta;

            if (tiempoAnimacion < DURACION_CICLO_XP) {
                // Paso 1: Reproducir frames 0 al 7 rápido en 0.3 segundos
                float progreso = tiempoAnimacion / DURACION_CICLO_XP;
                int frameIndex = (int) (progreso * 8); // Mapea a valores de 0 a 7
                if (frameIndex > 7) frameIndex = 7;

                imgIconoNivel.setDrawable(new TextureRegionDrawable(framesNivel[frameIndex]));
            }
            else if (tiempoAnimacion < DURACION_TOTAL_ANIM) {
                // Paso 2: Mostrar frame 8 (indicador de subida de nivel) con una pausa
                imgIconoNivel.setDrawable(new TextureRegionDrawable(framesNivel[8]));
            }
            else {
                // Paso 3: Finalizar animación y actualizar nivel visualmente
                animandoSubidaNivel = false;
                nivelAnterior = jugador.getNivel();
                labelNivel.setText(String.valueOf(jugador.getNivel()));
            }
        } else {
            // Estado Normal: Calcular frame (0 a 7) basado en el porcentaje de XP actual
            labelNivel.setText(String.valueOf(jugador.getNivel()));

            float porcentaje = (float) jugador.getExperienciaActual() / jugador.getExperienciaSiguienteNivel();
            int frameIndex = (int) (porcentaje * 8);

            // Evitar desbordamiento de índice
            if (frameIndex < 0) frameIndex = 0;
            if (frameIndex > 7) frameIndex = 7;

            imgIconoNivel.setDrawable(new TextureRegionDrawable(framesNivel[frameIndex]));
        }
    }

    public void render() {
        actualizar(Gdx.graphics.getDeltaTime());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
        fondoGrisTexture.dispose();
        billeteTexture.dispose();
        nivelSheetTexture.dispose();
        font.dispose();
    }
}
