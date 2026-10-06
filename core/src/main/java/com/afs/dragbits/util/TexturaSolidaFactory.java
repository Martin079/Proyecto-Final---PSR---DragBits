package com.afs.dragbits.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TexturaSolidaFactory {

    private TexturaSolidaFactory() {

    }

    public static Texture crearTextura(int ancho, int alto, Color color) {
        Pixmap pixmap = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture textura = new Texture(pixmap);
        pixmap.dispose();

        return textura;
    }

    public static Texture crearTextura(Color color) {
        return crearTextura(1, 1, color);
    }
}
