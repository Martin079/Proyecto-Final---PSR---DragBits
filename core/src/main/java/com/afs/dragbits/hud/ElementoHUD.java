package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class ElementoHUD implements Disposable {

    protected final OrthographicCamera CAMARA_HUD;

    public ElementoHUD(float anchoPantalla, float altoPantalla) {
        this.CAMARA_HUD = new OrthographicCamera();
        this.CAMARA_HUD.setToOrtho(false, anchoPantalla, altoPantalla);
    }

    protected void aplicarProyeccion(SpriteBatch batch) {
        batch.setProjectionMatrix(CAMARA_HUD.combined);
    }

    public void resize(float ancho, float alto) {
        CAMARA_HUD.setToOrtho(false, ancho, alto);
    }

    @Override
    public void dispose() {
    }
}
