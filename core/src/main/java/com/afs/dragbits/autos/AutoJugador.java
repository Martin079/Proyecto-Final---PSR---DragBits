package com.afs.dragbits.autos;

/** auto controlado por el jugador */
public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY) {
        // (posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet)
        super(posX, posY, 160f, 80f, 75f, "sprites/Autos/gol-sheet.png");
    }
}



