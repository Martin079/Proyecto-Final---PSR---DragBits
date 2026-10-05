package com.afs.dragbits.jugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class RepositorioJugador {

    private static final String PREFS_NAME = "dragbits_save_data";
    private static final String KEY_NIVEL = "nivel";
    private static final String KEY_XP_ACTUAL = "experienciaActual";
    private static final String KEY_DINERO = "dinero";


    public void guardarProgreso(Jugador jugador) {
        if (jugador == null) return;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(KEY_NIVEL, jugador.getNivel());
        prefs.putLong(KEY_XP_ACTUAL, jugador.getExperienciaActual());
        prefs.putLong(KEY_DINERO, jugador.getDinero());
        prefs.flush(); // Fuerza la escritura física en archivo

        Gdx.app.log("RepositorioJugador", "Progreso guardado correctamente.");
    }


    public void cargarProgreso(Jugador jugador) {
        if (jugador == null) return;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (prefs.contains(KEY_NIVEL)) {
            int nivel = prefs.getInteger(KEY_NIVEL, 1);
            long xpActual = prefs.getLong(KEY_XP_ACTUAL, 0);
            long dinero = prefs.getLong(KEY_DINERO, 0);

            jugador.setNivel(nivel);
            jugador.setExperienciaActual(xpActual);
            jugador.setDinero(dinero);

            Gdx.app.log("RepositorioJugador", "Progreso cargado con exito.");
        }
    }


    public Jugador cargarJugador() {
        Jugador jugador = new Jugador();
        cargarProgreso(jugador);
        return jugador;
    }

    public void borrarProgreso() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
        Gdx.app.log("RepositorioJugador", "Progreso borrado correctamente.");
    }
}
