package com.afs.dragbits.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Jugador {

    private static final String PREFS_NAME = "dragbits_save_data";

    private int nivel;
    private long experienciaActual;
    private long experienciaSiguienteNivel;
    private long dinero;

    // constructor por defecto (nuevo jugador)
    public Jugador() {
        this(1, 0, 0);
    }

    // constructor parametrizado (cargar partidas)
    public Jugador(int nivelInicial, long experienciaInicial, long dineroInicial) {
        this.nivel = Math.max(1, nivelInicial);
        this.experienciaActual = Math.max(0, experienciaInicial);
        this.dinero = Math.max(0, dineroInicial);
        this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
    }

    /** calculo de XP requerida para pasar del nivel actual al siguiente */
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

    //GUARDADO Y CARGA

    /** guardar estado actual del jugador en disco */
    public void guardarProgreso() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger("nivel", this.nivel);
        prefs.putLong("experienciaActual", this.experienciaActual);
        prefs.putLong("dinero", this.dinero);
        prefs.flush(); // Fuerza la escritura física en archivo
        Gdx.app.log("Jugador", "Progreso guardado correctamente.");
    }

    /** cargar estado guardado previamente.*/
    public void cargarProgreso() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (prefs.contains("nivel")) {
            this.nivel = prefs.getInteger("nivel", 1);
            this.experienciaActual = prefs.getLong("experienciaActual", 0);
            this.dinero = prefs.getLong("dinero", 0);
            this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
            Gdx.app.log("Jugador", "Progreso cargado con éxito.");
        }
    }

    /** Borra el archivo de guardado */
    public void borrarProgreso() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
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
