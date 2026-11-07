package com.afs.dragbits.autos;

public class AutoConfig {
    private final AutoJugadorTipo tipo;
    private final long costo;
    private final int nivelRequerido;
    private final float velocidadMaxima;
    private final float aceleracion;
    private final float traccion;
    private final float capacidadNitro;
    private final int cargasNitro;

    private AutoConfig(AutoJugadorTipo tipo, long costo, int nivelRequerido, float velocidadMaxima,
                      float aceleracion, float traccion, float capacidadNitro, int cargasNitro) {
        this.tipo = tipo;
        this.costo = costo;
        this.nivelRequerido = nivelRequerido;
        this.velocidadMaxima = velocidadMaxima;
        this.aceleracion = aceleracion;
        this.traccion = traccion;
        this.capacidadNitro = capacidadNitro;
        this.cargasNitro = cargasNitro;
    }

    public static AutoConfig gol() {
        return new AutoConfig(
            AutoJugadorTipo.GOL,
            0,
            1,
            160f,
            9.5f,
            0.6f,
            0f,
            0
        );
    }

    public static AutoConfig toyota() {
        return new AutoConfig(
            AutoJugadorTipo.TOYOTA,
            15000,
            7,
            205f,
            6.8f,
            0.75f,
            2f,
            1
        );
    }

    public static AutoConfig challenger() {
        return new AutoConfig(
            AutoJugadorTipo.CHALLENGER,
            45000,
            14,
            255f,
            4.5f,
            0.85f,
            3f,
            2
        );
    }

    public AutoJugadorTipo getTipo() {
        return tipo;
    }

    public long getCosto() {
        return costo;
    }

    public int getNivelRequerido() {
        return nivelRequerido;
    }

    public float getVelocidadMaxima() {
        return velocidadMaxima;
    }

    public float getAceleracion() {
        return aceleracion;
    }

    public float getTraccion() {
        return traccion;
    }

    public float getCapacidadNitro() {
        return capacidadNitro;
    }

    public int getCargasNitro() {
        return cargasNitro;
    }
}
