package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.afs.dragbits.util.TexturaSolidaFactory;


public class CartelResultado extends ElementoHUD {

    private final Texture TEXTURA_CARTEL;
    private final Texture TEXTURA_BOTON;
    private final BitmapFont FUENTE_TEXTO;
    private final Rectangle BOUNDS_BUTTON;

    private static final float ANCHO_CARTEL = 460f;
    private static final float ALTO_CARTEL = 240f;
    private static final float ANCHO_BOTON = 220f;
    private static final float ALTO_BOTON = 50f;

    public CartelResultado(float anchoPantalla, float altoPantalla) {
        super(anchoPantalla, altoPantalla);

        TEXTURA_CARTEL = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.85f));
        TEXTURA_BOTON = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, Color.valueOf("27ae60"));

        FUENTE_TEXTO = new BitmapFont();
        FUENTE_TEXTO.setColor(Color.WHITE);

        float btnX = (anchoPantalla - ANCHO_BOTON) / 2f;
        float btnY = (altoPantalla - ALTO_CARTEL) / 2f + 20f;
        BOUNDS_BUTTON = new Rectangle(btnX, btnY, ANCHO_BOTON, ALTO_BOTON);
    }


    public void dibujar(SpriteBatch batch, boolean gano, int recompensa, long dineroTotal, float anchoPantalla, float altoPantalla) {
        aplicarProyeccion(batch);

        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        batch.draw(TEXTURA_CARTEL, cartelX, cartelY);

        FUENTE_TEXTO.getData().setScale(2f);
        if (gano) {
            FUENTE_TEXTO.setColor(Color.GOLD);
            FUENTE_TEXTO.draw(batch, "¡VICTORIA!", cartelX + 140f, cartelY + 200f);

            FUENTE_TEXTO.getData().setScale(1.2f);
            FUENTE_TEXTO.setColor(Color.GREEN);
            FUENTE_TEXTO.draw(batch, "Recompensa: +$" + recompensa, cartelX + 130f, cartelY + 140f);
        } else {
            FUENTE_TEXTO.setColor(Color.RED);
            FUENTE_TEXTO.draw(batch, "DERROTA", cartelX + 160f, cartelY + 200f);

            FUENTE_TEXTO.getData().setScale(1.2f);
            FUENTE_TEXTO.setColor(Color.WHITE);
            FUENTE_TEXTO.draw(batch, "Recompensa: +$0", cartelX + 165f, cartelY + 140f);
        }

        FUENTE_TEXTO.setColor(Color.WHITE);
        FUENTE_TEXTO.draw(batch, "Total: $" + dineroTotal, cartelX + 175f, cartelY + 100f);

        batch.draw(TEXTURA_BOTON, BOUNDS_BUTTON.x, BOUNDS_BUTTON.y, BOUNDS_BUTTON.width, BOUNDS_BUTTON.height);
        FUENTE_TEXTO.getData().setScale(1.2f);
        FUENTE_TEXTO.draw(batch, "VOLVER AL MAPA", BOUNDS_BUTTON.x + 35f, BOUNDS_BUTTON.y + 32f);
    }

    public boolean fueBotonTocado(Vector3 coordsVirtuales) {
        return BOUNDS_BUTTON.contains(coordsVirtuales.x, coordsVirtuales.y);
    }

    @Override
    public void dispose() {
        if (TEXTURA_CARTEL != null) TEXTURA_CARTEL.dispose();
        if (TEXTURA_BOTON != null) TEXTURA_BOTON.dispose();
        if (FUENTE_TEXTO != null) FUENTE_TEXTO.dispose();
    }
}
