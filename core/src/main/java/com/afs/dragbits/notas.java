package com.afs.dragbits;

/*------------------------------
Solo para dejar un par de anotaciones para tener en cuenta
----------------------------------*/


public class notas {

    /*posibles Valores de mejora aproximados para cada nivel
    *
    * Nivel 1: Auto Base de Inicio (Sin mejoras en la tienda).
    * Nivel 2: Aceleración I (-0.15s en 0-100 km/h) | Tracción I (-10% patinaje inicial).
    * Nivel 3: Velocidad Máxima I (+3 km/h a la V-Max).
    *  Nivel 4: Caja de Cambios I (+5% margen de tiempo para cambios perfectos).
    * Nivel 5: Nitro I (Desbloqueo del sistema de nitro: 1 carga de 2 segundos de duración).
    *  Nivel 6: Aceleración II (-0.20s en 0-100 km/h) | Potencia del Nitro I (+5% empuje al activar nitro).
    *  Nivel 7: Tracción II (-15% patinaje inicial).
    *  Nivel 8: Velocidad Máxima II (+4 km/h a la V-Max) | Nitro II (+1 segundo de duración total).
    *  Nivel 9: Caja de Cambios II (+5% margen de tiempo para cambios perfectos).
    *  Nivel 10: Aceleración III (-0.25s en 0-100 km/h) | Potencia del Nitro II (+8% empuje al activar nitro).
    *   Nivel 11: Sin mejoras desbloqueadas (Nivel de transición/ahorro de dinero).
    * Nivel 12: Tracción III (-20% patinaje inicial) | Velocidad Máxima III (+5 km/h a la V-Max).
    *  Nivel 13: Nitro III (+1.5 segundos de duración total).
    *  Nivel 14: Aceleración IV (-0.30s en 0-100 km/h) | Caja de Cambios III (+8% margen de tiempo para cambios perfectos).
    * Nivel 15: Potencia del Nitro III (+10% empuje al activar nitro).
    *  Nivel 16: Velocidad Máxima IV (+6 km/h a la V-Max) | Tracción IV (-25% patinaje inicial).
    *   Nivel 17: Sin mejoras desbloqueadas (Nivel exigente previo al tramo final).
    * Nivel 18: Aceleración V (-0.35s en 0-100 km/h) | Nitro IV (Carga máxima / +2 segundos de duración).
    *  Nivel 19: Potencia del Nitro IV (+12% empuje al activar nitro) | Caja de Cambios IV (+10% margen de tiempo para cambios perfectos).
    *  Nivel 20: Velocidad Máxima V (Élite) (+8 km/h a la V-Max) | Tracción V (Élite) (Patinaje casi nulo en largada).
    *
    * */


