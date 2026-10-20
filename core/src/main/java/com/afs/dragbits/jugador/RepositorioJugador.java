package com.afs.dragbits.jugador;

import com.afs.dragbits.menurivales.TipoCarrera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class RepositorioJugador {

    private static final String PREFS_NAME = "dragbits_save_data";
    private static final String KEY_NIVEL = "nivel";
    private static final String KEY_XP_ACTUAL = "experienciaActual";
    private static final String KEY_DINERO = "dinero";
    private static final String KEY_DERROTADOS_LEGALES = "derrotadosLegales";
    private static final String KEY_DERROTADOS_ILEGALES = "derrotadosIlegales";


    public void guardarProgreso(Jugador jugador) {
        if (jugador == null) return;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(KEY_NIVEL, jugador.getNivel());
        prefs.putLong(KEY_XP_ACTUAL, jugador.getExperienciaActual());
        prefs.putLong(KEY_DINERO, jugador.getDinero());
        prefs.putInteger(KEY_DERROTADOS_LEGALES, jugador.getDerrotados(TipoCarrera.LEGAL));
        prefs.putInteger(KEY_DERROTADOS_ILEGALES, jugador.getDerrotados(TipoCarrera.ILEGAL));
        prefs.flush();

        Gdx.app.log("RepositorioJugador", "Progreso guardado correctamente.");
    }


    public void cargarProgreso(Jugador jugador) {
        if (jugador == null) return;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (prefs.contains(KEY_NIVEL)) {
            int nivel = prefs.getInteger(KEY_NIVEL, 1);
            long xpActual = prefs.getLong(KEY_XP_ACTUAL, 0);
            long dinero = prefs.getLong(KEY_DINERO, 0);
            int derrotadosLegales = prefs.getInteger(KEY_DERROTADOS_LEGALES, 0);
            int derrotadosIlegales = prefs.getInteger(KEY_DERROTADOS_ILEGALES, 0);

            jugador.setNivel(nivel);
            jugador.setExperienciaActual(xpActual);
            jugador.setDinero(dinero);
            jugador.setDerrotados(TipoCarrera.LEGAL, derrotadosLegales);
            jugador.setDerrotados(TipoCarrera.ILEGAL, derrotadosIlegales);

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
