package com.afs.dragbits.autos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Auto {

    protected float posX;
    protected float posY;
    protected float ancho = 100f;
    protected float alto = 40f;

    protected float velocidad;
    protected float velocidadMaxima;
    protected float aceleracion;
    protected float rpm;
    protected float rpmMaximas;

    // --- NUEVO: SISTEMA DE TRANSMISIÓN ---
    protected int marchaActual; // 0 = Neutral/Punto Muerto, 1..5 = Marchas
    protected boolean embraguePresionado;
    // Relaciones de transmisión hipotéticas para 5 marchas
    protected float[] relacionesTransmision = {0f, 0.25f, 0.45f, 0.65f, 0.85f, 1.0f};

    private Texture texturaAuto;

    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, Color colorAuto) {
        this.posX = posX;
        this.posY = posY;
        this.velocidadMaxima = velocidadMaxima;
        this.aceleracion = aceleracion;
        this.velocidad = 0f;
        this.rpm = 800f;
        this.rpmMaximas = 7000f;
        this.marchaActual = 0; // Inicia en Neutral
        this.embraguePresionado = false;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colorAuto);
        pixmap.fill();
        this.texturaAuto = new Texture(pixmap);
        pixmap.dispose();
    }

    public void acelerar(float delta) {
        // Solo transmite tracción si NO hay embrague y hay una marcha puesta (marcha > 0)
        if (!embraguePresionado && marchaActual > 0) {
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaActual];

            if (velocidad < velMaxMarcha) {
                velocidad += aceleracion * delta;
                if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
            }
        }

        // Subida de RPM (si pisas embrague o estás en neutral, sube rápido en vacío)
        if (embraguePresionado || marchaActual == 0) {
            rpm += 4000f * delta;
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        } else {
            // RPM calculadas según la velocidad actual respecto a la marcha
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaActual];
            rpm = 800f + (velocidad / velMaxMarcha) * (rpmMaximas - 800f);
        }
    }

    public void desacelerar(float delta) {
        if (velocidad > 0) {
            velocidad -= (aceleracion * 0.4f) * delta;
            if (velocidad < 0) velocidad = 0;
        }

        // Caída de RPM a ralentí
        if (rpm > 800f) {
            rpm -= 3000f * delta;
            if (rpm < 800f) rpm = 800f;
        }
    }

    public void actualizar(float delta) {
        posX += velocidad * delta;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaAuto, posX, posY, ancho, alto);
    }

    public void dispose() {
        if (texturaAuto != null) texturaAuto.dispose();
    }

    // Getters y Setters de Transmisión
    public float getVelocidad() { return velocidad; }
    public float getRpm() { return rpm; }
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public int getMarchaActual() { return marchaActual; }
    public void setMarchaActual(int marcha) { this.marchaActual = marcha; }
    public boolean isEmbraguePresionado() { return embraguePresionado; }
    public void setEmbraguePresionado(boolean embrague) { this.embraguePresionado = embrague; }
}
