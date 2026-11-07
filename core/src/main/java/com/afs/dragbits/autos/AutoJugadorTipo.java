package com.afs.dragbits.autos;

public enum AutoJugadorTipo {
    GOL(0, "Gol", "sprites/Autos/Jugador/gol-sheet.png"),
    TOYOTA(1, "Toyota", "sprites/Autos/Jugador/toyota-sheet.png"),
    CHALLENGER(2, "Challenger", "sprites/Autos/Jugador/challenger-sheet.png");

    private final int id;
    private final String nombre;
    private final String rutaSprite;

    AutoJugadorTipo(int id, String nombre, String rutaSprite) {
        this.id = id;
        this.nombre = nombre;
        this.rutaSprite = rutaSprite;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaSprite() {
        return rutaSprite;
    }
}
