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
            180f,
            12.5f,
            0.7f,
            3f,
            3
        );
    }

    public static AutoConfig toyota() {
        return new AutoConfig(
            AutoJugadorTipo.TOYOTA,
            5000,
            3,
            220f,
            9.5f,
            0.8f,
            4f,
            5
        );
    }

    public static AutoConfig challenger() {
        return new AutoConfig(
            AutoJugadorTipo.CHALLENGER,
            15000,
            5,
            280f,
            6.5f,
            0.9f,
            5f,
            7
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
