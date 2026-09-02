package com.afs.dragbits.ciudad;

import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.afs.dragbits.util.TexturaSolidaFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class Interfaz implements Disposable {

    private Stage stage;
    private Jugador jugador;

    // texturas y frames
    private Texture fondoGrisTexture;
    private Texture billeteTexture;
    private Texture nivelSheetTexture;
    private TextureRegion[] framesNivel;

    //UI
    private Image imgIconoNivel;
    private Label labelDinero;
    private Label labelNivel;
    private BitmapFont font;

    // animacion de Level Up
    private int nivelAnterior;
    private boolean animandoSubidaNivel = false;
    private float tiempoAnimacion = 0f;

    // tiempos de animación
    private static final float DURACION_CICLO_XP = 0.3f; // 0.3s para recorrer frames 0 a 7
    private static final float DURACION_PAUSA_FRAME9 = 0.5f; // pausa mostrando el frame 8
    private static final float DURACION_TOTAL_ANIM = DURACION_CICLO_XP + DURACION_PAUSA_FRAME9;

    public Interfaz(SpriteBatch batch, Jugador jugador) {
        this.jugador = jugador;
        this.nivelAnterior = jugador.getNivel();
        this.stage = new Stage(new ScreenViewport(), batch);

        cargarRecursos();
        crearInterfaz();
    }

    private void cargarRecursos() {
        // fondo gris oscuro semitransparente usando la fabrica
        fondoGrisTexture = TexturaSolidaFactory.crearTextura(new Color(0.15f, 0.15f, 0.15f, 0.75f));

        // Cargar Texturas usando SpriteSheetLoader
        billeteTexture = SpriteSheetLoader.cargarTextura("sprites/Ciudad/Billete.png");

        nivelSheetTexture = SpriteSheetLoader.cargarTextura("sprites/Ciudad/Nivel-sheet.png");
        framesNivel = SpriteSheetLoader.recortar(nivelSheetTexture, 41, 41, 9);

        // font por defecto
        font = new BitmapFont();
        font.getData().setScale(1.2f);
    }

    private void crearInterfaz() {
        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.top();

        Table barraHud = new Table();
        barraHud.setBackground(new Image(fondoGrisTexture).getDrawable());

        // DINERO
        Image imgBillete = new Image(billeteTexture);

        Label.LabelStyle estiloTexto = new Label.LabelStyle(font, Color.WHITE);
        labelDinero = new Label("$" + jugador.getDinero(), estiloTexto);
        labelDinero.setAlignment(Align.left);

        // NIVEL
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
        // actualizar
        labelDinero.setText("$" + jugador.getDinero());

        // detección de subida de nivel
        if (jugador.getNivel() > nivelAnterior && !animandoSubidaNivel) {
            animandoSubidaNivel = true;
            tiempoAnimacion = 0f;
        }

        if (animandoSubidaNivel) {
            tiempoAnimacion += delta;

            if (tiempoAnimacion < DURACION_CICLO_XP) {
                //1: reproducir frames 0 al 7
                float progreso = tiempoAnimacion / DURACION_CICLO_XP;
                int frameIndex = (int) (progreso * 8);
                if (frameIndex > 7) frameIndex = 7;

                imgIconoNivel.setDrawable(new TextureRegionDrawable(framesNivel[frameIndex]));
            }
            else if (tiempoAnimacion < DURACION_TOTAL_ANIM) {
                //mostrar frame 8
                imgIconoNivel.setDrawable(new TextureRegionDrawable(framesNivel[8]));
            }
            else {
                //finalizar animación y actualizar visualmente
                animandoSubidaNivel = false;
                nivelAnterior = jugador.getNivel();
                labelNivel.setText(String.valueOf(jugador.getNivel()));
            }
        } else {
            // estado normal - calcular frame (0 a 7) segun el porcentaje de XP actual
            labelNivel.setText(String.valueOf(jugador.getNivel()));

            float porcentaje = (float) jugador.getExperienciaActual() / jugador.getExperienciaSiguienteNivel();
            int frameIndex = (int) (porcentaje * 8);

            // evitar desbordamiento
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
