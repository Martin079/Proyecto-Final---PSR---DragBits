package com.afs.dragbits.autos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Auto {

    // estados posibles para la animación del auto (cambiar a clase ENUM)
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

    protected float velocidad;
    protected float velocidadMaxima;
    protected float aceleracion;
    protected float rpm;
    protected float rpmMaximas;

    // cambios y nitro
    protected int marchaActual; // 0 = Neutral, 1..5 = Marchas
    protected boolean embraguePresionado;
    protected boolean nitroActivo; //  uso futuro
    protected float[] relacionesTransmision = {0f, 0.25f, 0.45f, 0.65f, 0.85f, 1.0f};

    //sprites y animaciones
    private Texture spriteSheet;
    private TextureRegion frameEstatico;
    private TextureRegion frameAvance;
    private Animation<TextureRegion> animCambio;
    private Animation<TextureRegion> animNitro;

    private float stateTime; // reloj interno para animaciones
    private Texture texturaFallback; // respaldo si no se carga imagen

    /** constructor principal con sprite sheet. */
    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, String rutaSpriteSheet) {
        inicializarAtributos(posX, posY, velocidadMaxima, aceleracion);
        cargarSpriteSheet(rutaSpriteSheet);
    }

    /** constructor alternativo con color plano */
    public Auto(float posX, float posY, float velocidadMaxima, float aceleracion, Color colorFallback) {
        inicializarAtributos(posX, posY, velocidadMaxima, aceleracion);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(colorFallback);
        pixmap.fill();
        this.texturaFallback = new Texture(pixmap);
        pixmap.dispose();
    }

    private void inicializarAtributos(float posX, float posY, float velocidadMaxima, float aceleracion) {
        this.posX = posX;
        this.posY = posY;
        this.velocidadMaxima = velocidadMaxima;
        this.aceleracion = aceleracion;
        this.velocidad = 0f;
        this.rpm = 800f;
        this.rpmMaximas = 7000f;
        this.marchaActual = 0;
        this.embraguePresionado = false;
        this.nitroActivo = false;
        this.stateTime = 0f;
    }

    private void cargarSpriteSheet(String ruta) {
        spriteSheet = new Texture(Gdx.files.internal(ruta));

        //mantiene los píxeles nítidos al agrandar el auto
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // recorta la tira horizontal en cuadros de 100x40 píxeles
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 100, 40);

        //Frame 0: Estático - 1: Avance
        frameEstatico = tmp[0][0];
        frameAvance   = tmp[0][1];

        // Frame 2 y 3: Animacion Cambio de Marcha (0.15 seg por cuadro)
        animCambio = new Animation<>(0.15f, tmp[0][2], tmp[0][3]);

        // Frame 4 y 5: Animacion Nitro (0.10 seg por cuadro)
        animNitro = new Animation<>(0.10f, tmp[0][4], tmp[0][5]);
    }

    public void acelerar(float delta) {
        if (!embraguePresionado && marchaActual > 0) {
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaActual];

            if (velocidad < velMaxMarcha) {
                velocidad += aceleracion * delta;
                if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
            }
        }

        if (embraguePresionado || marchaActual == 0) {
            rpm += 4000f * delta;
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        } else {
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaActual];
            rpm = 800f + (velocidad / velMaxMarcha) * (rpmMaximas - 800f);
        }
    }

    public void desacelerar(float delta) {
        if (velocidad > 0) {
            velocidad -= (aceleracion * 0.4f) * delta;
            if (velocidad < 0) velocidad = 0;
        }

        if (rpm > 800f) {
            rpm -= 3000f * delta;
            if (rpm < 800f) rpm = 800f;
        }
    }

    public void actualizar(float delta) {
        posX += velocidad * delta;
        stateTime += delta; // acumula tiempo para avanzar los cuadros de animacion
    }

    /** evalua la situación actual del auto y devuelve el estado correspondiente*/
    public EstadoAuto getEstadoActual() {
        if (nitroActivo) {
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
            // dibujado de respaldo si se usó el constructor de color plano
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
}
