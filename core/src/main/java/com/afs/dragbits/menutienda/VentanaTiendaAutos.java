package com.afs.dragbits.menutienda;

import com.afs.dragbits.autos.AutoConfig;
import com.afs.dragbits.autos.AutoJugadorTipo;
import com.afs.dragbits.jugador.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.function.Consumer;

public class VentanaTiendaAutos implements com.badlogic.gdx.utils.Disposable {

    private static final float ANCHO_VIRTUAL = 1920f;
    private static final float ALTO_VIRTUAL = 1080f;

    private Stage stage;
    private Viewport viewport;
    private Table mainTable;
    private Table gridTable;
    private ImageButton botonCerrar;
    private Texture fondoTexture;
    private Texture iconosTexture;
    private Texture botonCerrarTexture;

    private Jugador jugador;
    private Consumer<AutoJugadorTipo> alComprarAuto;

    private static final AutoJugadorTipo[] AUTOS_DISPONIBLES = {
        AutoJugadorTipo.TOYOTA,
        AutoJugadorTipo.CHALLENGER
    };

    public VentanaTiendaAutos() {
        viewport = new FitViewport(ANCHO_VIRTUAL, ALTO_VIRTUAL);
        stage = new Stage(viewport);

        fondoTexture = new Texture(Gdx.files.internal("sprites/UI/FondoTienda.png"));
        if (!fondoTexture.getTextureData().isPrepared()) {
            fondoTexture.getTextureData().prepare();
        }

        try {
            iconosTexture = new Texture(Gdx.files.internal("sprites/UI/Iconos autos-sheet.png"));
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaAutos", "No se pudo cargar iconos autos-sheet.png", e);
            iconosTexture = null;
        }

        try {
            botonCerrarTexture = new Texture(Gdx.files.internal("sprites/UI/Boton cerrar.png"));
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaAutos", "No se pudo cargar Boton cerrar.png", e);
            botonCerrarTexture = null;
        }

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(fondoTexture)));
        stage.addActor(mainTable);

        gridTable = new Table();
        mainTable.add(gridTable).pad(100f).grow().row();

        Table botonCerrarTable = new Table();
        mainTable.add(botonCerrarTable).pad(20f);

        if (botonCerrarTexture != null) {
            ImageButton.ImageButtonStyle cerrarStyle = new ImageButton.ImageButtonStyle();
            cerrarStyle.imageUp = new TextureRegionDrawable(new TextureRegion(botonCerrarTexture));
            botonCerrar = new ImageButton(cerrarStyle);
            botonCerrarTable.add(botonCerrar).size(80f, 80f);

            botonCerrar.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                    ocultar();
                    return true;
                }
            });
        }
    }

    public void mostrar(Jugador jugador, Consumer<AutoJugadorTipo> alComprarAuto) {
        this.jugador = jugador;
        this.alComprarAuto = alComprarAuto;

        gridTable.clear();
        gridTable.defaults().pad(30f).width(400f).height(500f);

        for (AutoJugadorTipo tipo : AUTOS_DISPONIBLES) {
            AutoConfig config = obtenerConfig(tipo);
            if (config == null) continue;

            Table celda = crearCeldaAuto(tipo, config);
            gridTable.add(celda);
        }

        Gdx.input.setInputProcessor(stage);
    }

    private Table crearCeldaAuto(AutoJugadorTipo tipo, AutoConfig config) {
        Table celda = new Table();

        boolean tieneAuto = jugador.tieneAuto(tipo);
        boolean nivelSuficiente = jugador.getNivel() >= config.getNivelRequerido();
        boolean dineroSuficiente = jugador.getDinero() >= config.getCosto();
        boolean puedeComprar = !tieneAuto && nivelSuficiente && dineroSuficiente;

        Color overlayColor;
        String estadoTexto;

        if (tieneAuto) {
            overlayColor = new Color(0f, 0.8f, 0f, 0.3f);
            estadoTexto = "ADQUIRIDO";
        } else if (puedeComprar) {
            overlayColor = new Color(0.5f, 0.5f, 0.5f, 0.3f);
            estadoTexto = "COMPRAR $" + config.getCosto();
        } else if (!nivelSuficiente) {
            overlayColor = new Color(0.8f, 0f, 0f, 0.3f);
            estadoTexto = "NIVEL " + config.getNivelRequerido() + " REQ";
        } else {
            overlayColor = new Color(0.8f, 0f, 0f, 0.3f);
            long faltante = config.getCosto() - jugador.getDinero();
            estadoTexto = "$" + faltante + " FALTAN";
        }

        Texture overlayTexture = TexturaSolidaFactory.crearTextura(400, 500, overlayColor);
        celda.setBackground(new TextureRegionDrawable(new TextureRegion(overlayTexture)));

        if (iconosTexture != null) {
            TextureRegion iconoRegion = obtenerIconoAuto(tipo);
            if (iconoRegion != null) {
                ImageButton iconoBtn = new ImageButton(new TextureRegionDrawable(iconoRegion));
                iconoBtn.getImageCell().size(200f, 150f);
                celda.add(iconoBtn).padTop(20f).row();
            }
        }

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        Label nombreLabel = new Label(tipo.getNombre(), labelStyle);
        nombreLabel.setFontScale(2f);
        celda.add(nombreLabel).padTop(20f).row();

        String stats = String.format("V-Max: %.0f km/h\n0-100: %.1fs\nTracción: %.1f\nNitro: %.1fs",
            config.getVelocidadMaxima(),
            config.getAceleracion(),
            config.getTraccion(),
            config.getCapacidadNitro());
        Label statsLabel = new Label(stats, labelStyle);
        statsLabel.setFontScale(1.2f);
        celda.add(statsLabel).padTop(15f).row();

        Label estadoLabel = new Label(estadoTexto, labelStyle);
        estadoLabel.setFontScale(1.5f);
        estadoLabel.setColor(puedeComprar ? Color.YELLOW : (tieneAuto ? Color.GREEN : Color.RED));
        celda.add(estadoLabel).padTop(20f).row();

        if (puedeComprar) {
            celda.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                    if (jugador.comprarAuto(tipo)) {
                        if (alComprarAuto != null) {
                            alComprarAuto.accept(tipo);
                        }
                        mostrar(jugador, alComprarAuto);
                    }
                    return true;
                }
            });
        }

        return celda;
    }

    private TextureRegion obtenerIconoAuto(AutoJugadorTipo tipo) {
        if (iconosTexture == null) return null;

        int anchoFrame = iconosTexture.getWidth() / 3;
        int altoFrame = iconosTexture.getHeight();

        int indice;
        switch (tipo) {
            case TOYOTA: indice = 0; break;
            case CHALLENGER: indice = 1; break;
            default: indice = 0;
        }

        return new TextureRegion(iconosTexture, indice * anchoFrame, 0, anchoFrame, altoFrame);
    }

    private AutoConfig obtenerConfig(AutoJugadorTipo tipo) {
        switch (tipo) {
            case TOYOTA: return AutoConfig.toyota();
            case CHALLENGER: return AutoConfig.challenger();
            default: return null;
        }
    }

    public void ocultar() {
        Gdx.input.setInputProcessor(null);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (fondoTexture != null) fondoTexture.dispose();
        if (iconosTexture != null) iconosTexture.dispose();
        if (botonCerrarTexture != null) botonCerrarTexture.dispose();
    }

    private static class TexturaSolidaFactory {
        public static Texture crearTextura(int ancho, int alto, Color color) {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(ancho, alto, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            return texture;
        }
    }
}
