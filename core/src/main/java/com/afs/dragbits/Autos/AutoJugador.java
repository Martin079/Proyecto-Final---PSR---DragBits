package com.afs.dragbits.autos;

/** auto controlado por el jugador, carga el sprite sheet auto 1-sheet.png */
public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY) {
        // (posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet)
        // velocidadMaxima = 180 km/h limite base, aceleracion = 85, traccion = 80
        super(posX, posY, 200f, 220f, 200f, "Sprites/Autos/auto 1-sheet.png");
    }
}
