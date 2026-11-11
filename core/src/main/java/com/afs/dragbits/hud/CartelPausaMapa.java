package com.afs.dragbits.hud;

import com.afs.dragbits.jugador.RepositorioJugador;
import com.afs.dragbits.jugador.Jugador;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.afs.dragbits.util.TexturaSolidaFactory;

public class CartelPausaMapa extends ElementoHUD {

    private final Texture TEXTURA_FONDO;
    private final Texture TEXTURA_BOTON_NORMAL;
    private final Texture TEXTURA_BOTON_HOVER;
    private final Texture TEXTURA_BOTON_PELIGRO;
    private final Texture TEXTURA_BOTON_PELIGRO_HOVER;
    private final Texture TEXTURA_BOTON_EXITO;
    private final BitmapFont FUENTE_TEXTO;
    private final BitmapFont FUENTE_TITULO;

    private Rectangle boundsReanudar;
    private Rectangle boundsGuardar;
    private Rectangle boundsEliminar;
    private Rectangle boundsMenu;
    private Rectangle boundsSalir;
    private Rectangle boundsConfirmar;
    private Rectangle boundsCancelar;

    private static final float ANCHO_CARTEL = 500f;
    private static final float ALTO_CARTEL = 480f;
    private static final float ANCHO_BOTON = 320f;
    private static final float ANCHO_BOTON_CONFIRM = 220f; // Botones confirmación más angostos
    private static final float ALTO_BOTON = 60f;
    private static final float ESPACIO_ENTRE_BOTONES = 18f;

    private boolean mostrandoConfirmacionEliminar = false;
    private boolean mostrarMensajeGuardado = false;
    private float tiempoMensajeGuardado = 0f;
    private static final float DURACION_MENSAJE_GUARDADO = 1.5f;

    private final Jugador jugador;
    private final RepositorioJugador repositorioJugador;
    private final Game GAME;

    // Colores grises
    private static final Color GRIS_OSCURO = new Color(0.2f, 0.2f, 0.2f, 1f);
    private static final Color GRIS_MEDIO = new Color(0.35f, 0.35f, 0.35f, 1f);
    private static final Color GRIS_CLARO = new Color(0.5f, 0.5f, 0.5f, 1f);
    private static final Color GRIS_HOVER = new Color(0.45f, 0.45f, 0.45f, 1f);
    private static final Color ROJO_OSCURO = new Color(0.5f, 0.15f, 0.15f, 1f);
    private static final Color ROJO_HOVER = new Color(0.65f, 0.2f, 0.2f, 1f);
    private static final Color VERDE_EXITO = new Color(0.15f, 0.5f, 0.15f, 1f);
    private static final Color VERDE_HOVER = new Color(0.2f, 0.65f, 0.2f, 1f);
    private static final Color BLANCO = Color.WHITE;

