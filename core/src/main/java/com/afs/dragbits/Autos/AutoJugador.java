package com.afs.dragbits.autos;

import com.badlogic.gdx.graphics.Color;

/**
 * Representa el auto controlado por el jugador.
 */
public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY) {
        // (posX, posY, velocidadMaxima, aceleracion, Color)
        super(posX, posY, 400f, 150f, Color.RED); // Auto del jugador de color Rojo
    }
}
