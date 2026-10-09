package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.AutoJugador;
import com.afs.dragbits.autos.AutoRival;
import com.afs.dragbits.jugador.Jugador;
import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.mapas.Pista;
import com.afs.dragbits.menurivales.TipoCarrera;

public class ControladorCarrera {

    private final Pista PISTA;
    private final AutoJugador AUTO_JUGADOR;
    private final AutoRival AUTO_RIVAL;
    private final Jugador DATOS_JUGADOR;
    private final RepositorioJugador REPOSITORIO_JUGADOR;
    private final TipoCarrera TIPO_CARRERA;
    private final int INDICE_RIVAL;

    private boolean carreraFinalizada;
    private boolean jugadorGano;
    private boolean recompensaOtorgada;
    private int recompensaObtenida;
    private boolean primeraVictoria;
    private boolean subioDeNivel;

    public ControladorCarrera(Pista pista, AutoJugador autoJugador, AutoRival autoRival, Jugador datosJugador, TipoCarrera tipoCarrera, int indiceRival) {
        this.PISTA = pista;
        this.AUTO_JUGADOR = autoJugador;
        this.AUTO_RIVAL = autoRival;
        this.DATOS_JUGADOR = datosJugador;
        this.REPOSITORIO_JUGADOR = new RepositorioJugador();
        this.TIPO_CARRERA = tipoCarrera;
        this.INDICE_RIVAL = indiceRival;

        this.carreraFinalizada = false;
        this.jugadorGano = false;
        this.recompensaOtorgada = false;
        this.recompensaObtenida = 0;
        this.primeraVictoria = false;
        this.subioDeNivel = false;
    }

    public void actualizar() {
        if (carreraFinalizada) return;

        float metaX = PISTA.getPosicionLineaMeta();
        boolean jugadorCruzo = AUTO_JUGADOR.getFrenteX() >= metaX;
        boolean botCruzo = AUTO_RIVAL.getFrenteX() >= metaX;

        if (jugadorCruzo || botCruzo) {
            carreraFinalizada = true;
            jugadorGano = AUTO_JUGADOR.getFrenteX() >= AUTO_RIVAL.getFrenteX();

            if (jugadorGano && !recompensaOtorgada) {
                primeraVictoria = DATOS_JUGADOR.esPrimeraVictoria(TIPO_CARRERA, INDICE_RIVAL);
                int base = AUTO_RIVAL.getRecompensa();
                recompensaObtenida = primeraVictoria ? Math.round(base * 1.5f) : base;
                
                if (TIPO_CARRERA == TipoCarrera.LEGAL) {
                    DATOS_JUGADOR.sumarDinero(recompensaObtenida);
                } else {
                    subioDeNivel = DATOS_JUGADOR.sumarExperiencia(recompensaObtenida);
                }
                
                DATOS_JUGADOR.registrarVictoria(TIPO_CARRERA, INDICE_RIVAL);
                REPOSITORIO_JUGADOR.guardarProgreso(DATOS_JUGADOR);
                recompensaOtorgada = true;
            } else if (!jugadorGano) {
                recompensaObtenida = 0;
            }
        }
    }

    public boolean isCarreraFinalizada() {
        return carreraFinalizada;
    }

    public boolean isJugadorGano() {
        return jugadorGano;
    }

    public int getRecompensaObtenida() {
        return recompensaObtenida;
    }

    public boolean isPrimeraVictoria() {
        return primeraVictoria;
    }

    public boolean isSubioDeNivel() {
        return subioDeNivel;
    }
}
