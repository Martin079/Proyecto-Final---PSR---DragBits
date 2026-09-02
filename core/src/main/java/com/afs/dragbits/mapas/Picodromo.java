package com.afs.dragbits.mapas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Picodromo {

    private Texture spriteSheet;
    private TextureRegion regionLargada;
    private TextureRegion regionIntermedia;
    private TextureRegion regionMeta;

    private final float ANCHO_SECCION = 1080f;
    private final int CANTIDAD_INTERMEDIAS = 5; // Configura la longitud de la pista
    private final float xMeta;

    public Picodromo() {
        spriteSheet = new Texture("sprites/Pistas/Pista-sheet.png");

        // Dividir el sheet de 3x1 regiones de 1080x1080
        TextureRegion[][] regiones = TextureRegion.split(spriteSheet, 1080, 1080);
        regionLargada = regiones[0][0];
        regionIntermedia = regiones[0][1];
        regionMeta = regiones[0][2];

        // Posición X donde se dibuja el tile de la Meta
        this.xMeta = (1 + CANTIDAD_INTERMEDIAS) * ANCHO_SECCION;
    }

    public void dibujar(SpriteBatch batch, float altoPantalla) {
        float xActual = 0f;

        // 1. Dibujar Largada
        batch.draw(regionLargada, xActual, 0, ANCHO_SECCION, altoPantalla);
        xActual += ANCHO_SECCION;

        // 2. Dibujar Secciones Intermedias (4 o más)
        for (int i = 0; i < CANTIDAD_INTERMEDIAS; i++) {
            batch.draw(regionIntermedia, xActual, 0, ANCHO_SECCION, altoPantalla);
            xActual += ANCHO_SECCION;
        }

        // 3. Dibujar Meta
        batch.draw(regionMeta, xActual, 0, ANCHO_SECCION, altoPantalla);
    }

    /**
     * Calcula la posición global X de la línea de meta colocada en el centro del tile de meta.
     */
    public float getPosicionLineaMeta() {
        return xMeta + (ANCHO_SECCION / 2f);
    }

    /**
     * Posición de spawn inicial
     */
    public float getPosicionSpawnX() {
        return ANCHO_SECCION * 0.55f;
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
