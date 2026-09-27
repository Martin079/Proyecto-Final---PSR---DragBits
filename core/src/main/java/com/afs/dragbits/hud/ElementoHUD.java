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

    protected void aplicarProyeccion(SpriteBatch batch) {
        batch.setProjectionMatrix(camaraHUD.combined);
    }

    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    @Override
    public void dispose() {
    }
}
