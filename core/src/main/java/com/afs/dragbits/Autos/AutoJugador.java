package com.afs.dragbits.autos;

/**auto controlado por el jugador, carga el sprite sheet auto 1-sheet.png */
public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY) {
        // (posX, posY, velocidadMaxima, aceleracion, rutaSpriteSheet)
        super(posX, posY, 400f, 150f, "sprites/Autos/auto 1-sheet.png");
    }
}
