package com.afs.dragbits.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

//generacion de texturas de color solido mediante Pixmap.

public class TexturaSolidaFactory {

    private TexturaSolidaFactory() {
        // Constructor privado para evitar instanciacion
    }

    /**
     * crea una textura solida del tamaño especificado y del color dado.
     *
     * Ancho en pixeles.
     * Alto en pixeles.
     * Color de relleno.
     * return Texture generada.
     */

    public static Texture crearTextura(int ancho, int alto, Color color) {
        Pixmap pixmap = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture textura = new Texture(pixmap);
        pixmap.dispose();

        return textura;
    }

    /**
     * sobrecarga de conveniencia para crear una textura de 1x1 pixel.
     * ideal para fondos escalables en Scene2D / LibGDX.
     *
     * color Color de relleno.
     * return Texture de 1x1 generada.
     */
    public static Texture crearTextura(Color color) {
        return crearTextura(1, 1, color);
    }
}
