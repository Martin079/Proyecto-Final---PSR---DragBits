package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

public class Basicos extends ElementoHUD {

    private final BitmapFont FUENTE;

    public Basicos(float anchoPantalla, float altoPantalla) {
        super(anchoPantalla, altoPantalla);

        FUENTE = new BitmapFont();
        FUENTE.setColor(Color.valueOf("4DA6FF"));
        FUENTE.getData().setScale(1.8f);
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
    }

    @Override
    public void dispose() {
        if (FUENTE != null) FUENTE.dispose();
    }
}
