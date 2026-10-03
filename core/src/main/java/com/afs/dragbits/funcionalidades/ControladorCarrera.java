package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.autos.AutoRival;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.mapas.Picodromo;

public class ControladorCarrera {

    private final Picodromo PICODROMO;
    private final AutoJugador AUTO_JUGADOR;
    private final AutoRival AUTO_RIVAL;
    private final Jugador DATOS_JUGADOR;
    private final RepositorioJugador REPOSITORIO_JUGADOR;

    private boolean carreraFinalizada;
    private boolean jugadorGano;
    private boolean recompensaOtorgada;

    public ControladorCarrera(Picodromo picodromo, AutoJugador autoJugador, AutoRival autoRival, Jugador datosJugador) {
        this.PICODROMO = picodromo;
        this.AUTO_JUGADOR = autoJugador;
        this.AUTO_RIVAL = autoRival;
        this.DATOS_JUGADOR = datosJugador;
        this.REPOSITORIO_JUGADOR = new RepositorioJugador();

        this.carreraFinalizada = false;
        this.jugadorGano = false;
        this.recompensaOtorgada = false;
    }

    public void actualizar() {
        if (carreraFinalizada) return;

        float metaX = PICODROMO.getPosicionLineaMeta();
        boolean jugadorCruzo = AUTO_JUGADOR.getFrenteX() >= metaX;
        boolean botCruzo = AUTO_RIVAL.getFrenteX() >= metaX;

        if (jugadorCruzo || botCruzo) {
            carreraFinalizada = true;
            jugadorGano = AUTO_JUGADOR.getFrenteX() >= AUTO_RIVAL.getFrenteX();

            if (jugadorGano && !recompensaOtorgada) {
                DATOS_JUGADOR.sumarDinero(AUTO_RIVAL.getRecompensa());
                REPOSITORIO_JUGADOR.guardarProgreso(DATOS_JUGADOR);
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
