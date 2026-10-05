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

    private static final float ANCHO_BOTON_PAUSA = 75f;
    private static final float ALTO_BOTON_PAUSA = 40f;

    public Basicos(float anchoPantalla, float altoPantalla) {
        super(anchoPantalla, altoPantalla);

        FUENTE = new BitmapFont();
        FUENTE.setColor(Color.valueOf("4DA6FF"));
        FUENTE.getData().setScale(1.8f);

        TEXTURA_BOTON_PAUSA = new Texture("sprites/Botones/Boton pausa.png");

        float btnX = anchoPantalla - 80f;
        float btnY = altoPantalla - 80f;
        BOUNDS_BOTON_PAUSA = new Rectangle(btnX, btnY, ANCHO_BOTON_PAUSA, ALTO_BOTON_PAUSA);
    }

    public void dibujar(SpriteBatch batch, Auto auto, float anchoPantalla) {
        aplicarProyeccion(batch);

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

    public boolean fueBotonPausaTocado(com.badlogic.gdx.math.Vector3 coordsVirtuales) {
        return BOUNDS_BOTON_PAUSA.contains(coordsVirtuales.x, coordsVirtuales.y);
    }

    @Override
    public void resize(float ancho, float alto) {
        super.resize(ancho, alto);
        float btnX = ancho - 80f;
        float btnY = alto - 80f;
        BOUNDS_BOTON_PAUSA.set(btnX, btnY, ANCHO_BOTON_PAUSA, ALTO_BOTON_PAUSA);
    }

    @Override
    public void dispose() {
        if (FUENTE != null) FUENTE.dispose();
        if (TEXTURA_BOTON_PAUSA != null) TEXTURA_BOTON_PAUSA.dispose();
    }
}
