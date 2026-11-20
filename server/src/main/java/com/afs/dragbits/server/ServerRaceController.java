package com.afs.dragbits.server;

import com.afs.dragbits.network.AutoState;
import com.afs.dragbits.network.Input;
import com.afs.dragbits.network.PhysicsEngine;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Controlador de carrera del servidor.
 * Simula la física a 60Hz fixed timestep para ambos autos.
 */
public class ServerRaceController {

    private static final float FIXED_TIMESTEP = 1f / 60f; // 60Hz
    private final AutoState[] autos;
    private final float metaX;

    // Colas de input por jugador (thread-safe)
    private final ConcurrentLinkedQueue<Input>[] inputQueues;

    private float accumulator = 0f;

    @SuppressWarnings("unchecked")
    public ServerRaceController(AutoState[] autos, float metaX) {
        this.autos = autos;
        this.metaX = metaX;
        this.inputQueues = new ConcurrentLinkedQueue[2];
        this.inputQueues[0] = new ConcurrentLinkedQueue<>();
        this.inputQueues[1] = new ConcurrentLinkedQueue<>();
    }

    /**
     * Encola input de un jugador (llamado desde GameRoom.handleInput - hilo de red)
     */
    public void queueInput(int playerIndex, Input input) {
        if (playerIndex >= 0 && playerIndex < 2) {
            inputQueues[playerIndex].offer(input.copy());
        }
    }

    /**
     * Avanza la simulación (llamado desde GameRoom.update - hilo principal 60Hz)
     */
    public void step(float delta) {
        accumulator += delta;

        while (accumulator >= FIXED_TIMESTEP) {
            fixedStep(FIXED_TIMESTEP);
            accumulator -= FIXED_TIMESTEP;
        }
    }

    private void fixedStep(float dt) {
        // Obtener último input de cada jugador (o input neutro si no hay)
        Input input0 = inputQueues[0].poll();
        Input input1 = inputQueues[1].poll();

        if (input0 == null) input0 = new Input();
        if (input1 == null) input1 = new Input();

        // Simular física para ambos autos
        PhysicsEngine.step(autos[0], input0, dt);
        PhysicsEngine.step(autos[1], input1, dt);
    }

    public AutoState[] getAutos() {
        return autos;
    }
}