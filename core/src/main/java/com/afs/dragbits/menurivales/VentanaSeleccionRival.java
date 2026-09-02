package com.afs.dragbits.menurivales;

import com.badlogic.gdx.graphics.Color;
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
import com.afs.dragbits.util.SpriteSheetLoader;
import com.afs.dragbits.util.TexturaSolidaFactory;

public class VentanaSeleccionRival implements Disposable {

    public enum TipoCarrera { LEGAL, ILEGAL }

    private final Main game;
    private Stage stage;
    private boolean visible = false;

    // Recursos
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
        // Fondo gris oscuro semitransparente usando la fábrica
        fondoOscuroTexture = TexturaSolidaFactory.crearTextura(new Color(0.1f, 0.1f, 0.1f, 0.90f));

        // Cargar sheet de 14 cuadros de 200x200 px usando SpriteSheetLoader
        menuAutosSheet = SpriteSheetLoader.cargarTextura("sprites/MenuAutos/Iconos autos-sheet.png");
        TextureRegion[] todosLosFrames = SpriteSheetLoader.recortar(menuAutosSheet, 200, 200, 14);

        // Copiamos los primeros 10 frames para los rivales
        framesRival = new TextureRegion[10];
        System.arraycopy(todosLosFrames, 0, framesRival, 0, 10);

        // Frame del auto bloqueado (ícono '?')
        frameBloqueado = todosLosFrames[13];

        // Botón Cerrar
        botonCerrarTexture = SpriteSheetLoader.cargarTextura("sprites/Botones/Boton cerrar.png");
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

        // BOTÓN CERRAR
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

        // CONTENEDOR DE RIVALES (3 Arriba / 2 Abajo)
        Table grillaRivales = new Table();

        // FILA SUPERIOR (3 rivales
        Table filaArriba = crearFilaRivales(0, 3, tipoCarrera, maxRivalDesbloqueado);
        grillaRivales.add(filaArriba).padBottom(15).row();

        // FILA INFERIOR (2 rivales)
        Table filaAbajo = crearFilaRivales(3, 5, tipoCarrera, maxRivalDesbloqueado);
        grillaRivales.add(filaAbajo).row();

        ventanaTable.add(grillaRivales).expand().center();
    }

    private Table crearFilaRivales(int inicio, int fin, TipoCarrera tipoCarrera, int maxRivalDesbloqueado) {
        Table fila = new Table();
        int offsetInicio = (tipoCarrera == TipoCarrera.LEGAL) ? 0 : 5;

        for (int i = inicio; i < fin; i++) {
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
                        game.setScreen(new GameScreen(game));
                    }
                });
            }

            fila.add(btnRival).size(150, 150).pad(15);
        }

        return fila;
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
