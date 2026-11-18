package com.afs.dragbits.hud;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.afs.dragbits.util.TexturaSolidaFactory;

public class CartelPausaMapa extends ElementoHUD {

    private final Texture TEXTURA_FONDO;
    private final Texture TEXTURA_BOTON;
    private final BitmapFont FUENTE_TEXTO;

    private Rectangle boundsReanudar;
    private Rectangle boundsMenu;
    private Rectangle boundsSalir;

    private static final float ANCHO_CARTEL = 500f;
    private static final float ALTO_CARTEL = 300f;
    private static final float ANCHO_BOTON = 240f;
    private static final float ALTO_BOTON = 68f;
    private static final float ESPACIO_ENTRE_BOTONES = 15f;

    private final Game GAME;

    public CartelPausaMapa(float anchoPantalla, float altoPantalla, Game GAME) {
        super();
        this.GAME = GAME;

        TEXTURA_FONDO = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.85f));
        TEXTURA_BOTON = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, new Color(0.4f, 0.4f, 0.4f, 1f));

        FUENTE_TEXTO = new BitmapFont();
        FUENTE_TEXTO.setColor(Color.WHITE);

        actualizarBounds(anchoPantalla, altoPantalla);
    }

    private void actualizarBounds(float anchoPantalla, float altoPantalla) {
        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        float alturaTotalBotones = 3 * ALTO_BOTON + 2 * ESPACIO_ENTRE_BOTONES;
        float inicioY = cartelY + (ALTO_CARTEL + alturaTotalBotones) / 2f - ALTO_BOTON;

        float centroX = cartelX + (ANCHO_CARTEL - ANCHO_BOTON) / 2f;

        boundsReanudar = new Rectangle(centroX, inicioY, ANCHO_BOTON, ALTO_BOTON);
        boundsMenu = new Rectangle(centroX, inicioY - (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);
        boundsSalir = new Rectangle(centroX, inicioY - 2 * (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);
    }

    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla) {
        actualizarBounds(anchoPantalla, altoPantalla);

        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        batch.draw(TEXTURA_FONDO, cartelX, cartelY);

        GlyphLayout layout = new GlyphLayout();

        FUENTE_TEXTO.getData().setScale(3f);
        FUENTE_TEXTO.setColor(Color.WHITE);
        layout.setText(FUENTE_TEXTO, "PAUSA");
        FUENTE_TEXTO.draw(batch, "PAUSA", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + ALTO_CARTEL - 50f);

        batch.draw(TEXTURA_BOTON, boundsReanudar.x, boundsReanudar.y, boundsReanudar.width, boundsReanudar.height);
        FUENTE_TEXTO.getData().setScale(1.8f);
        FUENTE_TEXTO.setColor(Color.WHITE);
        layout.setText(FUENTE_TEXTO, "REANUDAR");
        FUENTE_TEXTO.draw(batch, "REANUDAR", boundsReanudar.x + (ANCHO_BOTON - layout.width) / 2f, boundsReanudar.y + 45f);

        batch.draw(TEXTURA_BOTON, boundsMenu.x, boundsMenu.y, boundsMenu.width, boundsMenu.height);
        layout.setText(FUENTE_TEXTO, "VOLVER AL MENÚ");
        FUENTE_TEXTO.draw(batch, "VOLVER AL MENÚ", boundsMenu.x + (ANCHO_BOTON - layout.width) / 2f, boundsMenu.y + 45f);

        batch.draw(TEXTURA_BOTON, boundsSalir.x, boundsSalir.y, boundsSalir.width, boundsSalir.height);
        layout.setText(FUENTE_TEXTO, "SALIR");
        FUENTE_TEXTO.draw(batch, "SALIR", boundsSalir.x + (ANCHO_BOTON - layout.width) / 2f, boundsSalir.y + 45f);
    }

    public boolean fueBotonReanudarTocado(float x, float y) {
        return boundsReanudar.contains(x, y);
    }

    public boolean fueBotonMenuTocado(float x, float y) {
        return boundsMenu.contains(x, y);
    }

    public boolean fueBotonSalirTocado(float x, float y) {
        return boundsSalir.contains(x, y);
    }

    public void volverAlMenuPrincipal() {
        Gdx.app.postRunnable(() -> GAME.setScreen(new com.afs.dragbits.screens.MainMenuScreen(GAME, (com.afs.dragbits.Main) GAME)));
    }

    public void salirAlEscritorio() {
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        if (TEXTURA_FONDO != null) TEXTURA_FONDO.dispose();
        if (TEXTURA_BOTON != null) TEXTURA_BOTON.dispose();
        if (FUENTE_TEXTO != null) FUENTE_TEXTO.dispose();
    }
}
