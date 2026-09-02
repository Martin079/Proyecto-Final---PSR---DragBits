package com.afs.dragbits.ciudad;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Burbuja {

    public interface AccionBurbuja {
        void ejecutar();
    }

    private float x, y, ancho, alto;
    private TextureRegion sprite;
    private Rectangle bounds;
    private AccionBurbuja accion;

    public Burbuja(float x, float y, float ancho, float alto, TextureRegion sprite, AccionBurbuja accion) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.sprite = sprite;
        this.accion = accion;
        this.bounds = new Rectangle(x, y, ancho, alto);
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(sprite, x, y, ancho, alto);
    }

    public boolean verificarClic(float toqueX, float toqueY) {
        if (bounds.contains(toqueX, toqueY)) {
            if (accion != null) accion.ejecutar();
            return true;
        }
        return false;
    }
}
