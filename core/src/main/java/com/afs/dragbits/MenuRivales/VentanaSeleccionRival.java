package com.afs.dragbits.MenuRivales;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.afs.dragbits.Main;
import com.afs.dragbits.screens.GameScreen;

public class VentanaSeleccionRival implements Disposable {

    public enum TipoCarrera { LEGAL, ILEGAL }

    private final Main game;
    private Stage stage;
    private boolean visible = false;

    // Recogida de Recursos
    private Texture fondoOscuroTexture;
    private Texture menuAutosSheet;
    private Texture botonCerrarTexture;
    private TextureRegion[] framesRival;
    private TextureRegion frameBloqueado;

    // Interface
    private Table ventanaTable;

    public VentanaSeleccionRival(Main game, Viewport viewport, Runnable accionCerrar) {
        this.game = game;
        this.stage = new Stage(viewport);
        cargarRecursos();
        crearEstructuraBase(accionCerrar);
    }

    private void cargarRecursos() {
        // Fondo gris oscuro semitransparente casi opaco (Alpha 0.9)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.1f, 0.90f));
        pixmap.fill();
        fondoOscuroTexture = new Texture(pixmap);
        pixmap.dispose();

        // Sheet 14 cuadros de 200x200 px
        menuAutosSheet = new Texture(Gdx.files.internal("sprites/MenuAutos/Iconos autos-sheet.png"));
        TextureRegion[][] tmp = TextureRegion.split(menuAutosSheet, 200, 200);

        framesRival = new TextureRegion[10];
        for (int i = 0; i < 10; i++) {
            framesRival[i] = tmp[0][i];
        }
        frameBloqueado = tmp[0][13]; // Ícono '?'

        // Botón Cerrar
        botonCerrarTexture = new Texture(Gdx.files.internal("Sprites/Botones/Boton cerrar.png"));
    }

    private void crearEstructuraBase(Runnable accionCerrar) {
        Table root = new Table();
        root.setFillParent(true);

        // Panel Central
        ventanaTable = new Table();
        ventanaTable.setBackground(new TextureRegionDrawable(new TextureRegion(fondoOscuroTexture)));
        ventanaTable.pad(20);

        root.add(ventanaTable).width(800).height(520);
        stage.addActor(root);
    }

    public void mostrar(TipoCarrera tipoCarrera, int maxRivalDesbloqueado) {
        this.visible = true;
        ventanaTable.clear();

        // 1. BOTÓN DE CERRAR
        Image btnCerrar = new Image(botonCerrarTexture);
        btnCerrar.setScaling(Scaling.fit);

        btnCerrar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ocultar();
            }
        });

        Table topBar = new Table();
        topBar.add().expandX();
        topBar.add(btnCerrar).size(80, 80).right();
        ventanaTable.add(topBar).growX().padBottom(15).row();

        // 2. CONTENEDOR DE RIVALES (3 Arriba / 2 Abajo)
        Table grillaRivales = new Table();
        int offsetInicio = (tipoCarrera == TipoCarrera.LEGAL) ? 0 : 5;

        // --- FILA SUPERIOR (3 Rivales) ---
        Table filaArriba = new Table();
        for (int i = 0; i < 3; i++) {
            int indiceRival = i;
            boolean desbloqueado = indiceRival <= maxRivalDesbloqueado;
            TextureRegion region = desbloqueado ? framesRival[offsetInicio + indiceRival] : frameBloqueado;

            ImageButton btnRival = new ImageButton(new TextureRegionDrawable(region));
            btnRival.getImage().setScaling(Scaling.fit);
            if (desbloqueado) {
                btnRival.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ocultar();
                        // Transición a GameScreen (puedes pasarle parámetros si tu constructor de GameScreen lo requiere)
                        game.setScreen(new GameScreen(game));
                    }
                });
            }

            filaArriba.add(btnRival).size(150, 150).pad(15);
        }
        grillaRivales.add(filaArriba).padBottom(15).row();

        // --- FILA INFERIOR (2 Rivales) ---
        Table filaAbajo = new Table();
        for (int i = 3; i < 5; i++) {
            int indiceRival = i;
            boolean desbloqueado = indiceRival <= maxRivalDesbloqueado;
            TextureRegion region = desbloqueado ? framesRival[offsetInicio + indiceRival] : frameBloqueado;

            ImageButton btnRival = new ImageButton(new TextureRegionDrawable(region));
            btnRival.getImage().setScaling(Scaling.fit);
            if (desbloqueado) {
                btnRival.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ocultar();
                        // Transición a GameScreen
                        game.setScreen(new GameScreen(game));
                    }
                });
            }

            filaAbajo.add(btnRival).size(150, 150).pad(15);
        }
        grillaRivales.add(filaAbajo).row();

        ventanaTable.add(grillaRivales).expand().center();
    }

    public void ocultar() {
        this.visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public Stage getStage() {
        return stage;
    }

    public void render(float delta) {
        if (!visible) return;
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        fondoOscuroTexture.dispose();
        menuAutosSheet.dispose();
        botonCerrarTexture.dispose();
    }
}
