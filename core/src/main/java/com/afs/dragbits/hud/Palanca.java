package com.afs.dragbits.hud;

import com.afs.dragbits.funcionalidades.CajaDeCambios;
import com.afs.dragbits.util.SpriteSheetLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Palanca extends ElementoHUD {

    private Texture spriteSheet;

    private TextureRegion frameEsquema;   // frame 0: diagrama
    private TextureRegion frameCentro;    // frame 1: palanca centro
    private TextureRegion frameIzquierda; // frame 2: palanca izquierda
    private TextureRegion frameDerecha;   // frame 3: palanca derecha

    private static final float ANCHO_VIRTUAL = 1920f;
    private static final float ALTO_VIRTUAL = 1080f;

    private float ancho = 252f;
    private float alto = 126f;

    public Palanca() {
        super();

        spriteSheet = SpriteSheetLoader.cargarTextura("sprites/HUD/palanca -sheet.png");
        TextureRegion[] frames = SpriteSheetLoader.recortar(spriteSheet, 120, 60);

        frameEsquema   = frames[0];
        frameCentro    = frames[1];
        frameIzquierda = frames[2];
        frameDerecha   = frames[3];
    }

    public void dibujar(SpriteBatch batch, CajaDeCambios caja, float anchoPantalla) {
        // izquierda del HUD (arranca en anchoPantalla - 310f)
        float posX = anchoPantalla - 560f;
        float posY = 20f; // DEBE SER LA MISMA ALTURA QUE EL HUD PRINCIPAL

        batch.draw(frameEsquema, posX, posY, ancho, alto);

        TextureRegion perillaActual;
        int pX = caja.getPalancaX();
        int pY = caja.getPalancaY();

        float offsetX = 0f;

        if (pX == -1) {
            perillaActual = frameIzquierda;
            offsetX = -44f; // a la izquierda
        } else if (pX == 1) {
            perillaActual = frameDerecha;
            offsetX = 44f;  // a la derecha
        } else {
            perillaActual = frameCentro;
            offsetX = 0f;
        }

        float offsetY = 0f;
        if (pY == 1) {
            offsetY = 18f;  // arriba
        } else if (pY == -1) {
            offsetY = -18f; // abajo
        }

        batch.draw(perillaActual, posX + offsetX, posY + offsetY, ancho, alto);
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