    /*posibles estadisticas para cada auto y recompensas/costo
    *
    *
    *Multiplicadores de Dificultad y Recompensas
    *
    * Bonus de Primera Victoria: +50% adicional de recompensa sobre la base elegida.
    *
    * Fácil: Stats del bot x1.0 | Recompensa x1.0
    * Medio: Stats del bot x1.05 | Recompensa x1.2
    * Difícil: Stats del bot x1.10 | Recompensa x1.6
    * Autos del Jugador
    * Auto 1 (Hatchback Base): Gratuito | Nivel 1 | $160\text{ km/h}$ V-Max | $9.5\text{ s}$ (0-100) | $60\%$ Tracción | Sin Nitro
    *
    * Auto 2 (Coupe Sport): $\$15,000$ | Nivel 7 | $205\text{ km/h}$ V-Max | $6.8\text{ s}$ (0-100) | $75\%$ Tracción | Nitro Incluido (1 carga, 2s)
    * Auto 3 (Supercar): $\$45,000$ | Nivel 14 | $255\text{ km/h}$ V-Max | $4.5\text{ s}$ (0-100) | $85\%$ Tracción | Nitro Incluido (2 cargas, 3s)
    *
    *
    *
    * Carreras Legales (Recompensa: DINERO - $)
    * Rival 1 (Principiante)
    * Exigencia: Auto 1 Stock
    * Stats Bot: $155\text{ km/h}$ V-Max | $9.8\text{ s}$ (0-100) | Sin Nitro
    * Recompensas: Fácil $\$800$ | Medio $\$960$ | Difícil $\$1,280$
    *
    * Rival 2 (Amateur)
    * Exigencia: Auto 1 Full mejorado o Auto 2 Stock
    * Stats Bot: $185\text{ km/h}$ V-Max | $7.8\text{ s}$ (0-100) | Sin Nitro
    * Recompensas: Fácil $\$2,200$ | Medio $\$2,640$ | Difícil $\$3,520$
    *
    * Rival 3 (Callejero)
    * Exigencia: Auto 2 con MejorasStats Bot: $210\text{ km/h}$ V-Max | $6.2\text{ s}$ (0-100) | Nitro (2s)
    * Recompensas: Fácil $\$5,000$ | Medio $\$6,000$ | Difícil $\$8,000$
    *
    * Rival 4 (Pro-Racer)
    * Exigencia: Auto 2 Full mejorado o Auto 3 StockStats Bot: $240\text{ km/h}$ V-Max | $4.8\text{ s}$ (0-100) | Nitro (3s)
    * Recompensas: Fácil $\$10,000$ | Medio $\$12,000$ | Difícil $\$16,000$
    *
    * Rival 5 (Campeón)
    * Exigencia: Auto 3 Full Tuned
    * Stats Bot: $275\text{ km/h}$ V-Max | $3.5\text{ s}$ (0-100) | Nitro (4s)
    * Recompensas: Fácil $\$18,000$ | Medio $\$21,600$ | Difícil $\$28,800$
    *
    *
    *
    * Carreras Ilegales (Recompensa: EXPERIENCIA - XP)
    *
    * Rival 1 (Novato Nocturno)
    * Exigencia: Auto 1 Stock
    * Stats Bot: $158\text{ km/h}$ V-Max | $9.6\text{ s}$ (0-100) | Sin Nitro
    * Recompensas: Fácil $150\text{ XP}$ | Medio $180\text{ XP}$ | Difícil $240\text{ XP}$
    *
    * Rival 2 (Corredor Urbano)
    * Exigencia: Auto 1 Full mejorado o Auto 2 Stock
    * Stats Bot: $190\text{ km/h}$ V-Max | $7.5\text{ s}$ (0-100) | Sin NitroRecompensas:
    * Fácil $350\text{ XP}$ | Medio $420\text{ XP}$ | Difícil $560\text{ XP}$
    *
    * Rival 3 (Apostador)
    * Exigencia: Auto 2 con Mejoras
    * Stats Bot: $220\text{ km/h}$ V-Max | $5.9\text{ s}$ (0-100) | Nitro (2s)
    * Recompensas: Fácil $700\text{ XP}$ | Medio $840\text{ XP}$ | Difícil $1,120\text{ XP}$
    *
    * Rival 4 (El Rey de la Pista)
    * Exigencia: Auto 2 Full mejorado o Auto 3 Stock
    * Stats Bot: $250\text{ km/h}$ V-Max | $4.4\text{ s}$ (0-100) | Nitro (3.5s)
    * Recompensas: Fácil $1,200\text{ XP}$ | Medio $1,440\text{ XP}$ | Difícil $1,920\text{ XP}$
    *
    * Rival 5 (Líder de la Red)
    * Exigencia: Auto 3 Full Tuned
    * Stats Bot: $285\text{ km/h}$ V-Max | $3.2\text{ s}$ (0-100) | Nitro (5s)
    * Recompensas: Fácil $2,000\text{ XP}$ | Medio $2,400\text{ XP}$ | Difícil $3,200\text{ XP}$    *
    *
    *
    * */

}
