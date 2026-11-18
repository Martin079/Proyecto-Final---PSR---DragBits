package com.afs.dragbits.menutienda;

import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.mejoras.MejoraConfig;
import com.afs.dragbits.mejoras.TipoMejora;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.afs.dragbits.util.TexturaSolidaFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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
    private static final int TAMANO_CUADRADO = 56; // agrandado de 40 a 56

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

    // Fonts compartidos
    private BitmapFont fontNombre;
    private BitmapFont fontCosto;
    private BitmapFont fontNivel;

    public VentanaTiendaMejoras(Viewport viewport, Runnable accionCerrar, Consumer<Void> alComprarMejora, Jugador jugador) {
        this.viewport = viewport;
        this.stage = new Stage(viewport);
        this.accionCerrar = accionCerrar;
        this.alComprarMejora = alComprarMejora;
        this.jugador = jugador;

        // Inicializar fonts
        fontNombre = new BitmapFont();
        fontNombre.getData().setScale(1.15f);
        fontCosto = new BitmapFont();
        fontCosto.getData().setScale(0.85f);
        fontNivel = new BitmapFont();
        fontNivel.getData().setScale(0.75f);

        fondoTexture = TexturaSolidaFactory.crearTextura((int) ANCHO_VIRTUAL, (int) ALTO_VIRTUAL, new Color(0.1f, 0.1f, 0.1f, 0.85f));

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
        root.add(mainTable).width(860).height(580);
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
        contentTable.defaults().pad(8f);

        scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        mainTable.add(scrollPane).expand().fill();
    }

    public void mostrar(Jugador jugador) {
        this.jugador = jugador;
        this.visible = true;

        contentTable.clear();
        contentTable.defaults().pad(8f);

        for (TipoMejora tipo : TipoMejora.values()) {
            Table filaMejora = crearFilaMejora(tipo);
            contentTable.add(filaMejora).row();
        }

        Gdx.input.setInputProcessor(stage);
    }

    private Table crearFilaMejora(TipoMejora tipo) {
        Table fila = new Table();
        fila.defaults().pad(4f);

        Label.LabelStyle nombreStyle = new Label.LabelStyle(fontNombre, Color.WHITE);
        Label nombreLabel = new Label(tipo.getNombreMostrar(), nombreStyle);
        fila.add(nombreLabel).left().padRight(25f).width(190f);

        Table nivelesTable = new Table();
        nivelesTable.defaults().pad(6f);

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
        int nivelActual = jugador.getNivelMejora(tipo);
        boolean esSiguienteNivel = (nivel == nivelActual + 1);
        boolean estaBloqueadoPorNivel = (nivel > nivelActual + 1) || (nivel < nivelActual); // niveles sin mejora se ignoran

        Color colorCuadrado;
        if (tieneMejora) {
            colorCuadrado = new Color(0f, 0.8f, 0f, 1f); // verde
        } else if (puedeComprar && esSiguienteNivel) {
            colorCuadrado = new Color(0.6f, 0.6f, 0.6f, 1f); // gris
        } else {
            colorCuadrado = new Color(0.8f, 0f, 0f, 1f); // rojo
        }

        Texture cuadradoTexture = TexturaSolidaFactory.crearTextura(TAMANO_CUADRADO, TAMANO_CUADRADO, colorCuadrado);
        Image cuadrado = new Image(cuadradoTexture);

        if (puedeComprar && esSiguienteNivel) {
            final int nivelFinal = nivel;
            final TipoMejora tipoFinal = tipo;
            cuadrado.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (jugador.comprarMejora(tipoFinal, nivelFinal)) {
                        if (alComprarMejora != null) {
                            alComprarMejora.accept(null);
                        }
                        mostrar(jugador);
                    }
                }
            });
        }

        // Cuadrado con label del nivel de jugador requerido DENTRO (centrado)
        Stack stackCuadrado = new Stack();
        stackCuadrado.add(cuadrado);
        stackCuadrado.setSize(TAMANO_CUADRADO, TAMANO_CUADRADO);

        MejoraConfig config = obtenerConfigMejora(tipo, nivel);
        Label.LabelStyle nivelStyle = new Label.LabelStyle(fontNivel, Color.WHITE);
        String nivelTexto = (config != null) ? "Lvl" + config.getNivelJugadorRequerido() : "";
        Label nivelLabel = new Label(nivelTexto, nivelStyle);
        nivelLabel.setAlignment(Align.center);
        nivelLabel.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        stackCuadrado.add(nivelLabel);

        celda.add(stackCuadrado).row();

        // Costo más grande y visible
        if (config != null) {
            Label.LabelStyle costoStyle = new Label.LabelStyle(fontCosto, Color.WHITE);
            Label costoLabel = new Label("$" + config.getCosto(), costoStyle);
            costoLabel.setAlignment(Align.center);
            celda.add(costoLabel).padTop(2f).row();
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
        if (fontNombre != null) fontNombre.dispose();
        if (fontCosto != null) fontCosto.dispose();
        if (fontNivel != null) fontNivel.dispose();
    }
}
