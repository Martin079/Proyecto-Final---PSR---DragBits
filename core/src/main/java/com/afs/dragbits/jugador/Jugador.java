package com.afs.dragbits.jugador;

import com.afs.dragbits.menurivales.TipoCarrera;
import com.badlogic.gdx.Gdx;

public class Jugador {

    private int nivel;
    private long experienciaActual;
    private long experienciaSiguienteNivel;
    private long dinero;
    private int derrotadosLegales;
    private int derrotadosIlegales;

    public Jugador() {
        this(1, 0, 0);
    }

    public Jugador(int nivelInicial, long experienciaInicial, long dineroInicial) {
        this.nivel = Math.max(1, nivelInicial);
        this.experienciaActual = Math.max(0, experienciaInicial);
        this.dinero = Math.max(0, dineroInicial);
        this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
        this.derrotadosLegales = 0;
        this.derrotadosIlegales = 0;
    }


    public long calcularExperienciaRequerida(int nivelActual) {
        return (long) (200 * Math.pow(nivelActual, 2));
    }


    public boolean sumarExperiencia(long cantidad) {
        if (cantidad <= 0) return false;

        this.experienciaActual += cantidad;
        boolean subioDeNivel = false;

        //por si la experiencia otorgada hace subir varios niveles de golpe
        while (this.experienciaActual >= this.experienciaSiguienteNivel) {
            this.experienciaActual -= this.experienciaSiguienteNivel;
            this.nivel++;
            this.experienciaSiguienteNivel = calcularExperienciaRequerida(this.nivel);
            subioDeNivel = true;

            Gdx.app.log("Jugador", "¡Felicidades! Subiste al nivel " + this.nivel);
        }

        return subioDeNivel;
    }



    public void sumarDinero(long cantidad) {
        if (cantidad > 0) {
            this.dinero += cantidad;
        }
    }

    public boolean restarDinero(long cantidad) {
        if (cantidad > 0 && this.dinero >= cantidad) {
            this.dinero -= cantidad;
            return true;
        }
        return false;
    }

    public int getNivel() { return nivel; }
    public long getExperienciaActual() { return experienciaActual; }
    public long getExperienciaSiguienteNivel() { return experienciaSiguienteNivel; }
    public long getDinero() { return dinero; }


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

    public int getDerrotados(TipoCarrera tipo) {
        if (tipo == TipoCarrera.LEGAL) {
            return derrotadosLegales;
        } else {
            return derrotadosIlegales;
        }
    }

    public void setDerrotados(TipoCarrera tipo, int cantidad) {
        cantidad = Math.max(0, Math.min(5, cantidad));
        if (tipo == TipoCarrera.LEGAL) {
            this.derrotadosLegales = cantidad;
        } else {
            this.derrotadosIlegales = cantidad;
        }
    }

    public int getMaxRivalDesbloqueado(TipoCarrera tipo) {
        return Math.min(getDerrotados(tipo), 4);
    }

    public boolean esPrimeraVictoria(TipoCarrera tipo, int indice) {
        return indice == getDerrotados(tipo);
    }

    public void registrarVictoria(TipoCarrera tipo, int indice) {
        if (esPrimeraVictoria(tipo, indice)) {
            if (tipo == TipoCarrera.LEGAL) {
                derrotadosLegales++;
            } else {
                derrotadosIlegales++;
            }
        }
    }
}