    public CartelPausaMapa(float anchoPantalla, float altoPantalla, Jugador jugador, RepositorioJugador repositorioJugador, Game GAME) {
        super();
        this.jugador = jugador;
        this.repositorioJugador = repositorioJugador;
        this.GAME = GAME;

        TEXTURA_FONDO = TexturaSolidaFactory.crearTextura((int) ANCHO_CARTEL, (int) ALTO_CARTEL, new Color(0, 0, 0, 0.85f));
        TEXTURA_BOTON_NORMAL = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, GRIS_MEDIO);
        TEXTURA_BOTON_HOVER = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, GRIS_HOVER);
        TEXTURA_BOTON_PELIGRO = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, ROJO_OSCURO);
        TEXTURA_BOTON_PELIGRO_HOVER = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, ROJO_HOVER);
        TEXTURA_BOTON_EXITO = TexturaSolidaFactory.crearTextura((int) ANCHO_BOTON, (int) ALTO_BOTON, VERDE_EXITO);

        FUENTE_TEXTO = new BitmapFont();
        FUENTE_TEXTO.setColor(BLANCO);
        FUENTE_TITULO = new BitmapFont();
        FUENTE_TITULO.setColor(BLANCO);

        // Inicializar bounds en posición por defecto (se actualizan en dibujar)
        actualizarBounds(anchoPantalla, altoPantalla);
    }

    private void actualizarBounds(float anchoPantalla, float altoPantalla) {
        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        // Centrar grupo de botones verticalmente en el cartel
        // 5 botones: altura total = 5 * ALTO_BOTON + 4 * ESPACIO_ENTRE_BOTONES
        float alturaTotalBotones = 5 * ALTO_BOTON + 4 * ESPACIO_ENTRE_BOTONES;
        float inicioY = cartelY + (ALTO_CARTEL + alturaTotalBotones) / 2f - ALTO_BOTON;

        float centroX = cartelX + (ANCHO_CARTEL - ANCHO_BOTON) / 2f;

        boundsReanudar = new Rectangle(centroX, inicioY, ANCHO_BOTON, ALTO_BOTON);
        boundsGuardar = new Rectangle(centroX, inicioY - (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);
        boundsEliminar = new Rectangle(centroX, inicioY - 2 * (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);
        boundsMenu = new Rectangle(centroX, inicioY - 3 * (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);
        boundsSalir = new Rectangle(centroX, inicioY - 4 * (ALTO_BOTON + ESPACIO_ENTRE_BOTONES), ANCHO_BOTON, ALTO_BOTON);

        // Botones de confirmación (centrados en la parte inferior del cartel, más angostos para no solaparse)
        float confirmY = cartelY + 80f;
        float gapEntreConfirm = 30f;
        float confirmTotalWidth = 2 * ANCHO_BOTON_CONFIRM + gapEntreConfirm;
        float confirmStartX = cartelX + (ANCHO_CARTEL - confirmTotalWidth) / 2f;
        boundsConfirmar = new Rectangle(confirmStartX, confirmY, ANCHO_BOTON_CONFIRM, ALTO_BOTON);
        boundsCancelar = new Rectangle(confirmStartX + ANCHO_BOTON_CONFIRM + gapEntreConfirm, confirmY, ANCHO_BOTON_CONFIRM, ALTO_BOTON);
    }

    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla, float mouseX, float mouseY) {
        // Actualizar bounds por si cambió la resolución
        actualizarBounds(anchoPantalla, altoPantalla);

        // Actualizar timer del mensaje de guardado
        if (mostrarMensajeGuardado) {
            tiempoMensajeGuardado += Gdx.graphics.getDeltaTime();
            if (tiempoMensajeGuardado >= DURACION_MENSAJE_GUARDADO) {
                mostrarMensajeGuardado = false;
                tiempoMensajeGuardado = 0f;
            }
        }

        float cartelX = (anchoPantalla - ANCHO_CARTEL) / 2f;
        float cartelY = (altoPantalla - ALTO_CARTEL) / 2f;

        batch.draw(TEXTURA_FONDO, cartelX, cartelY);

        GlyphLayout layout = new GlyphLayout();

        if (mostrandoConfirmacionEliminar) {
            // Pantalla de confirmación para eliminar progreso
            FUENTE_TITULO.getData().setScale(2.2f);
            FUENTE_TITULO.setColor(Color.valueOf("e74c3c"));
            layout.setText(FUENTE_TITULO, "ELIMINAR PROGRESO");
            FUENTE_TITULO.draw(batch, "ELIMINAR PROGRESO", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + ALTO_CARTEL - 80f);

            FUENTE_TEXTO.getData().setScale(1.3f);
            FUENTE_TEXTO.setColor(BLANCO);
            layout.setText(FUENTE_TEXTO, "¿Estás seguro? Se perderán");
            FUENTE_TEXTO.draw(batch, "¿Estás seguro? Se perderán", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 260f);
            layout.setText(FUENTE_TEXTO, "TODOS los datos guardados.");
            FUENTE_TEXTO.draw(batch, "TODOS los datos guardados.", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 230f);

            layout.setText(FUENTE_TEXTO, "Esta acción NO se puede deshacer.");
            FUENTE_TEXTO.getData().setScale(1.1f);
            FUENTE_TEXTO.setColor(Color.valueOf("e74c3c"));
            FUENTE_TEXTO.draw(batch, "Esta acción NO se puede deshacer.", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + 180f);

            // Botón CONFIRMAR (rojo) - "SÍ, ELIMINAR"
            boolean hoverConfirmar = boundsConfirmar.contains(mouseX, mouseY);
            batch.draw(hoverConfirmar ? TEXTURA_BOTON_PELIGRO_HOVER : TEXTURA_BOTON_PELIGRO,
                boundsConfirmar.x, boundsConfirmar.y, boundsConfirmar.width, boundsConfirmar.height);
            FUENTE_TEXTO.getData().setScale(1.4f);
            FUENTE_TEXTO.setColor(BLANCO);
            layout.setText(FUENTE_TEXTO, "SÍ, ELIMINAR");
            FUENTE_TEXTO.draw(batch, "SÍ, ELIMINAR",
                boundsConfirmar.x + (ANCHO_BOTON_CONFIRM - layout.width) / 2f, boundsConfirmar.y + 38f);

            // Botón CANCELAR (gris)
            boolean hoverCancelar = boundsCancelar.contains(mouseX, mouseY);
            batch.draw(hoverCancelar ? TEXTURA_BOTON_HOVER : TEXTURA_BOTON_NORMAL,
                boundsCancelar.x, boundsCancelar.y, boundsCancelar.width, boundsCancelar.height);
            layout.setText(FUENTE_TEXTO, "CANCELAR");
            FUENTE_TEXTO.draw(batch, "CANCELAR",
                boundsCancelar.x + (ANCHO_BOTON_CONFIRM - layout.width) / 2f, boundsCancelar.y + 38f);
        } else {
            // Menú principal de pausa
            FUENTE_TITULO.getData().setScale(2.5f);
            FUENTE_TITULO.setColor(Color.valueOf("3498db"));
            layout.setText(FUENTE_TITULO, "PAUSA");
            FUENTE_TITULO.draw(batch, "PAUSA", cartelX + (ANCHO_CARTEL - layout.width) / 2f, cartelY + ALTO_CARTEL - 60f);

            FUENTE_TEXTO.getData().setScale(1.5f);
            FUENTE_TEXTO.setColor(BLANCO);

            // Botón REANUDAR
            dibujarBoton(batch, boundsReanudar, "REANUDAR", false, false, layout, mouseX, mouseY);
            // Botón GUARDAR PROGRESO - verde si mostrando mensaje
            dibujarBoton(batch, boundsGuardar, "GUARDAR PROGRESO", false, mostrarMensajeGuardado, layout, mouseX, mouseY);
            // Botón ELIMINAR PROGRESO (rojo)
            dibujarBoton(batch, boundsEliminar, "ELIMINAR PROGRESO", true, false, layout, mouseX, mouseY);
            // Botón VOLVER AL MENÚ
            dibujarBoton(batch, boundsMenu, "VOLVER AL MENÚ", false, false, layout, mouseX, mouseY);
            // Botón SALIR AL ESCRITORIO
            dibujarBoton(batch, boundsSalir, "SALIR AL ESCRITORIO", false, false, layout, mouseX, mouseY);

            // Mensaje de guardado encima del botón
            if (mostrarMensajeGuardado) {
                FUENTE_TEXTO.getData().setScale(1.2f);
                FUENTE_TEXTO.setColor(Color.valueOf("2ecc71"));
                layout.setText(FUENTE_TEXTO, "¡Progreso guardado!");
                FUENTE_TEXTO.draw(batch, "¡Progreso guardado!",
                    boundsGuardar.x + (ANCHO_BOTON - layout.width) / 2f, boundsGuardar.y - 10f);
            }
        }
    }

    private void dibujarBoton(SpriteBatch batch, Rectangle bounds, String texto, boolean esPeligro, boolean esExito, GlyphLayout layout, float mouseX, float mouseY) {
        boolean hover = bounds.contains(mouseX, mouseY);
        Texture textura;
        if (esPeligro) {
            textura = hover ? TEXTURA_BOTON_PELIGRO_HOVER : TEXTURA_BOTON_PELIGRO;
        } else if (esExito) {
            textura = TEXTURA_BOTON_EXITO;
        } else {
            textura = hover ? TEXTURA_BOTON_HOVER : TEXTURA_BOTON_NORMAL;
        }
        batch.draw(textura, bounds.x, bounds.y, bounds.width, bounds.height);
        layout.setText(FUENTE_TEXTO, texto);
        FUENTE_TEXTO.draw(batch, texto,
            bounds.x + (ANCHO_BOTON - layout.width) / 2f, bounds.y + 38f);
    }

    // Métodos de detección usando bounds actualizados
    public boolean fueBotonReanudarTocado(float x, float y) {
        return !mostrandoConfirmacionEliminar && boundsReanudar.contains(x, y);
    }

    public boolean fueBotonGuardarTocado(float x, float y) {
        return !mostrandoConfirmacionEliminar && boundsGuardar.contains(x, y);
    }

    public boolean fueBotonEliminarTocado(float x, float y) {
        return !mostrandoConfirmacionEliminar && boundsEliminar.contains(x, y);
    }

    public boolean fueBotonMenuTocado(float x, float y) {
        return !mostrandoConfirmacionEliminar && boundsMenu.contains(x, y);
    }

    public boolean fueBotonSalirTocado(float x, float y) {
        return !mostrandoConfirmacionEliminar && boundsSalir.contains(x, y);
    }

    public boolean fueBotonConfirmarEliminarTocado(float x, float y) {
        return mostrandoConfirmacionEliminar && boundsConfirmar.contains(x, y);
    }

    public boolean fueBotonCancelarEliminarTocado(float x, float y) {
        return mostrandoConfirmacionEliminar && boundsCancelar.contains(x, y);
    }

    public void setMostrandoConfirmacionEliminar(boolean mostrando) {
        this.mostrandoConfirmacionEliminar = mostrando;
    }

    public boolean isMostrandoConfirmacionEliminar() {
        return mostrandoConfirmacionEliminar;
    }

    public void setMostrarMensajeGuardado(boolean mostrar) {
        this.mostrarMensajeGuardado = mostrar;
        this.tiempoMensajeGuardado = 0f;
    }

    public void guardarProgreso() {
        if (jugador != null && repositorioJugador != null) {
            repositorioJugador.guardarProgreso(jugador);
        }
    }

    public void eliminarProgresoYVolverAlMenu() {
        Gdx.app.log("CartelPausaMapa", "Eliminando progreso...");
        if (repositorioJugador != null) {
            repositorioJugador.borrarProgreso();
            Gdx.app.log("CartelPausaMapa", "Progreso borrado del repositorio");
        }
        Gdx.app.postRunnable(() -> {
            Gdx.app.log("CartelPausaMapa", "Cambiando a MainMenuScreen");
            GAME.setScreen(new com.afs.dragbits.screens.MainMenuScreen(GAME, (com.afs.dragbits.Main) GAME));
        });
    }

    public void volverAlMenuPrincipal() {
        Gdx.app.postRunnable(() -> GAME.setScreen(new com.afs.dragbits.screens.MainMenuScreen(GAME, (com.afs.dragbits.Main) GAME)));
    }

    public void salirAlEscritorio() {
        Gdx.app.exit();
    }

    public void resize(int width, int height) {
        // Se recalculan en dibujar()
    }

    @Override
    public void dispose() {
        if (TEXTURA_FONDO != null) TEXTURA_FONDO.dispose();
        if (TEXTURA_BOTON_NORMAL != null) TEXTURA_BOTON_NORMAL.dispose();
        if (TEXTURA_BOTON_HOVER != null) TEXTURA_BOTON_HOVER.dispose();
        if (TEXTURA_BOTON_PELIGRO != null) TEXTURA_BOTON_PELIGRO.dispose();
        if (TEXTURA_BOTON_PELIGRO_HOVER != null) TEXTURA_BOTON_PELIGRO_HOVER.dispose();
        if (TEXTURA_BOTON_EXITO != null) TEXTURA_BOTON_EXITO.dispose();
        if (FUENTE_TEXTO != null) FUENTE_TEXTO.dispose();
        if (FUENTE_TITULO != null) FUENTE_TITULO.dispose();
    }
}