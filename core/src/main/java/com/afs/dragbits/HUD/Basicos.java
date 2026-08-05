package com.afs.dragbits.HUD;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

/**
 * Renderiza el tablero/interfaz estático abajo a la derecha.
 */
public class Basicos {

    private final OrthographicCamera camaraHUD;
    private final BitmapFont fuente;

    public Basicos(float anchoPantalla, float altoPantalla) {
        // Cámara estática que NO se mueve con el auto
        camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, anchoPantalla, altoPantalla);

        // Fuente por defecto de LibGDX pintada de azul claro
        fuente = new BitmapFont();
        fuente.setColor(Color.valueOf("4DA6FF")); // Azul celeste brillante
        fuente.getData().setScale(1.3f);          // Tamaño de texto
    }

    public void dibujar(SpriteBatch batch, Auto auto, float anchoPantalla) {
        // Aplicar proyección de la cámara del HUD antes de dibujar el texto
        batch.setProjectionMatrix(camaraHUD.combined);

        // Dar formato a los valores
        int velKmH = (int) (auto.getVelocidad() * 0.5f); // Conversión a Km/h ficticia
        int rpm = (int) auto.getRpm();
        String marchaStr = (auto.getMarchaActual() == 0) ? "N" : String.valueOf(auto.getMarchaActual());
        String embragueStr = auto.isEmbraguePresionado() ? " [EMBRAGUE]" : "";

        // Posición abajo a la derecha (offset desde el borde derecho)
        float posX = anchoPantalla - 220f;
        float posY = 90f;

        fuente.draw(batch, "VEL: " + velKmH + " Km/h", posX, posY);
        fuente.draw(batch, "RPM: " + rpm, posX, posY - 25f);
        fuente.draw(batch, "MARCHA: " + marchaStr + embragueStr, posX, posY - 50f);
    }

    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    public void dispose() {
        if (fuente != null) fuente.dispose();
    }
}
