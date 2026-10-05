package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.afs.dragbits.util.TexturaSolidaFactory;


public class CartelResultado extends ElementoHUD {

    private final Texture TEXTURA_CARTEL;
    private final Texture TEXTURA_BOTON;
    private final BitmapFont FUENTE_TEXTO;
    private final Rectangle BOUNDS_BUTTON;

    private static final float ANCHO_CARTEL = 690f;
    private static final float ALTO_CARTEL = 360f;
    private static final float ANCHO_BOTON = 330f;
    private static final float ALTO_BOTON = 75f;

    public CartelResultado(float anchoPantalla, float altoPantalla) {
        super();

        TEXTURA_CARTEL = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.85f));
        TEXTURA_BOTON = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, Color.valueOf("27ae60"));

        FUENTE_TEXTO = new BitmapFont();
        FUENTE_TEXTO.setColor(Color.WHITE);

        float btnX = (anchoPantalla - ANCHO_BOTON) / 2f;
        float btnY = (altoPantalla - ALTO_CARTEL) / 2f + 30f;
        BOUNDS_BUTTON = new Rectangle(btnX, btnY, ANCHO_BOTON, ALTO_BOTON);
    }


    public void dibujar(SpriteBatch batch, boolean gano, int recompensa, long dineroTotal, float anchoPantalla, float altoPantalla) {
        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        float btnX = (anchoPantalla - ANCHO_BOTON) / 2f;
        float btnY = cartelY + 30f;
        BOUNDS_BUTTON.set(btnX, btnY, ANCHO_BOTON, ALTO_BOTON);

        batch.draw(TEXTURA_CARTEL, cartelX, cartelY);

        GlyphLayout layout = new GlyphLayout();
        FUENTE_TEXTO.getData().setScale(3f);
        if (gano) {
            FUENTE_TEXTO.setColor(Color.GOLD);
            layout.setText(FUENTE_TEXTO, "¡VICTORIA!");
            FUENTE_TEXTO.draw(batch, "¡VICTORIA!", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 300f);

            FUENTE_TEXTO.getData().setScale(1.8f);
            FUENTE_TEXTO.setColor(Color.GREEN);
            layout.setText(FUENTE_TEXTO, "Recompensa: +$" + recompensa);
            FUENTE_TEXTO.draw(batch, "Recompensa: +$" + recompensa, cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 210f);
        } else {
            FUENTE_TEXTO.setColor(Color.RED);
            layout.setText(FUENTE_TEXTO, "DERROTA");
            FUENTE_TEXTO.draw(batch, "DERROTA", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 300f);

            FUENTE_TEXTO.getData().setScale(1.8f);
            FUENTE_TEXTO.setColor(Color.WHITE);
            layout.setText(FUENTE_TEXTO, "Recompensa: +$0");
            FUENTE_TEXTO.draw(batch, "Recompensa: +$0", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 210f);
        }

        FUENTE_TEXTO.setColor(Color.WHITE);
        layout.setText(FUENTE_TEXTO, "Total: $" + dineroTotal);
        FUENTE_TEXTO.draw(batch, "Total: $" + dineroTotal, cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 150f);

        batch.draw(TEXTURA_BOTON, BOUNDS_BUTTON.x, BOUNDS_BUTTON.y, BOUNDS_BUTTON.width, BOUNDS_BUTTON.height);
        FUENTE_TEXTO.getData().setScale(1.8f);
        layout.setText(FUENTE_TEXTO, "VOLVER AL MAPA");
        FUENTE_TEXTO.draw(batch, "VOLVER AL MAPA", BOUNDS_BUTTON.x + (ANCHO_BOTON - layout.width) / 2f, BOUNDS_BUTTON.y + 48f);
    }

    public boolean fueBotonTocado(float x, float y) {
        return BOUNDS_BUTTON.contains(x, y);
    }

    @Override
    public void dispose() {
        if (TEXTURA_CARTEL != null) TEXTURA_CARTEL.dispose();
        if (TEXTURA_BOTON != null) TEXTURA_BOTON.dispose();
        if (FUENTE_TEXTO != null) FUENTE_TEXTO.dispose();
    }
}
