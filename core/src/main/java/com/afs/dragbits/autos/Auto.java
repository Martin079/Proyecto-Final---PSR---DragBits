package com.afs.dragbits.autos;

import com.afs.dragbits.Main;
import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class Auto {

    protected float posX;
    protected float posY;
    protected float ancho = 280f;
    protected float alto = 112f;

    protected float velocidad;
    protected float velocidadMaxima;
    protected float aceleracion;
    protected float traccion;
    protected float potenciaNitro;
    protected float capacidadNitro;
    protected float nitroRestante;
    protected float zonaSincronizacion;

    protected float rpm;
    protected float rpmMaximas;


    protected int marchaActual;
    protected boolean embraguePresionado;
    protected boolean nitroActivo;
    protected float[] relacionesTransmision = {0f, 0.30f, 0.50f, 0.70f, 0.85f, 1.0f};

    protected boolean patinando;


    private Texture spriteSheet;
    private TextureRegion frameEstatico;
    private Animation<TextureRegion> animAvanzando;
    private Animation<TextureRegion> animCambio;
    private Animation<TextureRegion> animNitro;

    private float stateTime;
    private Texture texturaFallback;

    private Sound sonidoMotor;
    private long idMotor = -1;
    private boolean estaAcelerando;
    private Main mainGame;

    public static final float ESCALA_SPRITE = 2.8f;
    public static final float ANCHO_BASE = 280f;

    private static final float FACTOR_MOVIMIENTO = 4.2f;


    private static final float UMBRAL_ARRANQUE = 25f;
    private static final float FACTOR_ARRANQUE = 1.25f;
    private static final float PISO_CURVA_ACELERACION = 0.4f;

    private static final float UMBRAL_PATINAJE = 1.2f;
    private static final float PERDIDA_MIN_PATINAJE = 0.45f;
    private static final float PERDIDA_MAX_PATINAJE = 0.85f;

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, String rutaSpriteSheet, Main mainGame) {
        this.mainGame = mainGame;
        inicializarAtributos(posX, posY, velocidadMaxima, aceleracion, traccion);
        cargarSpriteSheet(rutaSpriteSheet);
    }

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, Color colorFallback, Main mainGame) {
        this.mainGame = mainGame;
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

        this.potenciaNitro = 1.5f;
        this.capacidadNitro = 3.0f;
        this.nitroRestante = capacidadNitro;
        this.zonaSincronizacion = 400f;

        this.velocidad = 0f;
        this.rpm = 800f;
        this.rpmMaximas = 7000f;
        this.marchaActual = 0;
        this.embraguePresionado = false;
        this.nitroActivo = false;
        this.patinando = false;
        this.stateTime = 0f;

        this.sonidoMotor = Gdx.audio.newSound(Gdx.files.internal("audio/Gameplay/motor_loop.wav"));
        this.estaAcelerando = false;
    }

    private void cargarSpriteSheet(String ruta) {
        spriteSheet = SpriteSheetLoader.cargarTextura(ruta);

        int anchoFrame = spriteSheet.getWidth() / 7;
        int altoFrame = spriteSheet.getHeight();

        this.ancho = anchoFrame * ESCALA_SPRITE;
        this.alto = altoFrame * ESCALA_SPRITE;

        TextureRegion[] frames = SpriteSheetLoader.recortar(spriteSheet, anchoFrame, altoFrame);

        frameEstatico = frames[0];

        animAvanzando = new Animation<>(0.12f, frames[1], frames[2]);

        animCambio = new Animation<>(0.15f, frames[3], frames[4]);

        animNitro = new Animation<>(0.10f, frames[5], frames[6]);
    }

    public void acelerar(float delta) {
        this.estaAcelerando = true;
        if (!embraguePresionado && marchaActual > 0) {
            int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];

            float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida);

            if (velocidad < UMBRAL_ARRANQUE) {
                aceleracionEfectiva *= FACTOR_ARRANQUE;
            }

            if (velMaxMarcha > 0f) {
                float margen = MathUtils.clamp((velMaxMarcha - velocidad) / velMaxMarcha, PISO_CURVA_ACELERACION, 1f);
                aceleracionEfectiva *= margen;
            }

            if (nitroActivo && nitroRestante > 0) {
                aceleracionEfectiva *= potenciaNitro;
                velMaxMarcha *= 1.15f;
                nitroRestante -= delta;
                if (nitroRestante <= 0) {
                    nitroRestante = 0;
                    nitroActivo = false;
                }
            }

            if (velocidad < velMaxMarcha) {
                velocidad += aceleracionEfectiva * delta;
                if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
            }
        }

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
        return MathUtils.clamp(1f - exceso * 0.5f, PERDIDA_MIN_PATINAJE, PERDIDA_MAX_PATINAJE);
    }

    public void desacelerar(float delta) {
        this.estaAcelerando = false;
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

    private void actualizarAudioMotor() {
        if (estaAcelerando) {
            if (idMotor == -1) {
                idMotor = sonidoMotor.loop(0.5f);
            }

            float volumenGlobal = (
                mainGame != null) ? mainGame.getVolumenGameplay() : 1.0f;

            float ratioRpm = rpm / rpmMaximas;
            float ratioMarcha = (float) marchaActual / (relacionesTransmision.length - 1);

            float pitchBase = 0.5f + (ratioMarcha * 0.6f);
            float pitchDinamico = pitchBase + (ratioRpm * 1.2f);
            pitchDinamico = MathUtils.clamp(pitchDinamico, 0.5f, 2.2f);

            float volumenBase = 0.4f + (ratioMarcha * 0.3f);
            float volumenDinamico = volumenBase + (ratioRpm * 0.4f);
            volumenDinamico = MathUtils.clamp(volumenDinamico, 0.4f, 1.0f);
            volumenDinamico *= volumenGlobal;

            sonidoMotor.setPitch(idMotor, pitchDinamico);
            sonidoMotor.setVolume(idMotor, volumenDinamico);
        } else {
            if (idMotor != -1) {
                sonidoMotor.stop(idMotor);
                idMotor = -1;
            }
        }
    }

    public void actualizar(float delta) {
        posX += (velocidad * FACTOR_MOVIMIENTO) * delta;
        stateTime += delta;
        actualizarAudioMotor();
    }

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
                    frameActual = animAvanzando.getKeyFrame(stateTime, true);
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

    public void apagarMotor() {
        this.estaAcelerando = false;
        if (sonidoMotor != null && idMotor != -1) {
            sonidoMotor.stop(idMotor);
            idMotor = -1;
        }
    }

    public void dispose() {
        apagarMotor();
        if (spriteSheet != null) spriteSheet.dispose();
        if (texturaFallback != null) texturaFallback.dispose();
        if (sonidoMotor != null) {
            sonidoMotor.dispose();
        }
    }

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

    public float getX() { return posX; }
    public float getY() { return posY; }

    public float getFrenteX() {
        return posX + ancho;
    }

    public void alinearFrente(float frenteX) {
        this.posX = frenteX - ancho;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }
}