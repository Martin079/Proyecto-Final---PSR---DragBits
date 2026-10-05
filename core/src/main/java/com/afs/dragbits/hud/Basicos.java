package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.afs.dragbits.autos.Auto;

public class Basicos extends ElementoHUD {

    private final BitmapFont FUENTE;
    private final Texture TEXTURA_BOTON_PAUSA;
    private final Rectangle BOUNDS_BOTON_PAUSA;

    private static final float ANCHO_VIRTUAL = 1920f;
    private static final float ALTO_VIRTUAL = 1080f;

    private static final float ANCHO_BOTON_PAUSA = 105f;
    private static final float ALTO_BOTON_PAUSA = 56f;

    public Basicos() {
        super();

        FUENTE = new BitmapFont();
        FUENTE.setColor(Color.valueOf("4DA6FF"));
        FUENTE.getData().setScale(2.5f);

        TEXTURA_BOTON_PAUSA = new Texture("sprites/Botones/Boton pausa.png");

        float btnX = ANCHO_VIRTUAL - 112f;
        float btnY = ALTO_VIRTUAL - 112f;
        BOUNDS_BOTON_PAUSA = new Rectangle(btnX, btnY, ANCHO_BOTON_PAUSA, ALTO_BOTON_PAUSA);
    }

    public void dibujar(SpriteBatch batch, Auto auto, float anchoPantalla) {
        int velKmH = (int) auto.getVelocidad();
        int rpm = (int) auto.getRpm();
        String marchaStr = (auto.getMarchaActual() == 0) ? "N" : String.valueOf(auto.getMarchaActual());
        String embragueStr = auto.isEmbraguePresionado() ? " [EMBRAGUE]" : "";

        //izquierda
        float posX = anchoPantalla - 310f;
        float posY = 110f;

        FUENTE.draw(batch, "VEL: " + velKmH + " Km/h", posX, posY);
        FUENTE.draw(batch, "RPM: " + rpm, posX, posY - 35f);
        FUENTE.draw(batch, "MARCHA: " + marchaStr + embragueStr, posX, posY - 70f);

        // Dibujar botón de pausa
        batch.draw(TEXTURA_BOTON_PAUSA, BOUNDS_BOTON_PAUSA.x, BOUNDS_BOTON_PAUSA.y, BOUNDS_BOTON_PAUSA.width, BOUNDS_BOTON_PAUSA.height);
    }

    public boolean fueBotonPausaTocado(float x, float y) {
        return BOUNDS_BOTON_PAUSA.contains(x, y);
    }


    @Override
    public void dispose() {
        if (FUENTE != null) FUENTE.dispose();
        if (TEXTURA_BOTON_PAUSA != null) TEXTURA_BOTON_PAUSA.dispose();
    }
}
