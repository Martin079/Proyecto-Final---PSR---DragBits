package com.afs.dragbits.Jugador;

import com.badlogic.gdx.Gdx;

public class Jugador {

    private int nivel;
    private long experienciaActual;
    private long experienciaSiguienteNivel;
    private long dinero;

    public Jugador() {
        this.nivel = 1;
        this.experienciaActual = 0;
        this.dinero = 0;
        this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
    }

    /**calculo para pasar de nivel     */
    private long calcularExperienciaRequerida(int nivelActual) {
        // calculo: Experiencia Base (vivel 1 a 2), nivel actual del jugador, curva/dificultad.
        return (long) (200 * Math.pow(nivelActual, 2));
    }

    /** * Añade experiencia al jugador y revisa si sube de nivel. */
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

    // dinero

    public void sumarDinero(long cantidad) {
        if (cantidad > 0) {
            this.dinero += cantidad;
        }
    }

    public boolean restarDinero(long cantidad) {
        if (cantidad > 0 && this.dinero >= cantidad) {
            this.dinero -= cantidad;
            return true; // compra exitosa
        }
        return false; // dinero insuficiente
    }

    // getters y setters

    public int getNivel() { return nivel; }
    public long getExperienciaActual() { return experienciaActual; }
    public long getExperienciaSiguienteNivel() { return experienciaSiguienteNivel; }
    public long getDinero() { return dinero; }
}
