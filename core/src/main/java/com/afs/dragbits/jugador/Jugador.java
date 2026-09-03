package com.afs.dragbits.jugador;

import com.badlogic.gdx.Gdx;

public class Jugador {

    private int nivel;
    private long experienciaActual;
    private long experienciaSiguienteNivel;
    private long dinero;

    // Constructor por defecto (nuevo jugador)
    public Jugador() {
        this(1, 0, 0);
    }

    // Constructor parametrizado (cargar partidas o testing)
    public Jugador(int nivelInicial, long experienciaInicial, long dineroInicial) {
        this.nivel = Math.max(1, nivelInicial);
        this.experienciaActual = Math.max(0, experienciaInicial);
        this.dinero = Math.max(0, dineroInicial);
        this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
    }

    /** Calculo de XP requerida para pasar del nivel actual al siguiente */
    public long calcularExperienciaRequerida(int nivelActual) {
        return (long) (200 * Math.pow(nivelActual, 2));
    }

    /** Añade experiencia al jugador y revisa si sube de nivel */
    public boolean sumarExperiencia(long cantidad) {
        if (cantidad <= 0) return false;

        this.experienciaActual += cantidad;
        boolean subioDeNivel = false;

        // while por si la experiencia otorgada hace subir varios niveles de golpe
        while (this.experienciaActual >= this.experienciaSiguienteNivel) {
            this.experienciaActual -= this.experienciaSiguienteNivel;
            this.nivel++;
            this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
            subioDeNivel = true;

            Gdx.app.log("Jugador", "¡Felicidades! Subiste al nivel " + this.nivel);
        }

        return subioDeNivel;
    }

    // DINERO

    public void sumarDinero(long cantidad) {
        if (cantidad > 0) {
            this.dinero += cantidad;
        }
    }

    public boolean restarDinero(long cantidad) {
        if (cantidad > 0 && this.dinero >= cantidad) {
            this.dinero -= cantidad;
            return true; // Compra exitosa
        }
        return false; // Dinero insuficiente
    }

    // GETTERS Y SETTERS

    public int getNivel() { return nivel; }
    public long getExperienciaActual() { return experienciaActual; }
    public long getExperienciaSiguienteNivel() { return experienciaSiguienteNivel; }
    public long getDinero() { return dinero; }

    // Setters manuales
    public void setNivel(int nivel) {
        this.nivel = Math.max(1, nivel);
        this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
    }

    public void setExperienciaActual(long experienciaActual) {
        this.experienciaActual = Math.max(0, experienciaActual);
    }

    public void setDinero(long dinero) {
        this.dinero = Math.max(0, dinero);
    }
}
