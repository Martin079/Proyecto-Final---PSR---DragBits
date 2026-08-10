package com.afs.dragbits.HUD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.afs.dragbits.funcionalidades.CajaDeCambios;

/**renderiza esquema de la palanca  a la izquierda del HUD*/
public class Palanca {

    private final OrthographicCamera camaraHUD;
    private Texture spriteSheet;

    private TextureRegion frameEsquema;   // frame 0: diagrama
    private TextureRegion frameCentro;    // frame 1: palanca centro
    private TextureRegion frameIzquierda; // frame 2: palanca izquierda
    private TextureRegion frameDerecha;   // frame 3: palanca derecha

    private float ancho = 180f;
    private float alto = 90f;

    public Palanca(float anchoPantalla, float altoPantalla) {
        camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, anchoPantalla, altoPantalla);

        spriteSheet = new Texture(Gdx.files.internal("sprites/palanca -sheet.png"));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 120, 60);
        frameEsquema   = tmp[0][0];
        frameCentro    = tmp[0][1];
        frameIzquierda = tmp[0][2];
        frameDerecha   = tmp[0][3];
    }

    public void dibujar(SpriteBatch batch, CajaDeCambios caja, float anchoPantalla) {
        batch.setProjectionMatrix(camaraHUD.combined);

        //izquierda del HUD (arranca en anchoPantalla - 310f)
        float posX = anchoPantalla - 530f;
        float posY = 30f; // alineacion altura HUD principal

        // dibujar esquema base
        batch.draw(frameEsquema, posX, posY, ancho, alto);

        //determinar palanca y desplazamiento X
        TextureRegion perillaActual;
        int pX = caja.getPalancaX();
        int pY = caja.getPalancaY();

        float offsetX = 0f;

        if (pX == -1) {
            perillaActual = frameIzquierda;
            offsetX = -44f; //a la izquierda
        } else if (pX == 1) {
            perillaActual = frameDerecha;
            offsetX = 44f;  //a la derecha
        } else {
            perillaActual = frameCentro;
            offsetX = 0f;
        }

        //desplazamiento Y
        float offsetY = 0f;
        if (pY == 1) {
            offsetY = 18f;  // arriba
        } else if (pY == -1) {
            offsetY = -18f; // abajo
        }

        // dibujar palanca arriba del esquema con el desplazamiento aplicado
        batch.draw(perillaActual, posX + offsetX, posY + offsetY, ancho, alto);
    }

    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }
}
