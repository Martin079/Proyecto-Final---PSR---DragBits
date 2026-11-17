package com.afs.dragbits.menutienda;

import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.mejoras.MejoraConfig;
import com.afs.dragbits.mejoras.TipoMejora;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.afs.dragbits.util.TexturaSolidaFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.function.Consumer;

public class VentanaTiendaMejoras implements com.badlogic.gdx.utils.Disposable {

    private static final float ANCHO_VIRTUAL = 1280f;
    private static final float ALTO_VIRTUAL = 720f;
    private static final int TAMANO_CUADRADO = 40;

    private Stage stage;
    private Viewport viewport;
    private Table mainTable;
    private Table contentTable;
    private ScrollPane scrollPane;
    private Image botonCerrar;
    private Texture fondoTexture;
    private Texture botonCerrarTexture;

    private Jugador jugador;
    private Consumer<Void> alComprarMejora;
    private Runnable accionCerrar;
    private boolean visible = false;

    public VentanaTiendaMejoras(Viewport viewport, Runnable accionCerrar, Consumer<Void> alComprarMejora, Jugador jugador) {
        this.viewport = viewport;
        this.stage = new Stage(viewport);
        this.accionCerrar = accionCerrar;
        this.alComprarMejora = alComprarMejora;
        this.jugador = jugador;

        try {
            fondoTexture = new Texture(Gdx.files.internal("sprites/UI/FondoTienda.png"));
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaMejoras", "No se pudo cargar FondoTienda.png, usando fallback", e);
            fondoTexture = TexturaSolidaFactory.crearTextura((int) ANCHO_VIRTUAL, (int) ALTO_VIRTUAL, new Color(0.1f, 0.1f, 0.1f, 0.85f));
        }

        try {
            botonCerrarTexture = SpriteSheetLoader.cargarTextura("sprites/Botones/Boton cerrar.png");
        } catch (Exception e) {
            Gdx.app.error("VentanaTiendaMejoras", "No se pudo cargar Boton cerrar.png", e);
            botonCerrarTexture = null;
        }

        Table root = new Table();
        root.setFillParent(true);

        mainTable = new Table();
        mainTable.setBackground(new TextureRegionDrawable(new TextureRegion(fondoTexture)));
        mainTable.pad(20);
        root.add(mainTable).width(800).height(550);
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
            topBar.add(botonCerrar).size(60, 60).padRight(15).padTop(15).right();
            mainTable.add(topBar).growX().padBottom(-15).row();
        }

        contentTable = new Table();
        contentTable.defaults().pad(15f);

        scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        mainTable.add(scrollPane).expand().fill();
    }

    public void mostrar(Jugador jugador) {
        this.jugador = jugador;
        this.visible = true;

        contentTable.clear();
        contentTable.defaults().pad(15f);

        for (TipoMejora tipo : TipoMejora.values()) {
            Table filaMejora = crearFilaMejora(tipo);
            contentTable.add(filaMejora).row();
        }

        Gdx.input.setInputProcessor(stage);
    }

    private Table crearFilaMejora(TipoMejora tipo) {
        Table fila = new Table();
        fila.defaults().pad(10f);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        Label nombreLabel = new Label(tipo.getNombreMostrar(), labelStyle);
        nombreLabel.setFontScale(1.3f);
        fila.add(nombreLabel).left().padRight(30f).width(180f);

        Table nivelesTable = new Table();
        nivelesTable.defaults().pad(5f);

        int maxNivel = obtenerMaxNivel(tipo);
        for (int nivel = 1; nivel <= maxNivel; nivel++) {
            Table celdaNivel = crearCeldaNivel(tipo, nivel);
            nivelesTable.add(celdaNivel);
        }

        fila.add(nivelesTable).expandX().left();
        return fila;
    }

    private Table crearCeldaNivel(TipoMejora tipo, int nivel) {
        Table celda = new Table();
        celda.defaults().pad(2f);

        boolean tieneMejora = jugador.tieneMejora(tipo, nivel);
        boolean puedeComprar = jugador.puedeComprarMejora(tipo, nivel);

        Color colorCuadrado;
        if (tieneMejora) {
            colorCuadrado = new Color(0f, 0.8f, 0f, 1f);
        } else if (puedeComprar) {
            colorCuadrado = new Color(0.6f, 0.6f, 0.6f, 1f);
        } else {
            colorCuadrado = new Color(0.8f, 0f, 0f, 1f);
        }

        Texture cuadradoTexture = TexturaSolidaFactory.crearTextura(TAMANO_CUADRADO, TAMANO_CUADRADO, colorCuadrado);
        Image cuadrado = new Image(cuadradoTexture);

        if (puedeComprar) {
            cuadrado.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (jugador.comprarMejora(tipo, nivel)) {
                        if (alComprarMejora != null) {
                            alComprarMejora.accept(null);
                        }
                        mostrar(jugador);
                    }
                }
            });
        }

        celda.add(cuadrado).size(TAMANO_CUADRADO, TAMANO_CUADRADO).row();

        MejoraConfig config = obtenerConfigMejora(tipo, nivel);
        if (config != null) {
            Label.LabelStyle costoStyle = new Label.LabelStyle();
            costoStyle.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
            costoStyle.fontColor = Color.WHITE;

            Label costoLabel = new Label("$" + config.getCosto(), costoStyle);
            costoLabel.setFontScale(0.7f);
            celda.add(costoLabel).row();
        }

        return celda;
    }

    private int obtenerMaxNivel(TipoMejora tipo) {
        switch (tipo) {
            case ACELERACION:
            case VELOCIDAD_MAXIMA:
            case TRACCION:
                return 5;
            case NITRO:
            case POTENCIA_NITRO:
                return 4;
            default:
                return 5;
        }
    }

    private MejoraConfig obtenerConfigMejora(TipoMejora tipo, int nivel) {
        switch (tipo) {
            case ACELERACION:
                switch (nivel) {
                    case 1: return MejoraConfig.aceleracionI();
                    case 2: return MejoraConfig.aceleracionII();
                    case 3: return MejoraConfig.aceleracionIII();
                    case 4: return MejoraConfig.aceleracionIV();
                    case 5: return MejoraConfig.aceleracionV();
                    default: return null;
                }
            case VELOCIDAD_MAXIMA:
                switch (nivel) {
                    case 1: return MejoraConfig.velocidadMaximaI();
                    case 2: return MejoraConfig.velocidadMaximaII();
                    case 3: return MejoraConfig.velocidadMaximaIII();
                    case 4: return MejoraConfig.velocidadMaximaIV();
                    case 5: return MejoraConfig.velocidadMaximaV();
                    default: return null;
                }
            case TRACCION:
                switch (nivel) {
                    case 1: return MejoraConfig.traccionI();
                    case 2: return MejoraConfig.traccionII();
                    case 3: return MejoraConfig.traccionIII();
                    case 4: return MejoraConfig.traccionIV();
                    case 5: return MejoraConfig.traccionV();
                    default: return null;
                }
            case NITRO:
                switch (nivel) {
                    case 1: return MejoraConfig.nitroI();
                    case 2: return MejoraConfig.nitroII();
                    case 3: return MejoraConfig.nitroIII();
                    case 4: return MejoraConfig.nitroIV();
                    default: return null;
                }
            case POTENCIA_NITRO:
                switch (nivel) {
                    case 1: return MejoraConfig.potenciaNitroI();
                    case 2: return MejoraConfig.potenciaNitroII();
                    case 3: return MejoraConfig.potenciaNitroIII();
                    case 4: return MejoraConfig.potenciaNitroIV();
                    default: return null;
                }
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
        if (botonCerrarTexture != null) botonCerrarTexture.dispose();
    }
}
