package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class ElementoHUD implements Disposable {

    protected final OrthographicCamera camaraHUD;

    public ElementoHUD(float anchoPantalla, float altoPantalla) {
        this.camaraHUD = new OrthographicCamera();
        this.camaraHUD.setToOrtho(false, anchoPantalla, altoPantalla);
    }

    /**
     * aplica la matriz de proyección de la camara del HUD al SpriteBatch
     * debe llamarse al inicio del metodo de renderizado/dibujo de cada elemento
     */
    protected void aplicarProyeccion(SpriteBatch batch) {
        batch.setProjectionMatrix(camaraHUD.combined);
    }

    /**
     * actualiza el viewport de la camara ortografica cuando cambia el tamaño de la pantalla
     */
    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    @Override
    public void dispose() {
        // Implementacion vacia por defecto para las subclases que no requieran liberar recursos.
    }
}
