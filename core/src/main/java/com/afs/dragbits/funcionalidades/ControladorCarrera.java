package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.autos.AutoRival;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.mapas.Picodromo;

/**
 * gestiona el estado de la carrera:
 * deteccion de cruce de meta, determinacion del ganador y recompensas.
 */
public class ControladorCarrera {

    private final Picodromo picodromo;
    private final AutoJugador autoJugador;
    private final AutoRival autoRival;
    private final Jugador datosJugador;
    private final RepositorioJugador repositorioJugador;

    private boolean carreraFinalizada;
    private boolean jugadorGano;
    private boolean recompensaOtorgada;

    public ControladorCarrera(Picodromo picodromo, AutoJugador autoJugador, AutoRival autoRival, Jugador datosJugador) {
        this.picodromo = picodromo;
        this.autoJugador = autoJugador;
        this.autoRival = autoRival;
        this.datosJugador = datosJugador;
        this.repositorioJugador = new RepositorioJugador();

        this.carreraFinalizada = false;
        this.jugadorGano = false;
        this.recompensaOtorgada = false;
    }

    /**
     * Evalua las condiciones de fin de carrera y gestiona la recompensa.
     */
    public void actualizar() {
        if (carreraFinalizada) return;

        float metaX = picodromo.getPosicionLineaMeta();
        boolean jugadorCruzo = autoJugador.getFrenteX() >= metaX;
        boolean botCruzo = autoRival.getFrenteX() >= metaX;

        if (jugadorCruzo || botCruzo) {
            carreraFinalizada = true;
            jugadorGano = autoJugador.getFrenteX() >= autoRival.getFrenteX();

            if (jugadorGano && !recompensaOtorgada) {
                datosJugador.sumarDinero(autoRival.getRecompensa());
                repositorioJugador.guardarProgreso(datosJugador);
                recompensaOtorgada = true;
            }
        }
    }

    public boolean isCarreraFinalizada() {
        return carreraFinalizada;
    }

    public boolean isJugadorGano() {
        return jugadorGano;
    }
}
