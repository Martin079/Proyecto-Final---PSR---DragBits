package com.afs.dragbits.autos;

import com.afs.dragbits.Main;
import com.afs.dragbits.audio.ProveedorMusica;
import com.afs.dragbits.funcionalidades.EntradaJugador;
import com.afs.dragbits.network.AutoState;
import com.afs.dragbits.network.Input;
import com.afs.dragbits.network.PhysicsEngine;
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

    protected float rpm;
    protected float rpmMaximas;


    protected int marchaActual;
    protected boolean embraguePresionado;
    protected boolean nitroActivo;
    protected float[] relacionesTransmision = {0f, 0.30f, 0.50f, 0.70f, 0.85f, 1.0f};

    // Posición visual de la palanca de cambios (para HUD)
    protected int palancaX = 0;
    protected int palancaY = 0;

    private static final float[] MULTIPLICADOR_POTENCIA_POR_MARCHA = {
        1.0f,
        2.50f,
        1.80f,
        1.20f,
        0.85f,
        0.55f
    };

    protected boolean patinando;


    private Texture spriteSheet;
    private TextureRegion frameEstatico;
    private Animation<TextureRegion> animAvanzando;
    private Animation<TextureRegion> animCambio;
    private Animation<TextureRegion> animNitro;
    private static Animation<TextureRegion> animPatinaje;
    private static Texture texturaPatinaje;

    private float stateTime;
    private Texture texturaFallback;

    private Sound sonidoMotor;
    private long idMotor = -1;
    protected boolean estaAcelerando;
    private Main mainGame;

    public static final float ESCALA_SPRITE = 2.8f;
    public static final float ANCHO_BASE = 280f;

    private static final float FACTOR_MOVIMIENTO = 4.2f;


    protected static final float UMBRAL_ARRANQUE = 25f;
    protected static final float FACTOR_ARRANQUE = 1.25f;
    protected static final float PISO_CURVA_ACELERACION = 0.4f;

    private static final float UMBRAL_PATINAJE = 1.2f;
    private static final float PERDIDA_MIN_PATINAJE = 0.45f;
    private static final float PERDIDA_MAX_PATINAJE = 0.85f;

    // Estado de física compartido (para sincronización con PhysicsEngine)
    protected final AutoState physicsState;

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, String rutaSpriteSheet, Main mainGame) {
        this.mainGame = mainGame;
        this.physicsState = new AutoState(posX, posY, velocidadMaxima, aceleracion, traccion, 3.0f, 1.5f);
        this.physicsState.nitroCapacity = 3.0f;
        this.physicsState.nitroPower = 1.5f;
        sincronizarDesdePhysics();
        cargarSpriteSheet(rutaSpriteSheet);
    }

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, Color colorFallback, Main mainGame) {
        this.mainGame = mainGame;
        this.physicsState = new AutoState(posX, posY, velocidadMaxima, aceleracion, traccion, 3.0f, 1.5f);
        this.physicsState.nitroCapacity = 3.0f;
        this.physicsState.nitroPower = 1.5f;
        sincronizarDesdePhysics();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colorFallback);
        pixmap.fill();
        this.texturaFallback = new Texture(pixmap);
        pixmap.dispose();
    }

    protected void sincronizarDesdePhysics() {
        this.posX = physicsState.posX;
        this.posY = physicsState.posY;
        this.velocidadMaxima = physicsState.maxVelocity;
        this.aceleracion = physicsState.acceleration;
        this.traccion = physicsState.traction;
        this.potenciaNitro = physicsState.nitroPower;
        this.capacidadNitro = physicsState.nitroCapacity;
        this.nitroRestante = physicsState.nitroRemaining;

        this.velocidad = physicsState.velocity;
        this.rpm = physicsState.rpm;
        this.rpmMaximas = physicsState.RPM_MAXIMAS;
        this.marchaActual = physicsState.gear;
        this.embraguePresionado = physicsState.clutchPressed;
        this.nitroActivo = physicsState.nitroActive;
        this.patinando = physicsState.spinning;
        this.stateTime = 0f;

        this.sonidoMotor = Gdx.audio.newSound(Gdx.files.internal("audio/Gameplay/motor_loop.wav"));
        this.estaAcelerando = false;
    }

    private void sincronizarHaciaPhysics() {
        physicsState.posX = this.posX;
        physicsState.posY = this.posY;
        physicsState.velocity = this.velocidad;
        physicsState.rpm = this.rpm;
        physicsState.gear = this.marchaActual;
        physicsState.clutchPressed = this.embraguePresionado;
        physicsState.nitroActive = this.nitroActivo;
        physicsState.nitroRemaining = this.nitroRestante;
        physicsState.spinning = this.patinando;
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

        cargarAnimacionPatinaje();
    }

    private static void cargarAnimacionPatinaje() {
        if (animPatinaje == null) {
            texturaPatinaje = SpriteSheetLoader.cargarTextura("sprites/Autos/Jugador/patinaje.png");
            int anchoFrame = texturaPatinaje.getWidth() / 4;
            int altoFrame = texturaPatinaje.getHeight();
            TextureRegion[] frames = SpriteSheetLoader.recortar(texturaPatinaje, anchoFrame, altoFrame);
            animPatinaje = new Animation<>(0.08f, frames);
        }
    }

    public void acelerar(float delta) {
        // Delegar en PhysicsEngine para consistencia cliente-servidor
        Input input = new Input(true, embraguePresionado, 0, 0, false);
        PhysicsEngine.step(physicsState, input, delta);
        sincronizarDesdePhysics();
        this.estaAcelerando = true;
    }

    public void desacelerar(float delta) {
        Input input = new Input(false, embraguePresionado, 0, 0, false);
        PhysicsEngine.step(physicsState, input, delta);
        sincronizarDesdePhysics();
        this.estaAcelerando = false;
    }

    public void actualizarConInput(Input input, float delta) {
        // Actualizar posición visual de la palanca
        if (input.clutch && (input.leverX != 0 || input.leverY != 0)) {
            this.palancaX = input.leverX;
            this.palancaY = input.leverY;
        }
        PhysicsEngine.step(physicsState, input, delta);
        sincronizarDesdePhysics();
        this.estaAcelerando = input.accelerator;
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

    public void aplicarInput(EntradaJugador entrada, float delta) {
        Input input = new Input(
            entrada.estaAcelerando(),
            entrada.estaEmbragado(),
            entrada.seMovioPalancaIzquierda() ? -1 : (entrada.seMovioPalancaDerecha() ? 1 : 0),
            entrada.seMovioPalancaArriba() ? 1 : (entrada.seMovioPalancaAbajo() ? -1 : 0),
            entrada.consumirNitro()
        );
        actualizarConInput(input, delta);
    }

    public EstadoAuto getEstadoActual() {
        if (nitroActivo && nitroRestante > 0) {
            return EstadoAuto.NITRO;
        } else if (embraguePresionado) {
            return EstadoAuto.CAMBIANDO_MARCHA;
        } else if (patinando && estaAcelerando) {
            return EstadoAuto.PATINANDO;
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
                case PATINANDO:
                    frameActual = animAvanzando.getKeyFrame(stateTime, true);
                    batch.draw(frameActual, posX, posY, ancho, alto);
                    if (animPatinaje != null) {
                        TextureRegion frameHumo = animPatinaje.getKeyFrame(stateTime, true);
                        float anchoPatinaje = frameHumo.getRegionWidth() * ESCALA_SPRITE;
                        float altoPatinaje = frameHumo.getRegionHeight() * ESCALA_SPRITE;
                        batch.draw(frameHumo, posX, posY, anchoPatinaje, altoPatinaje);
                    }
                    return;
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
        if (texturaPatinaje != null) {
            texturaPatinaje.dispose();
            texturaPatinaje = null;
            animPatinaje = null;
        }
    }

    public float getVelocidad() { return velocidad; }
    public float getRpm() { return rpm; }
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public int getMarchaActual() { return marchaActual; }
    public void setMarchaActual(int marcha) { this.marchaActual = marcha; physicsState.gear = marcha; }
    public boolean isEmbraguePresionado() { return embraguePresionado; }
    public void setEmbraguePresionado(boolean embrague) { this.embraguePresionado = embrague; physicsState.clutchPressed = embrague; }
    public boolean isNitroActivo() { return nitroActivo; }
    public void setNitroActivo(boolean nitroActivo) { this.nitroActivo = nitroActivo; physicsState.nitroActive = nitroActivo; }
    public void setPosX(float posX) { this.posX = posX; physicsState.posX = posX; }
    public void setVelocidad(float velocidad) { this.velocidad = velocidad; physicsState.velocity = velocidad; }

    public float getTraccion() { return traccion; }
    public void setTraccion(float traccion) { this.traccion = traccion; physicsState.traction = traccion; }
    public float getAceleracion() { return aceleracion; }
    public void setAceleracion(float aceleracion) { this.aceleracion = aceleracion; physicsState.acceleration = aceleracion; }
    public float getVelocidadMaxima() { return velocidadMaxima; }
    public void setVelocidadMaxima(float velocidadMaxima) { this.velocidadMaxima = velocidadMaxima; physicsState.maxVelocity = velocidadMaxima; }
    public float getNitroRestante() { return nitroRestante; }
    public float getCapacidadNitro() { return capacidadNitro; }
    public boolean isPatinando() { return patinando; }

    public AutoState getPhysicsState() {
        return physicsState.copy();
    }

    public int getPalancaX() { return palancaX; }
    public int getPalancaY() { return palancaY; }

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
