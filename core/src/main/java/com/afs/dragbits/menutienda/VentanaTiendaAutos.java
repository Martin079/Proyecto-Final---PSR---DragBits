package com.afs.dragbits.menutienda;

import com.afs.dragbits.autos.AutoConfig;
import com.afs.dragbits.autos.AutoJugadorTipo;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.function.Consumer;

public class VentanaTiendaAutos implements com.badlogic.gdx.utils.Disposable {

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;

    private Stage stage;
    private Viewport viewport;
    private Table mainTable;
    private Table gridTable;
    private Image botonCerrar;
    private Texture fondoTexture;
    private Texture iconosTexture;
    private Texture botonCerrarTexture;

    private Jugador jugador;
    private Consumer<AutoJugadorTipo> alComprarAuto;
    private Runnable accionCerrar;
    private boolean visible = false;

    private static final AutoJugadorTipo[] AUTOS_DISPONIBLES = {
        AutoJugadorTipo.TOYOTA,
        AutoJugadorTipo.CHALLENGER
    };

    public VentanaTiendaAutos(Viewport viewport, Runnable accionCerrar, Consumer<AutoJugadorTipo> alComprarAuto) {
        this.viewport = viewport;
        this.stage = new Stage(viewport);
        this.accionCerrar = accionCerrar;
        this.alComprarAuto = alComprarAuto;

        try {
            fondoTexture = new Texture(Gdx.files.internal("sprites/UI/FondoTienda.png"));
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaAutos", "No se pudo cargar FondoTienda.png, usando fallback", e);
            fondoTexture = TexturaSolidaFactory.crearTextura((int) ANCHO_VIRTUAL, (int) ALTO_VIRTUAL, new Color(0.1f, 0.1f, 0.1f, 0.85f));
        }

        try {
            iconosTexture = new Texture(Gdx.files.internal("sprites/UI/Iconos autos-sheet.png"));
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaAutos", "No se pudo cargar iconos autos-sheet.png", e);
            iconosTexture = null;
        }

        try {
            botonCerrarTexture = SpriteSheetLoader.cargarTextura("sprites/Botones/Boton cerrar.png");
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaAutos", "No se pudo cargar Boton cerrar.png", e);
            botonCerrarTexture = null;
        }

        Table root = new Table();
        root.setFillParent(true);

        mainTable = new Table();
        mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(fondoTexture)));
        mainTable.pad(20);
        root.add(mainTable).width(750).height(500);
        stage.addActor(root);

        if (botonCerrarTexture != null) {
            botonCerrar = new Image(botonCerrarTexture);
            botonCerrar.setScaling(Scaling.fit);

            botonCerrar.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ocultar();
                }
            });

            Table topBar = new Table();
            topBar.add().expandX();
            topBar.add(botonCerrar).size(60, 60).padRight(15).padTop(25).right();
            mainTable.add(topBar).growX().padBottom(15).row();
        }

        gridTable = new Table();
        mainTable.add(gridTable).expand().center();
    }

    public void mostrar(Jugador jugador) {
        this.jugador = jugador;
        this.visible = true;

        gridTable.clear();
        gridTable.defaults().pad(20f).width(340f).height(420f);

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
                        mostrar(jugador);
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
        if (!visible) return;
        this.visible = false;
        if (accionCerrar != null) {
            accionCerrar.run();
        }
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isVisible() {
        return visible;
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
