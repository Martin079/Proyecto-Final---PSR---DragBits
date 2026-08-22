package com.afs.dragbits.autos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class Auto {

    public enum EstadoAuto {
        ESTATICO,
        AVANZANDO,
        CAMBIANDO_MARCHA,
        NITRO
    }

    protected float posX;
    protected float posY;
    protected float ancho = 200f;
    protected float alto = 80f;

    // estadisticas del auto
    protected float velocidad;
    protected float velocidadMaxima;
    protected float aceleracion;       // fuerza del motor
    protected float traccion;          // evita que las ruedas patinen en marchas bajas o arranque
    protected float potenciaNitro;     // multiplicador extra de aceleracion
    protected float capacidadNitro;    // tiempo util de nitro en segundos (ej. 3.0s)
    protected float nitroRestante;
    protected float zonaSincronizacion;// ampliacion de la zona de cambio de marcha

    protected float rpm;
    protected float rpmMaximas;

    // Cambios y nitro
    protected int marchaActual; // 0 = Neutral, 1..5
    protected boolean embraguePresionado;
    protected boolean nitroActivo;
    // relaciones ajustadas para llegar al 100% de la velocidad maxima
    protected float[] relacionesTransmision = {0f, 0.30f, 0.50f, 0.70f, 0.85f, 1.0f};

    // indicadores físicos
    protected boolean patinando;

    // sprites y animaciones
    private Texture spriteSheet;
    private TextureRegion frameEstatico;
    private TextureRegion frameAvance;
    private Animation<TextureRegion> animCambio;
    private Animation<TextureRegion> animNitro;

    private float stateTime;
    private Texture texturaFallback;

    // multiplicador visual para desplazar mas pixeles en pantalla por cada km/h
    // (no afecta la velocidad real ni el HUD, solo que tan rapido se ve avanzar el auto)
    private static final float FACTOR_MOVIMIENTO = 4.2f;

    // umbral de velocidad por debajo del cual se aplica un impulso de arranque,
    // simulando el mayor torque disponible en bajas revoluciones
    private static final float UMBRAL_ARRANQUE = 25f;
    private static final float FACTOR_ARRANQUE = 1.25f;

    // margen minimo de aceleracion que se conserva al acercarse al techo de la marcha,
    // para evitar que el auto "choque contra una pared" invisible al llegar al limite
    private static final float PISO_CURVA_ACELERACION = 0.4f;

    // relacion potencia/traccion a partir de la cual el auto empieza a patinar
    private static final float UMBRAL_PATINAJE = 1.2f;
    private static final float PERDIDA_MIN_PATINAJE = 0.45f;
    private static final float PERDIDA_MAX_PATINAJE = 0.85f;

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, String rutaSpriteSheet) {
        inicializarAtributos(posX, posY, velocidadMaxima, aceleracion, traccion);
        cargarSpriteSheet(rutaSpriteSheet);
    }

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, Color colorFallback) {
        inicializarAtributos(posX, posY, velocidadMaxima, aceleracion, traccion);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colorFallback);
        pixmap.fill();
        this.texturaFallback = new Texture(pixmap);
        pixmap.dispose();
    }

    private void inicializarAtributos(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion) {
        this.posX = posX;
        this.posY = posY;
        this.velocidadMaxima = velocidadMaxima;
        this.aceleracion = aceleracion;
        this.traccion = traccion;

        // valores base predeterminados de Nitro y Sincronización
        this.potenciaNitro = 1.5f;     // 50% más de aceleración con Nitro
        this.capacidadNitro = 3.0f;    // 3 segundos de carga
        this.nitroRestante = capacidadNitro;
        this.zonaSincronizacion = 400f; // 400 RPM de margen aceptable

        this.velocidad = 0f;
        this.rpm = 800f;
        this.rpmMaximas = 7000f;
        this.marchaActual = 0;
        this.embraguePresionado = false;
        this.nitroActivo = false;
        this.patinando = false;
        this.stateTime = 0f;
    }

    private void cargarSpriteSheet(String ruta) {
        spriteSheet = new Texture(Gdx.files.internal(ruta));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 100, 40);

        frameEstatico = tmp[0][0];
        frameAvance   = tmp[0][1];
        animCambio = new Animation<>(0.15f, tmp[0][2], tmp[0][3]);
        animNitro = new Animation<>(0.10f, tmp[0][4], tmp[0][5]);
    }

    public void acelerar(float delta) {
        if (!embraguePresionado && marchaActual > 0) {
            // aseguramos que el indice no se desborde
            int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];

            float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida);

            // impulso de arranque: mas respuesta al salir desde velocidades bajas,
            // simulando el pico de torque de un motor real en las primeras marchas
            if (velocidad < UMBRAL_ARRANQUE) {
                aceleracionEfectiva *= FACTOR_ARRANQUE;
            }

            // curva de acercamiento al techo de la marcha: la aceleracion se atenua
            // de forma progresiva a medida que la velocidad se acerca al limite,
            // en vez de cortarse en seco (se siente mas fluido y natural)
            if (velMaxMarcha > 0f) {
                float margen = MathUtils.clamp((velMaxMarcha - velocidad) / velMaxMarcha, PISO_CURVA_ACELERACION, 1f);
                aceleracionEfectiva *= margen;
            }

            // sistema nitro (base lista para expandir mas adelante)
            if (nitroActivo && nitroRestante > 0) {
                aceleracionEfectiva *= potenciaNitro;
                velMaxMarcha *= 1.15f;
                nitroRestante -= delta;
                if (nitroRestante <= 0) {
                    nitroRestante = 0;
                    nitroActivo = false;
                }
            }

            // aplicar velocidad
            if (velocidad < velMaxMarcha) {
                velocidad += aceleracionEfectiva * delta;
                if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
            }
        }

        // subida de RPM
        if (embraguePresionado || marchaActual == 0) {
            rpm += 5500f * delta;
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        } else {
            int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];

            if (velMaxMarcha > 0) {
                rpm = 800f + (velocidad / velMaxMarcha) * (rpmMaximas - 800f);
            }
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        }
    }

    /**
     * Calcula cuanto de la aceleracion del motor logra transmitirse realmente al piso,
     * segun la relacion entre la potencia (aceleracion) y el agarre disponible (traccion).
     * Solo aplica en marchas bajas (1 y 2), donde el patinaje es mas probable.
     * A diferencia de un corte fijo, la perdida crece de forma progresiva cuanto mas
     * se pasa la potencia del motor respecto a la traccion disponible.
     */
    private float calcularFactorTraccion(int marchaValida) {
        if (marchaValida > 2 || traccion <= 0f) {
            patinando = false;
            return 1f;
        }

        float relacionPotenciaAgarre = aceleracion / traccion;

        if (relacionPotenciaAgarre <= UMBRAL_PATINAJE) {
            patinando = false;
            return 1f;
        }

        patinando = true;
        float exceso = relacionPotenciaAgarre - UMBRAL_PATINAJE;
        // cuanto mayor el exceso de potencia sobre la traccion, mayor la perdida de agarre,
        // pero siempre dentro de un rango acotado para que nunca se sienta "trabado"
        return MathUtils.clamp(1f - exceso * 0.5f, PERDIDA_MIN_PATINAJE, PERDIDA_MAX_PATINAJE);
    }

    public void desacelerar(float delta) {
        if (velocidad > 0) {
            velocidad -= (aceleracion * 0.3f) * delta;
            if (velocidad < 0) velocidad = 0;
        }

        if (rpm > 800f) {
            rpm -= 3500f * delta;
            if (rpm < 800f) rpm = 800f;
        }
        patinando = false;
    }

    public void actualizar(float delta) {
        posX += (velocidad * FACTOR_MOVIMIENTO) * delta;
        stateTime += delta;
    }

    /** Evalua la sincronización del cambio */
    public boolean esCambioPerfecto() {
        return (rpm >= (rpmMaximas - zonaSincronizacion));
    }

    public EstadoAuto getEstadoActual() {
        if (nitroActivo && nitroRestante > 0) {
            return EstadoAuto.NITRO;
        } else if (embraguePresionado) {
            return EstadoAuto.CAMBIANDO_MARCHA;
        } else if (velocidad > 0) {
            return EstadoAuto.AVANZANDO;
        } else {
            return EstadoAuto.ESTATICO;
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (spriteSheet != null) {
            TextureRegion frameActual;

            switch (getEstadoActual()) {
                case NITRO:
                    frameActual = animNitro.getKeyFrame(stateTime, true);
                    break;
                case CAMBIANDO_MARCHA:
                    frameActual = animCambio.getKeyFrame(stateTime, true);
                    break;
                case AVANZANDO:
                    frameActual = frameAvance;
                    break;
                case ESTATICO:
                default:
                    frameActual = frameEstatico;
                    break;
            }

            batch.draw(frameActual, posX, posY, ancho, alto);
        } else if (texturaFallback != null) {
            batch.draw(texturaFallback, posX, posY, ancho, alto);
        }
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
        if (texturaFallback != null) texturaFallback.dispose();
    }

    // Getters y Setters
    public float getVelocidad() { return velocidad; }
    public float getRpm() { return rpm; }
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public int getMarchaActual() { return marchaActual; }
    public void setMarchaActual(int marcha) { this.marchaActual = marcha; }
    public boolean isEmbraguePresionado() { return embraguePresionado; }
    public void setEmbraguePresionado(boolean embrague) { this.embraguePresionado = embrague; }
    public boolean isNitroActivo() { return nitroActivo; }
    public void setNitroActivo(boolean nitroActivo) { this.nitroActivo = nitroActivo; }
    public void setPosX(float posX) { this.posX = posX; }
    public void setVelocidad(float velocidad) { this.velocidad = velocidad; }

    public float getTraccion() { return traccion; }
    public void setTraccion(float traccion) { this.traccion = traccion; }
    public float getAceleracion() { return aceleracion; }
    public void setAceleracion(float aceleracion) { this.aceleracion = aceleracion; }
    public float getVelocidadMaxima() { return velocidadMaxima; }
    public void setVelocidadMaxima(float velocidadMaxima) { this.velocidadMaxima = velocidadMaxima; }
    public float getNitroRestante() { return nitroRestante; }
    public boolean isPatinando() { return patinando; }
    public void setZonaSincronizacion(float zona) { this.zonaSincronizacion = zona; }
}
