package com.afs.dragbits.rivales;

public class RivalConfig {
    private final String NOMBRE;
    private final String RUTA_SPRITE;
    private final float VELOCIDAD_MAXIMA;
    private final float ACELERACION;
    private final float TRACCION;
    private final float NITRO_SEGUNDOS;
    private final int RECOMPENSA_BASE;

    public RivalConfig(String nombre, String rutaSprite, float velocidadMaxima, float aceleracion, float traccion, float nitroSegundos, int recompensaBase) {
        this.NOMBRE = nombre;
        this.RUTA_SPRITE = rutaSprite;
        this.VELOCIDAD_MAXIMA = velocidadMaxima;
        this.ACELERACION = aceleracion;
        this.TRACCION = traccion;
        this.NITRO_SEGUNDOS = nitroSegundos;
        this.RECOMPENSA_BASE = recompensaBase;
    }

    public String getNombre() {
        return NOMBRE;
    }

    public String getRutaSprite() {
        return RUTA_SPRITE;
    }

    public float getVelocidadMaxima() {
        return VELOCIDAD_MAXIMA;
    }

    public float getAceleracion() {
        return ACELERACION;
    }

    public float getTraccion() {
        return TRACCION;
    }

    public float getNitroSegundos() {
        return NITRO_SEGUNDOS;
    }

    public int getRecompensaBase() {
        return RECOMPENSA_BASE;
    }
}
