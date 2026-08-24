package com.afs.dragbits.Ciudad;

import com.afs.dragbits.Jugador.Jugador;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HUD implements Disposable {

    private Stage stage;
    private Jugador jugador;

    // Texturas
    private Texture fondoGrisTexture;
    private Texture billeteTexture;
    private Texture nivelSheetTexture;
    private TextureRegion iconoNivel;

    // Elementos UI
    private Label labelDinero;
    private Label labelNivel;
    private BitmapFont font;

    public HUD(SpriteBatch batch, Jugador jugador) {
        this.jugador = jugador;
        this.stage = new Stage(new ScreenViewport(), batch);

        cargarRecursos();
        crearInterfaz();
    }

    private void cargarRecursos() {
        // 1. Fondo gris oscuro semitransparente (1x1 pixeles escalado)
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.15f, 0.15f, 0.15f, 0.75f)); // Gris oscuro 75% opacidad
        pixmap.fill();
        fondoGrisTexture = new Texture(pixmap);
        pixmap.dispose();

        // 2. Cargar Texturas desde la carpeta assets/Ciudad/
        billeteTexture = new Texture(Gdx.files.internal("Sprites/Ciudad/Billete.png"));
        nivelSheetTexture = new Texture(Gdx.files.internal("Sprites/Ciudad/Nivel-sheet.png"));
        // Nota: Asegúrate de que la imagen en assets se llame NivelSheet.png o ajusta la ruta

        // Tomamos el primer frame del sheet de nivel (41x41 px)
        TextureRegion[][] tmp = TextureRegion.split(nivelSheetTexture, 41, 41);
        iconoNivel = tmp[0][0];

        // Font por defecto para el texto
        font = new BitmapFont();
        font.getData().setScale(1.2f); // Escala para legibilidad
    }

    private void crearInterfaz() {
        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.top(); // Alinear al tope de la pantalla

        // Tabla del fondo del HUD (Largo completo de pantalla, 50px de alto)
        Table barraHud = new Table();
        barraHud.setBackground(new Image(fondoGrisTexture).getDrawable());

        // --- SECCIÓN DINERO ---
        Image imgBillete = new Image(billeteTexture);

        Label.LabelStyle estiloTexto = new Label.LabelStyle(font, Color.WHITE);
        labelDinero = new Label("$" + jugador.getDinero(), estiloTexto);
        labelDinero.setAlignment(Align.left);

        // --- SECCIÓN NIVEL ---
        Image imgIconoNivel = new Image(iconoNivel);
        labelNivel = new Label(String.valueOf(jugador.getNivel()), estiloTexto);
        labelNivel.setAlignment(Align.center);

        // Contenedor que encierra el icono del nivel y el número superpuesto en el centro
        Table contenedorNivel = new Table();
        contenedorNivel.add(imgIconoNivel).size(41, 41);
        // Colocamos el texto del nivel flotando encima del icono
        Table overlayTextoNivel = new Table();
        overlayTextoNivel.add(labelNivel).center();

        // Estructura de contenido dentro del HUD
        Table contenido = new Table();

        // 1. Billete
        contenido.add(imgBillete).size(30, 30).padRight(8);

        // 2. Dinero (espacio fijo calculado para $9.999.999 aprox)
        contenido.add(labelDinero).width(110).padRight(40);

        // 3. Icono de Nivel con texto centrado
        contenido.stack(contenedorNivel, overlayTextoNivel).size(41, 41);

        // Añadir el contenido al HUD con un leve desplazamiento hacia la derecha (padLeft 80px)
        barraHud.add(contenido).expandX().center().padLeft(80);

        // Añadir la barra a la tabla principal
        tablaPrincipal.add(barraHud).growX().height(50);

        stage.addActor(tablaPrincipal);
    }

    public void actualizar() {
        // Sincroniza los valores visuales con la clase Jugador
        labelDinero.setText("$" + jugador.getDinero());
        labelNivel.setText(String.valueOf(jugador.getNivel()));
    }

    public void render() {
        actualizar();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
