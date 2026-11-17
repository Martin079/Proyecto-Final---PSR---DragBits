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
    private final float potenciaNitro;

    private AutoConfig(AutoJugadorTipo tipo, long costo, int nivelRequerido, float velocidadMaxima,
                      float aceleracion, float traccion, float capacidadNitro, int cargasNitro, float potenciaNitro) {
        this.tipo = tipo;
        this.costo = costo;
        this.nivelRequerido = nivelRequerido;
        this.velocidadMaxima = velocidadMaxima;
        this.aceleracion = aceleracion;
        this.traccion = traccion;
        this.capacidadNitro = capacidadNitro;
        this.cargasNitro = cargasNitro;
        this.potenciaNitro = potenciaNitro;
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
            0,
            1f
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
            1,
            1f
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
            2,
            1f
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

    public float getPotenciaNitro() {
        return potenciaNitro;
    }

    public AutoConfig withVelocidadMaxima(float velocidadMaxima) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }

    public AutoConfig withAceleracion(float aceleracion) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }

    public AutoConfig withTraccion(float traccion) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }

    public AutoConfig withCapacidadNitro(float capacidadNitro) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }

    public AutoConfig withCargasNitro(int cargasNitro) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }

    public AutoConfig withPotenciaNitro(float potenciaNitro) {
        return new AutoConfig(tipo, costo, nivelRequerido, velocidadMaxima, aceleracion, traccion, capacidadNitro, cargasNitro, potenciaNitro);
    }
}
