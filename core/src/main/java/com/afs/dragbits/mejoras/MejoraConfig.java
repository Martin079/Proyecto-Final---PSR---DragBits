package com.afs.dragbits.mejoras;

public class MejoraConfig {
    private final TipoMejora tipo;
    private final int nivel;
    private final int nivelJugadorRequerido;
    private final long costo;
    private final float valorMejora;
    private final String descripcion;

    private MejoraConfig(TipoMejora tipo, int nivel, int nivelJugadorRequerido, long costo, float valorMejora, String descripcion) {
        this.tipo = tipo;
        this.nivel = nivel;
        this.nivelJugadorRequerido = nivelJugadorRequerido;
        this.costo = costo;
        this.valorMejora = valorMejora;
        this.descripcion = descripcion;
    }

    // Aceleración
    public static MejoraConfig aceleracionI() {
        return new MejoraConfig(
            TipoMejora.ACELERACION,
            1,
            2,
            800,
            -0.15f,
            "-0.15s 0-100"
        );
    }

    public static MejoraConfig aceleracionII() {
        return new MejoraConfig(
            TipoMejora.ACELERACION,
            2,
            6,
            2000,
            -0.20f,
            "-0.20s 0-100"
        );
    }

    public static MejoraConfig aceleracionIII() {
        return new MejoraConfig(
            TipoMejora.ACELERACION,
            3,
            10,
            4000,
            -0.25f,
            "-0.25s 0-100"
        );
    }

    public static MejoraConfig aceleracionIV() {
        return new MejoraConfig(
            TipoMejora.ACELERACION,
            4,
            14,
            7000,
            -0.30f,
            "-0.30s 0-100"
        );
    }

    public static MejoraConfig aceleracionV() {
        return new MejoraConfig(
            TipoMejora.ACELERACION,
            5,
            18,
            12000,
            -0.35f,
            "-0.35s 0-100"
        );
    }

    // Tracción
    public static MejoraConfig traccionI() {
        return new MejoraConfig(
            TipoMejora.TRACCION,
            1,
            2,
            800,
            -0.10f,
            "-10% patinaje"
        );
    }

    public static MejoraConfig traccionII() {
        return new MejoraConfig(
            TipoMejora.TRACCION,
            2,
            7,
            2200,
            -0.15f,
            "-15% patinaje"
        );
    }

    public static MejoraConfig traccionIII() {
        return new MejoraConfig(
            TipoMejora.TRACCION,
            3,
            12,
            4500,
            -0.20f,
            "-20% patinaje"
        );
    }

    public static MejoraConfig traccionIV() {
        return new MejoraConfig(
            TipoMejora.TRACCION,
            4,
            16,
            8500,
            -0.25f,
            "-25% patinaje"
        );
    }

    public static MejoraConfig traccionV() {
        return new MejoraConfig(
            TipoMejora.TRACCION,
            5,
            20,
            16000,
            -0.30f,
            "Patinaje casi nulo"
        );
    }

    // Velocidad Máxima
    public static MejoraConfig velocidadMaximaI() {
        return new MejoraConfig(
            TipoMejora.VELOCIDAD_MAXIMA,
            1,
            3,
            1000,
            3.0f,
            "+3 km/h"
        );
    }

    public static MejoraConfig velocidadMaximaII() {
        return new MejoraConfig(
            TipoMejora.VELOCIDAD_MAXIMA,
            2,
            8,
            2500,
            4.0f,
            "+4 km/h"
        );
    }

    public static MejoraConfig velocidadMaximaIII() {
        return new MejoraConfig(
            TipoMejora.VELOCIDAD_MAXIMA,
            3,
            12,
            4500,
            5.0f,
            "+5 km/h"
        );
    }

    public static MejoraConfig velocidadMaximaIV() {
        return new MejoraConfig(
            TipoMejora.VELOCIDAD_MAXIMA,
            4,
            16,
            8500,
            6.0f,
            "+6 km/h"
        );
    }

    public static MejoraConfig velocidadMaximaV() {
        return new MejoraConfig(
            TipoMejora.VELOCIDAD_MAXIMA,
            5,
            20,
            16000,
            8.0f,
            "+8 km/h (Élite)"
        );
    }

    // Nitro (capacidad/duración)
    public static MejoraConfig nitroI() {
        return new MejoraConfig(
            TipoMejora.NITRO,
            1,
            5,
            2500,
            2.0f,
            "1 carga, 2s"
        );
    }

    public static MejoraConfig nitroII() {
        return new MejoraConfig(
            TipoMejora.NITRO,
            2,
            8,
            3000,
            1.0f,
            "+1s duración"
        );
    }

    public static MejoraConfig nitroIII() {
        return new MejoraConfig(
            TipoMejora.NITRO,
            3,
            13,
            5500,
            1.5f,
            "+1.5s duración"
        );
    }

    public static MejoraConfig nitroIV() {
        return new MejoraConfig(
            TipoMejora.NITRO,
            4,
            18,
            11000,
            2.0f,
            "Carga máx +2s"
        );
    }

    // Potencia del Nitro
    public static MejoraConfig potenciaNitroI() {
        return new MejoraConfig(
            TipoMejora.POTENCIA_NITRO,
            1,
            6,
            2000,
            0.05f,
            "+5% empuje"
        );
    }

    public static MejoraConfig potenciaNitroII() {
        return new MejoraConfig(
            TipoMejora.POTENCIA_NITRO,
            2,
            10,
            3800,
            0.08f,
            "+8% empuje"
        );
    }

    public static MejoraConfig potenciaNitroIII() {
        return new MejoraConfig(
            TipoMejora.POTENCIA_NITRO,
            3,
            15,
            7500,
            0.10f,
            "+10% empuje"
        );
    }

    public static MejoraConfig potenciaNitroIV() {
        return new MejoraConfig(
            TipoMejora.POTENCIA_NITRO,
            4,
            19,
            13000,
            0.12f,
            "+12% empuje"
        );
    }

    // Getters
    public TipoMejora getTipo() {
        return tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getNivelJugadorRequerido() {
        return nivelJugadorRequerido;
    }

    public long getCosto() {
        return costo;
    }

    public float getValorMejora() {
        return valorMejora;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
