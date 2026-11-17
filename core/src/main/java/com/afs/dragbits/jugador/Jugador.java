package com.afs.dragbits.jugador;

import com.afs.dragbits.autos.AutoConfig;
import com.afs.dragbits.autos.AutoJugadorTipo;
import com.afs.dragbits.menurivales.TipoCarrera;
import com.afs.dragbits.mejoras.MejoraConfig;
import com.afs.dragbits.mejoras.TipoMejora;
import com.badlogic.gdx.Gdx;

import java.util.EnumMap;
import java.util.EnumSet;

public class Jugador {

    private int nivel;
    private long experienciaActual;
    private long experienciaSiguienteNivel;
    private long dinero;
    private int derrotadosLegales;
    private int derrotadosIlegales;
    private AutoJugadorTipo autoActual;
    private EnumSet<AutoJugadorTipo> autosComprados;
    private EnumMap<TipoMejora, Integer> mejorasCompradas;

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
        this.autoActual = AutoJugadorTipo.GOL;
        this.autosComprados = EnumSet.of(AutoJugadorTipo.GOL);
        this.mejorasCompradas = new EnumMap<>(TipoMejora.class);
        for (TipoMejora tipo : TipoMejora.values()) {
            mejorasCompradas.put(tipo, 0);
        }
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

    public boolean tieneAuto(AutoJugadorTipo tipo) {
        return autosComprados.contains(tipo);
    }

    public boolean comprarAuto(AutoJugadorTipo tipo) {
        AutoConfig config = obtenerConfig(tipo);
        if (config == null) return false;
        if (tieneAuto(tipo)) return false;
        if (nivel < config.getNivelRequerido()) {
            Gdx.app.log("Jugador", "Nivel insuficiente para comprar " + tipo.getNombre());
            return false;
        }
        if (!restarDinero(config.getCosto())) {
            Gdx.app.log("Jugador", "Dinero insuficiente para comprar " + tipo.getNombre());
            return false;
        }
        autosComprados.add(tipo);
        autoActual = tipo;
        Gdx.app.log("Jugador", "Auto comprado: " + tipo.getNombre());
        return true;
    }

    public boolean equiparAuto(AutoJugadorTipo tipo) {
        if (!tieneAuto(tipo)) {
            Gdx.app.log("Jugador", "No posees el auto " + tipo.getNombre());
            return false;
        }
        autoActual = tipo;
        Gdx.app.log("Jugador", "Auto equipado: " + tipo.getNombre());
        return true;
    }

    public AutoConfig getConfigAutoActual() {
        return obtenerConfig(autoActual);
    }

    private AutoConfig obtenerConfig(AutoJugadorTipo tipo) {
        switch (tipo) {
            case GOL: return AutoConfig.gol();
            case TOYOTA: return AutoConfig.toyota();
            case CHALLENGER: return AutoConfig.challenger();
            default: return null;
        }
    }

    public AutoJugadorTipo getAutoActual() {
        return autoActual;
    }

    public void setAutoActual(AutoJugadorTipo autoActual) {
        this.autoActual = autoActual;
    }

    public EnumSet<AutoJugadorTipo> getAutosComprados() {
        return autosComprados;
    }

    public void setAutosComprados(EnumSet<AutoJugadorTipo> autosComprados) {
        this.autosComprados = autosComprados;
    }

    public EnumMap<TipoMejora, Integer> getMejorasCompradas() {
        return mejorasCompradas;
    }

    public void setMejorasCompradas(EnumMap<TipoMejora, Integer> mejorasCompradas) {
        this.mejorasCompradas = mejorasCompradas;
    }

    public int getNivelMejora(TipoMejora tipo) {
        return mejorasCompradas.getOrDefault(tipo, 0);
    }

    public boolean tieneMejora(TipoMejora tipo, int nivel) {
        return getNivelMejora(tipo) >= nivel;
    }

    private MejoraConfig obtenerConfigMejora(TipoMejora tipo, int nivel) {
        switch (tipo) {
            case ACELERACION:
                switch (nivel) {
                    case 1: return MejoraConfig.aceleracionI();
                    case 2: return MejoraConfig.aceleracionII();
                    case 3: return MejoraConfig.aceleracionIII();
                    case 4: return MejoraConfig.aceleracionIV();
                    case 5: return MejoraConfig.aceleracionV();
                    default: return null;
                }
            case VELOCIDAD_MAXIMA:
                switch (nivel) {
                    case 1: return MejoraConfig.velocidadMaximaI();
                    case 2: return MejoraConfig.velocidadMaximaII();
                    case 3: return MejoraConfig.velocidadMaximaIII();
                    case 4: return MejoraConfig.velocidadMaximaIV();
                    case 5: return MejoraConfig.velocidadMaximaV();
                    default: return null;
                }
            case TRACCION:
                switch (nivel) {
                    case 1: return MejoraConfig.traccionI();
                    case 2: return MejoraConfig.traccionII();
                    case 3: return MejoraConfig.traccionIII();
                    case 4: return MejoraConfig.traccionIV();
                    case 5: return MejoraConfig.traccionV();
                    default: return null;
                }
            case NITRO:
                switch (nivel) {
                    case 1: return MejoraConfig.nitroI();
                    case 2: return MejoraConfig.nitroII();
                    case 3: return MejoraConfig.nitroIII();
                    case 4: return MejoraConfig.nitroIV();
                    default: return null;
                }
            case POTENCIA_NITRO:
                switch (nivel) {
                    case 1: return MejoraConfig.potenciaNitroI();
                    case 2: return MejoraConfig.potenciaNitroII();
                    case 3: return MejoraConfig.potenciaNitroIII();
                    case 4: return MejoraConfig.potenciaNitroIV();
                    default: return null;
                }
            default: return null;
        }
    }

