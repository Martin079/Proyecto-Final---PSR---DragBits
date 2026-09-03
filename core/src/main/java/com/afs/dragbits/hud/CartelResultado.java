package com.afs.dragbits.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.afs.dragbits.util.TexturaSolidaFactory;

/**
 * componente UI del HUD encargado de renderizar el cartel al finalizar
 * una carrera (victoria/derrota, recompensa y dinero total).
 */
public class CartelResultado extends ElementoHUD {

    private final Texture texturaCartel;
    private final Texture texturaBoton;
    private final BitmapFont fuenteTexto;
    private final Rectangle boundsBoton;

    private static final float ANCHO_CARTEL = 460f;
    private static final float ALTO_CARTEL = 240f;
    private static final float ANCHO_BOTON = 220f;
    private static final float ALTO_BOTON = 50f;

    public CartelResultado(float anchoPantalla, float altoPantalla) {
        super(anchoPantalla, altoPantalla);

        texturaCartel = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.85f));
        texturaBoton = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, Color.valueOf("27ae60"));

        fuenteTexto = new BitmapFont();
        fuenteTexto.setColor(Color.WHITE);

        float btnX = (anchoPantalla - ANCHO_BOTON) / 2f;
        float btnY = (altoPantalla - ALTO_CARTEL) / 2f + 20f;
        boundsBoton = new Rectangle(btnX, btnY, ANCHO_BOTON, ALTO_BOTON);
    }


    public void dibujar(SpriteBatch batch, boolean gano, int recompensa, long dineroTotal, float anchoPantalla, float altoPantalla) {
        aplicarProyeccion(batch);

        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        batch.draw(texturaCartel, cartelX, cartelY);

        fuenteTexto.getData().setScale(2f);
        if (gano) {
            fuenteTexto.setColor(Color.GOLD);
            fuenteTexto.draw(batch, "¡VICTORIA!", cartelX + 140f, cartelY + 200f);

            fuenteTexto.getData().setScale(1.2f);
            fuenteTexto.setColor(Color.GREEN);
            fuenteTexto.draw(batch, "Recompensa: +$" + recompensa, cartelX + 130f, cartelY + 140f);
        } else {
            fuenteTexto.setColor(Color.RED);
            fuenteTexto.draw(batch, "DERROTA", cartelX + 160f, cartelY + 200f);

            fuenteTexto.getData().setScale(1.2f);
            fuenteTexto.setColor(Color.WHITE);
            fuenteTexto.draw(batch, "Recompensa: +$0", cartelX + 165f, cartelY + 140f);
        }

        fuenteTexto.setColor(Color.WHITE);
        fuenteTexto.draw(batch, "Total: $" + dineroTotal, cartelX + 175f, cartelY + 100f);

        batch.draw(texturaBoton, boundsBoton.x, boundsBoton.y, boundsBoton.width, boundsBoton.height);
        fuenteTexto.getData().setScale(1.2f);
        fuenteTexto.draw(batch, "VOLVER AL MAPA", boundsBoton.x + 35f, boundsBoton.y + 32f);
    }

    /**
     * verifica si una coordenada del espacio de interfaz toca el botón "Volver al Mapa".
     */
    public boolean fueBotonTocado(Vector3 coordsVirtuales) {
        return boundsBoton.contains(coordsVirtuales.x, coordsVirtuales.y);
    }

    @Override
    public void dispose() {
        if (texturaCartel != null) texturaCartel.dispose();
        if (texturaBoton != null) texturaBoton.dispose();
        if (fuenteTexto != null) fuenteTexto.dispose();
    }
}
