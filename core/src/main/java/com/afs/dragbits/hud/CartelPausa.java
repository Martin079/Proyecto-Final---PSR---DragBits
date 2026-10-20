package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.afs.dragbits.util.TexturaSolidaFactory;

public class CartelPausa extends ElementoHUD {

    private final Texture TEXTURA_FONDO;
    private final Texture TEXTURA_BOTON_REANUDAR;
    private final Texture TEXTURA_BOTON_ABANDONAR;
    private final BitmapFont FUENTE_TEXTO;
    private final Rectangle BOUNDS_REANUDAR;
    private final Rectangle BOUNDS_ABANDONAR;

    private static final float ANCHO_CARTEL = 600f;
    private static final float ALTO_CARTEL = 375f;
    private static final float ANCHO_BOTON = 240f;
    private static final float ALTO_BOTON = 68f;

    public CartelPausa(float anchoPantalla, float altoPantalla) {
        super();

        TEXTURA_FONDO = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.7f));
        TEXTURA_BOTON_REANUDAR = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, Color.valueOf("27ae60"));
        TEXTURA_BOTON_ABANDONAR = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, Color.valueOf("c0392b"));

        FUENTE_TEXTO = new BitmapFont();
        FUENTE_TEXTO.setColor(Color.WHITE);

        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;
        
        float btnY = cartelY + 45f;
        BOUNDS_REANUDAR = new Rectangle(cartelX + 45f, btnY, ANCHO_BOTON, ALTO_BOTON);
        BOUNDS_ABANDONAR = new Rectangle(cartelX + ANCHO_CARTEL - 45f - ANCHO_BOTON, btnY, ANCHO_BOTON, ALTO_BOTON);
    }

    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla) {
        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        batch.draw(TEXTURA_FONDO, cartelX, cartelY);

        GlyphLayout layout = new GlyphLayout();
        FUENTE_TEXTO.getData().setScale(3f);
        FUENTE_TEXTO.setColor(Color.WHITE);
        layout.setText(FUENTE_TEXTO, "PAUSA");
        FUENTE_TEXTO.draw(batch, "PAUSA", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 300f);

        batch.draw(TEXTURA_BOTON_REANUDAR, BOUNDS_REANUDAR.x, BOUNDS_REANUDAR.y, BOUNDS_REANUDAR.width, BOUNDS_REANUDAR.height);
        FUENTE_TEXTO.getData().setScale(1.8f);
        FUENTE_TEXTO.setColor(Color.WHITE);
        layout.setText(FUENTE_TEXTO, "REANUDAR");
        FUENTE_TEXTO.draw(batch, "REANUDAR", BOUNDS_REANUDAR.x + (ANCHO_BOTON - layout.width) / 2f, BOUNDS_REANUDAR.y + 45f);

        batch.draw(TEXTURA_BOTON_ABANDONAR, BOUNDS_ABANDONAR.x, BOUNDS_ABANDONAR.y, BOUNDS_ABANDONAR.width, BOUNDS_ABANDONAR.height);
        layout.setText(FUENTE_TEXTO, "ABANDONAR");
        FUENTE_TEXTO.draw(batch, "ABANDONAR", BOUNDS_ABANDONAR.x + (ANCHO_BOTON - layout.width) / 2f, BOUNDS_ABANDONAR.y + 45f);
    }

    public boolean fueBotonReanudarTocado(float x, float y) {
        return BOUNDS_REANUDAR.contains(x, y);
    }

    public boolean fueBotonAbandonarTocado(float x, float y) {
        return BOUNDS_ABANDONAR.contains(x, y);
    }

    @Override
    public void dispose() {
        if (TEXTURA_FONDO != null) TEXTURA_FONDO.dispose();
        if (TEXTURA_BOTON_REANUDAR != null) TEXTURA_BOTON_REANUDAR.dispose();
        if (TEXTURA_BOTON_ABANDONAR != null) TEXTURA_BOTON_ABANDONAR.dispose();
        if (FUENTE_TEXTO != null) FUENTE_TEXTO.dispose();
    }
}
