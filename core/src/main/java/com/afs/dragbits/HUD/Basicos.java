package com.afs.dragbits.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

public class Basicos {

    private final OrthographicCamera camaraHUD;
    private final BitmapFont fuente;

    public Basicos(float anchoPantalla, float altoPantalla) {
        camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, anchoPantalla, altoPantalla);

        fuente = new BitmapFont();
        fuente.setColor(Color.valueOf("4DA6FF"));
        fuente.getData().setScale(1.8f); // Texto más grande para acompañar el HUD
    }

    public void dibujar(SpriteBatch batch, Auto auto, float anchoPantalla) {
        batch.setProjectionMatrix(camaraHUD.combined);

        int velKmH = (int) auto.getVelocidad();
        int rpm = (int) auto.getRpm();
        String marchaStr = (auto.getMarchaActual() == 0) ? "N" : String.valueOf(auto.getMarchaActual());
        String embragueStr = auto.isEmbraguePresionado() ? " [EMBRAGUE]" : "";

        // Posición ajustada a la izquierda del borde para dar margen al texto grande
        float posX = anchoPantalla - 310f;
        float posY = 110f;

        fuente.draw(batch, "VEL: " + velKmH + " Km/h", posX, posY);
        fuente.draw(batch, "RPM: " + rpm, posX, posY - 35f);
        fuente.draw(batch, "MARCHA: " + marchaStr + embragueStr, posX, posY - 70f);
    }

    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    public void dispose() {
        if (fuente != null) fuente.dispose();
    }
}
