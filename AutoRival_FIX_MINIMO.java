// ============================================================
// COPY-PASTE DIRECTO: Reemplaza el método actualizarIA en AutoRival.java
// ============================================================

// AGREGAR ESTOS CAMPOS al inicio de la clase (después de línea 13 aprox):
private float tiempoEnMarchaActual = 0f;
private float velocidadAnterior = 0f;

private static final float UMBRAL_RPM_DOWNSHIFT = 2500f;
private static final float TIEMPO_MINIMO_MARCHA = 1.0f;
private static final float ACELERACION_MINIMA_ESPERADA = 8f;

// REEMPLAZAR TODO EL MÉTODO actualizarIA (líneas 40-70) POR ESTE:

@Override
public void actualizarIA(float delta, EstadoSemaforo estadoSemaforo) {
    // 1. ARRANQUE
    if (estadoSemaforo == EstadoSemaforo.VERDE && marchaActual == 0) {
        marchaActual = 1;
        tiempoEnMarchaActual = 0f;
    }

    if (marchaActual > 0) {
        if (embraguePresionado) {
            tiempoSiguienteCambio -= delta;
            if (tiempoSiguienteCambio <= 0) {
                embraguePresionado = false;
            }
        } else {
            tiempoEnMarchaActual += delta;

            // Nitro simple (mejorable después)
            if (!nitroActivo && nitroRestante > 0 && marchaActual >= 3 && rpm > rpmMaximas * 0.6f) {
                if (Math.random() < 0.25f) nitroActivo = true;
            }

            acelerar(delta);

            // ===== NUEVO: Calcular aceleración real =====
            float aceleracionReal = (velocidad - velocidadAnterior) / delta;
            velocidadAnterior = velocidad;

            // ===== UPSHIFT (subir) =====
            boolean rpmLista = rpm >= (rpmMaximas - 600f);
            boolean hayMarchaSuperior = marchaActual < (relacionesTransmision.length - 1);
            boolean velocidadSuficiente = velocidad >= VELOCIDAD_MINIMA_MARCHA[marchaActual + 1];
            boolean tiempoOk = tiempoEnMarchaActual >= TIEMPO_MINIMO_MARCHA;

            if (rpmLista && hayMarchaSuperior && velocidadSuficiente && tiempoOk) {
                if (Math.random() > 0.15f) {  // 85% chance = variabilidad
                    marchaActual++;
                    embraguePresionado = true;
                    tiempoSiguienteCambio = 0.15f;
                    tiempoEnMarchaActual = 0f;
                }
            }
            // ===== DOWNSHIFT (bajar) - EL FIX CLAVE =====
            else {
                boolean rpmBaja = rpm < UMBRAL_RPM_DOWNSHIFT;
                boolean acelInsuficiente = aceleracionReal < ACELERACION_MINIMA_ESPERADA;
                boolean puedeBajar = marchaActual > 1 && tiempoOk;

                if (rpmBaja && acelInsuficiente && puedeBajar) {
                    // Más agresivo en 4ta/5ta
                    float prob = (marchaActual >= 4) ? 0.9f : 0.7f;
                    if (Math.random() < prob) {
                        marchaActual--;
                        embraguePresionado = true;
                        tiempoSiguienteCambio = 0.2f;
                        tiempoEnMarchaActual = 0f;
                    }
                }
            }
        }
    }

    actualizar(delta);
}