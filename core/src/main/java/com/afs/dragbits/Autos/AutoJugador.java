package com.afs.dragbits.autos;

/** auto controlado por el jugador, carga el sprite sheet auto 1-sheet.png */
public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY) {
        // (posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet)
        super(posX, posY, 160f, 80f, 75f, "Sprites/Autos/auto 1-sheet.png");
    }
}
