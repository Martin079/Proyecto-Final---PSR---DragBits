package com.afs.dragbits.mapas;

import com.afs.dragbits.util.TexturaSolidaFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PistaCiudad implements Pista {

    private static final float ANCHO_SECCION = 1080f;
    private static final int CANTIDAD_INTERMEDIAS = 5;

    // Zona de asfalto de la imagen ya escalada a 1080 de alto (aprox., ajustar a ojo)
    private static final float Y_RUTA_MIN = 83f;
    private static final float Y_RUTA_MAX = 426f;

    private final Texture fondo;
    private final Texture texturaMeta;
    private final float X_META;

    public PistaCiudad() {
        fondo = new Texture(Gdx.files.internal("sprites/Pistas/Ciudad.png"));
        fondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaMeta = TexturaSolidaFactory.crearTextura(Color.WHITE);
        this.X_META = (1 + CANTIDAD_INTERMEDIAS) * ANCHO_SECCION;
    }

    @Override
    public void dibujar(SpriteBatch batch, float altoPantalla) {
        // Mismo rango que Picodromo: de -1 a CANTIDAD_INTERMEDIAS + 2
        for (int i = -1; i <= CANTIDAD_INTERMEDIAS + 2; i++) {
            batch.draw(fondo, i * ANCHO_SECCION, 0, ANCHO_SECCION, altoPantalla);
        }
        // Línea de meta provisoria
        batch.draw(texturaMeta, getPosicionLineaMeta(), Y_RUTA_MIN, 12f, Y_RUTA_MAX - Y_RUTA_MIN);
    }

    @Override
    public float getPosicionLineaMeta() {
        return X_META + (ANCHO_SECCION / 2f);
    }

    @Override
    public float getPosicionSpawnX() {
        return ANCHO_SECCION * 0.45f;
    }

    // Rival en el carril de arriba, jugador en el de abajo (separados por la línea amarilla)
    @Override
    public float getYJugador() { return 130f; }

    @Override
    public float getYRival() { return 290f; }

    @Override
    public void dispose() {
        fondo.dispose();
        texturaMeta.dispose();
    }
}
