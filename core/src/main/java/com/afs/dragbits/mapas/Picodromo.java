package com.afs.dragbits.mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Picodromo implements Pista {

    private Texture spriteSheet;
    private TextureRegion regionLargada;
    private TextureRegion regionIntermedia;
    private TextureRegion regionMeta;

    private static final float ANCHO_SECCION = 1080f;
    private static final int CANTIDAD_INTERMEDIAS = 5;
    private final float X_META;

    public Picodromo() {
        spriteSheet = new Texture("sprites/Pistas/Pista-sheet.png");

        TextureRegion[][] regiones = TextureRegion.split(spriteSheet, 1080, 1080);
        regionLargada = regiones[0][0];
        regionIntermedia = regiones[0][1];
        regionMeta = regiones[0][2];

        this.X_META = (1 + CANTIDAD_INTERMEDIAS) * ANCHO_SECCION;
    }

    @Override
    public void dibujar(SpriteBatch batch, float altoPantalla) {
        float xActual = 0f;

        batch.draw(regionIntermedia, xActual - ANCHO_SECCION, 0, ANCHO_SECCION, altoPantalla);

        batch.draw(regionLargada, xActual, 0, ANCHO_SECCION, altoPantalla);
        xActual += ANCHO_SECCION;

        for (int i = 0; i < CANTIDAD_INTERMEDIAS; i++) {
            batch.draw(regionIntermedia, xActual, 0, ANCHO_SECCION, altoPantalla);
            xActual += ANCHO_SECCION;
        }

        batch.draw(regionMeta, xActual, 0, ANCHO_SECCION, altoPantalla);
        xActual += ANCHO_SECCION;

        batch.draw(regionIntermedia, xActual, 0, ANCHO_SECCION, altoPantalla);
    }


    @Override
    public float getPosicionLineaMeta() {
        return X_META + (ANCHO_SECCION / 2f);
    }


    @Override
    public float getPosicionSpawnX() {
        return ANCHO_SECCION * 0.45f;
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }

    @Override
    public float getYJugador() { return 199f; }

    @Override
    public float getYRival() { return 352f; }
}