    public boolean puedeComprarMejora(TipoMejora tipo, int nivel) {
        MejoraConfig config = obtenerConfigMejora(tipo, nivel);
        if (config == null) return false;
        if (nivel < 1) return false;
        if (tieneMejora(tipo, nivel)) return false;
        if (nivel != getNivelMejora(tipo) + 1) return false;
        if (nivel < config.getNivelJugadorRequerido()) return false;
        if (dinero < config.getCosto()) return false;
        return true;
    }

    public boolean comprarMejora(TipoMejora tipo, int nivel) {
        if (!puedeComprarMejora(tipo, nivel)) return false;
        MejoraConfig config = obtenerConfigMejora(tipo, nivel);
        if (!restarDinero(config.getCosto())) return false;
        mejorasCompradas.put(tipo, nivel);
        Gdx.app.log("Jugador", "Mejora comprada: " + tipo.getNombreMostrar() + " nivel " + nivel);
        return true;
    }

    public AutoConfig aplicarMejorasAAuto(AutoConfig baseConfig) {
        if (baseConfig == null) return null;

        AutoConfig configModificado = baseConfig;

        int nivelAceleracion = getNivelMejora(TipoMejora.ACELERACION);
        if (nivelAceleracion > 0) {
            float mejoraTotal = 0f;
            for (int i = 1; i <= nivelAceleracion; i++) {
                MejoraConfig mc = obtenerConfigMejora(TipoMejora.ACELERACION, i);
                if (mc != null) mejoraTotal += mc.getValorMejora();
            }
            configModificado = configModificado.withAceleracion(baseConfig.getAceleracion() - mejoraTotal);
        }

        int nivelVelocidad = getNivelMejora(TipoMejora.VELOCIDAD_MAXIMA);
        if (nivelVelocidad > 0) {
            float mejoraTotal = 0f;
            for (int i = 1; i <= nivelVelocidad; i++) {
                MejoraConfig mc = obtenerConfigMejora(TipoMejora.VELOCIDAD_MAXIMA, i);
                if (mc != null) mejoraTotal += mc.getValorMejora();
            }
            configModificado = configModificado.withVelocidadMaxima(baseConfig.getVelocidadMaxima() + mejoraTotal);
        }

        int nivelTraccion = getNivelMejora(TipoMejora.TRACCION);
        if (nivelTraccion > 0) {
            float mejoraTotal = 0f;
            for (int i = 1; i <= nivelTraccion; i++) {
                MejoraConfig mc = obtenerConfigMejora(TipoMejora.TRACCION, i);
                if (mc != null) mejoraTotal += mc.getValorMejora();
            }
            configModificado = configModificado.withTraccion(baseConfig.getTraccion() + mejoraTotal);
        }

        int nivelNitro = getNivelMejora(TipoMejora.NITRO);
        if (nivelNitro > 0) {
            float mejoraTotal = 0f;
            for (int i = 1; i <= nivelNitro; i++) {
                MejoraConfig mc = obtenerConfigMejora(TipoMejora.NITRO, i);
                if (mc != null) mejoraTotal += mc.getValorMejora();
            }
            configModificado = configModificado.withCapacidadNitro(baseConfig.getCapacidadNitro() + mejoraTotal);
            if (nivelNitro >= 1) {
                configModificado = configModificado.withCargasNitro(baseConfig.getCargasNitro() + 1);
            }
        }

        int nivelPotenciaNitro = getNivelMejora(TipoMejora.POTENCIA_NITRO);
        if (nivelPotenciaNitro > 0) {
            float mejoraTotal = 1f;
            for (int i = 1; i <= nivelPotenciaNitro; i++) {
                MejoraConfig mc = obtenerConfigMejora(TipoMejora.POTENCIA_NITRO, i);
                if (mc != null) mejoraTotal += mc.getValorMejora();
            }
            configModificado = configModificado.withPotenciaNitro(baseConfig.getPotenciaNitro() * mejoraTotal);
        }

        return configModificado;
    }
}
