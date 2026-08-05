package com.afs.dragbits.mapas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Picodromo {

    private Texture texturaFondo;
    private Texture texturaLinea;

    public Picodromo() {
        // Fondo gris oscuro
        Pixmap pixmapFondo = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapFondo.setColor(Color.valueOf("262626"));
        pixmapFondo.fill();
        texturaFondo = new Texture(pixmapFondo);
        pixmapFondo.dispose();

        // Línea vertical blanca
        Pixmap pixmapLinea = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapLinea.setColor(Color.WHITE);
        pixmapLinea.fill();
        texturaLinea = new Texture(pixmapLinea);
        pixmapLinea.dispose();
    }

    /**
     * Dibuja la pista y las líneas divisorias.
     * Genera líneas desde la izquierda de la pantalla hasta más adelante en la pista.
     */
    public void dibujar(SpriteBatch batch, float ancho, float alto) {
        // Dibuja el fondo abarcando un tramo largo (ej. 20,000 px de largo)
        batch.draw(texturaFondo, 0, 0, 20000f, alto);

        // Dibuja líneas verticales cada 150 píxeles para referencia visual de movimiento
        float anchoLinea = 8f;
        float paso = 150f;
        for (float x = 0; x < 20000f; x += paso) {
            batch.draw(texturaLinea, x, 0, anchoLinea, alto);
        }
    }

    public void dispose() {
        if (texturaFondo != null) texturaFondo.dispose();
        if (texturaLinea != null) texturaLinea.dispose();
    }
}
