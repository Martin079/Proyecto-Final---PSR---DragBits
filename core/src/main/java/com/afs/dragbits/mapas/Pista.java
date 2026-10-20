package com.afs.dragbits.mapas;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Pista {
    void dibujar(SpriteBatch batch, float altoPantalla);
    float getPosicionLineaMeta();
    float getPosicionSpawnX();
    float getYJugador();
    float getYRival();
    void dispose();
}
