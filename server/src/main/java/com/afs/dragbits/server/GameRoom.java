package com.afs.dragbits.server;

import com.afs.dragbits.network.Protocol;
import com.afs.dragbits.network.AutoState;
import com.afs.dragbits.network.Input;
import com.afs.dragbits.network.PhysicsEngine;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sala de juego: gestiona el estado de la partida (máquina de estados).
 * Estados: WAITING_PLAYERS -> VEHICLE_SELECT -> COUNTDOWN -> RACING -> FINISHED -> WAITING_PLAYERS
 */
public class GameRoom {

    public enum GameState {
        WAITING_PLAYERS,
        VEHICLE_SELECT,
        COUNTDOWN,
        RACING,
        FINISHED
    }

    private final PlayerSlot[] slots = new PlayerSlot[2];
    private GameState state = GameState.WAITING_PLAYERS;

    // Carrera
    private ServerRaceController raceController;
    private final AutoState[] autos = new AutoState[2];
    private final float pistaSpawnX = 0f; // Se actualiza cuando inicia la carrera
    private final float metaX = 10000f; // Posición de meta (ajustar según pista)
    private long raceStartTime = 0;
    private long[] finishTimes = new long[2];
    private boolean[] finished = new boolean[2];

    // Countdown
    private int countdownValue = 3;
    private long countdownLastTick = 0;

    // Semáforo
    private int semaphoreState = 0; // 0=APAGADO, 1=LUZ_1, 2=LUZ_2, 3=LUZ_3, 4=VERDE, 5=SALIDA_FALSO, 6=FINALIZADO
    private float semaphoreTimer = 0f;
    private float semaphoreNextLightTime = 0f;

    // Desafíos
    private final AtomicInteger challengeIdCounter = new AtomicInteger(0);
    private Challenge pendingChallenge;

    // Resultados
    private int winnerId = -1;
    private boolean falseStart = false;

    public synchronized void playerConnected(int playerId, ClientHandler handler) {
        // Buscar slot libre
        for (int i = 0; i < 2; i++) {
            if (slots[i] == null || !slots[i].connected) {
                String name = "Player" + (playerId + 1);
                slots[i] = new PlayerSlot(playerId, name, handler);  // Usar playerId del TCPServer
                broadcastPlayerJoined(slots[i]);
                sendWelcome(i);
                checkStateTransition();
                return;
            }
        }
    }

    public synchronized void playerDisconnected(int playerId) {
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null && slots[i].id == playerId) {
                String name = slots[i].name;
                slots[i].connected = false;
                slots[i].handler = null;
                broadcastPlayerLeft(playerId, name, "DISCONNECTED");

                // Si estaba en carrera, el otro gana
                if (state == GameState.RACING || state == GameState.COUNTDOWN) {
                    handleDisconnectionDuringRace(i);
                }
                checkStateTransition();
                return;
            }
        }
    }

    private void handleDisconnectionDuringRace(int disconnectedSlot) {
        int otherSlot = 1 - disconnectedSlot;
        if (slots[otherSlot] != null && slots[otherSlot].connected) {
            // El otro jugador gana
            winnerId = slots[otherSlot].id;
            falseStart = false;
            finishTimes[otherSlot] = System.currentTimeMillis() - raceStartTime;
            finishTimes[disconnectedSlot] = -1;
            finished[otherSlot] = true;
            finished[disconnectedSlot] = true;
            state = GameState.FINISHED;
            broadcastRaceResult();
            // Volver a WAITING_PLAYERS después de 5s
            new Thread(() -> {
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                resetRace();
            }).start();
        }
    }

    public synchronized void handleJoin(int playerId, String playerName, int selectedCar, ClientHandler handler) {
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null && slots[i].id == playerId) {
                slots[i].name = playerName;
                slots[i].handler = handler;
                slots[i].connected = true;
                sendWelcome(i);
                broadcastPlayerJoined(slots[i]);
                return;
            }
        }
    }

    public synchronized void handleInput(int playerId, Protocol.InputMessage inputMsg) {
        if (state != GameState.RACING) return;

        int slotIndex = findSlotByPlayerId(playerId);
        if (slotIndex == -1) return;

        Input input = new Input(
                inputMsg.accelerator,
                inputMsg.clutch,
                inputMsg.leverX,
                inputMsg.leverY,
                inputMsg.nitro
        );

        // La física se simula en el loop fijo de ServerRaceController
        raceController.queueInput(slotIndex, input);
    }

    public synchronized void handleReady(int playerId) {
        // Permitir READY en WAITING_PLAYERS (test rápido) o VEHICLE_SELECT
        if (state != GameState.VEHICLE_SELECT && state != GameState.WAITING_PLAYERS) return;

        int slotIndex = findSlotByPlayerId(playerId);
        if (slotIndex == -1) return;

        System.out.println("[GameRoom] handleReady playerId=" + playerId + " slotIndex=" + slotIndex + " state=" + state);

        // En test rápido (WAITING_PLAYERS), asignar CHALLENGER automáticamente
        if (state == GameState.WAITING_PLAYERS) {
            System.out.println("[GameRoom] slots[0]=" + (slots[0] != null ? slots[0].id : "null") + " slots[1]=" + (slots[1] != null ? slots[1].id : "null"));
            if (slots[0] != null && slots[1] != null) {
                state = GameState.VEHICLE_SELECT;
                slots[0].selectedCar = 2; // CHALLENGER
                slots[1].selectedCar = 2; // CHALLENGER (mismo auto, solo visual)
                broadcastStateChange();
                System.out.println("[GameRoom] Cambiado a VEHICLE_SELECT, autos asignados");
            }
        }

        slots[slotIndex].ready = true;
        broadcastPlayerReady(slotIndex);
        System.out.println("[GameRoom] Jugador " + slotIndex + " listo, ready=" + slots[slotIndex].ready);

        // Verificar si ambos listos
        if (slots[0] != null && slots[1] != null &&
            slots[0].ready && slots[1].ready &&
            slots[0].selectedCar != -1 && slots[1].selectedCar != -1) {
            System.out.println("[GameRoom] Ambos listos, iniciando countdown");
            startCountdown();
        }
    }

    public synchronized void handleChallenge(int fromPlayerId, int targetPlayerId) {
        if (state != GameState.WAITING_PLAYERS && state != GameState.VEHICLE_SELECT) return;

        int fromSlot = findSlotByPlayerId(fromPlayerId);
        int targetSlot = findSlotByPlayerId(targetPlayerId);
        if (fromSlot == -1 || targetSlot == -1) return;

        int challengeId = challengeIdCounter.incrementAndGet();
        pendingChallenge = new Challenge(challengeId, fromSlot, targetSlot);

        // Enviar al desafiado
        Protocol.ChallengeReceivedMessage msg = new Protocol.ChallengeReceivedMessage();
        msg.challengeId = challengeId;
        msg.fromPlayerId = fromPlayerId;
        msg.fromPlayerName = slots[fromSlot].name;
        slots[targetSlot].handler.send(msg);
    }

    public synchronized void handleChallengeResponse(int playerId, int challengeId, boolean accepted) {
        if (pendingChallenge == null || pendingChallenge.id != challengeId) return;
        if (pendingChallenge.targetSlot != findSlotByPlayerId(playerId)) return;

        int fromSlot = pendingChallenge.fromSlot;
        int targetSlot = pendingChallenge.targetSlot;

        Protocol.ChallengeResultMessage result = new Protocol.ChallengeResultMessage();
        result.challengeId = challengeId;
        result.accepted = accepted;

        slots[fromSlot].handler.send(result);

        if (accepted) {
            // Ambos van a VEHICLE_SELECT
            state = GameState.VEHICLE_SELECT;
            slots[fromSlot].ready = false;
            slots[targetSlot].ready = false;
            slots[fromSlot].selectedCar = -1;
            slots[targetSlot].selectedCar = -1;
            broadcastStateChange();
        }
        pendingChallenge = null;
    }

    public synchronized void handleLeave(int playerId, String reason) {
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null && slots[i].id == playerId) {
                String name = slots[i].name;
                slots[i].connected = false;
                slots[i].handler = null;
                broadcastPlayerLeft(playerId, name, reason);
                checkStateTransition();
                return;
            }
        }
    }

    public synchronized void handleRejoin(int playerId, String playerName, ClientHandler handler) {
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null && slots[i].id == playerId) {
                slots[i].name = playerName;
                slots[i].handler = handler;
                slots[i].connected = true;
                sendWelcome(i);
                broadcastPlayerJoined(slots[i]);
                return;
            }
        }
    }

    public int getConnectedPlayersCount() {
        int count = 0;
        for (PlayerSlot slot : slots) {
            if (slot != null && slot.connected) count++;
        }
        return count;
    }

    private int findSlotByPlayerId(int playerId) {
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null && slots[i].id == playerId) return i;
        }
        return -1;
    }

    private void sendWelcome(int slotIndex) {
        PlayerSlot slot = slots[slotIndex];
        Protocol.WelcomeMessage welcome = new Protocol.WelcomeMessage();
        welcome.playerId = slot.id;
        welcome.playerName = slot.name;
        welcome.isHost = (slotIndex == 0);
        welcome.gameState = state.name();

        Protocol.PlayerInfo[] players = new Protocol.PlayerInfo[2];
        for (int i = 0; i < 2; i++) {
            if (slots[i] != null) {
                players[i] = slots[i].toPlayerInfo();
            } else {
                Protocol.PlayerInfo empty = new Protocol.PlayerInfo();
                empty.id = i;
                empty.name = "";
                empty.selectedCar = -1;
                empty.ready = false;
                empty.connected = false;
                players[i] = empty;
            }
        }
        welcome.players = players;
        slot.handler.send(welcome);
    }

    private void broadcastPlayerJoined(PlayerSlot slot) {
        Protocol.PlayerJoinedMessage msg = new Protocol.PlayerJoinedMessage();
        msg.player = slot.toPlayerInfo();
        broadcast(msg);
    }

    private void broadcastPlayerLeft(int playerId, String playerName, String reason) {
        Protocol.PlayerLeftMessage msg = new Protocol.PlayerLeftMessage();
        msg.playerId = playerId;
        msg.playerName = playerName;
        msg.reason = reason;
        broadcast(msg);
    }

    private void broadcastPlayerReady(int slotIndex) {
        Protocol.PlayerReadyMessage msg = new Protocol.PlayerReadyMessage();
        msg.playerId = slots[slotIndex].id;
        msg.selectedCar = slots[slotIndex].selectedCar;
        broadcast(msg);
    }

    private void broadcastStateChange() {
        for (PlayerSlot slot : slots) {
            if (slot != null && slot.connected) {
                Protocol.WelcomeMessage welcome = new Protocol.WelcomeMessage();
                welcome.playerId = slot.id;
                welcome.playerName = slot.name;
                welcome.isHost = (slot.id == 0);
                welcome.gameState = state.name();
                Protocol.PlayerInfo[] players = new Protocol.PlayerInfo[2];
                for (int i = 0; i < 2; i++) {
                    if (slots[i] != null) players[i] = slots[i].toPlayerInfo();
                }
                welcome.players = players;
                slot.handler.send(welcome);
            }
        }
    }

    private void checkStateTransition() {
        int connected = getConnectedPlayersCount();
        if (connected < 2) {
            state = GameState.WAITING_PLAYERS;
            pendingChallenge = null;
        }
    }

    private void startCountdown() {
        state = GameState.COUNTDOWN;
        countdownValue = 3;
        countdownLastTick = System.currentTimeMillis();

        // Iniciar semáforo
        semaphoreState = 0; // APAGADO
        semaphoreTimer = 0f;
        semaphoreNextLightTime = 0.5f; // Primera luz a los 0.5s

        broadcastCountdown();
    }

    private void broadcastCountdown() {
        Protocol.RaceStartCountdownMessage msg = new Protocol.RaceStartCountdownMessage();
        msg.count = countdownValue;
        broadcast(msg);
    }

    /**
     * Loop principal de la sala - se llama desde DragBitsServer cada ~16ms (60Hz)
     */
    public void update(float delta) {
        switch (state) {
            case COUNTDOWN:
                updateCountdown(delta);
                break;
            case RACING:
                updateRace(delta);
                break;
            case FINISHED:
                // Esperar 5s y volver a WAITING_PLAYERS
                break;
        }
    }

    private void updateCountdown(float delta) {
        long now = System.currentTimeMillis();
        if (now - countdownLastTick >= 1000) {
            countdownLastTick = now;
            countdownValue--;

            if (countdownValue >= 0) {
                broadcastCountdown();
            }

            if (countdownValue < 0) {
                // Countdown terminado -> iniciar semáforo normal
                startSemaphore();
            }
        }
    }

    private void startSemaphore() {
        // Semáforo estilo juego: luces aleatorias luego VERDE
        semaphoreState = 0; // APAGADO
        semaphoreTimer = 0f;
        semaphoreNextLightTime = (float) (Math.random() * 0.5 + 0.3); // 0.3-0.8s

        // Iniciar carrera física
        startRace();
    }

    private void startRace() {
        state = GameState.RACING;

        // Configurar autos con CHALLENGER base (igual para ambos)
        float spawnX = pistaSpawnX;
        float y0 = 300f; // Y jugador 0
        float y1 = 500f; // Y jugador 1

        for (int i = 0; i < 2; i++) {
            autos[i] = PhysicsEngine.createOnlineChallenger(spawnX, i == 0 ? y0 : y1);
            autos[i].id = i;
        }

        raceController = new ServerRaceController(autos, metaX);
        raceStartTime = System.currentTimeMillis();
        Arrays.fill(finished, false);
        Arrays.fill(finishTimes, -1L);
        winnerId = -1;
        falseStart = false;
    }

    private void updateRace(float delta) {
        // Actualizar semáforo
        updateSemaphore(delta);

        // Simular física (fixed timestep 60Hz)
        raceController.step(delta);

        // Verificar meta
        for (int i = 0; i < 2; i++) {
            if (!finished[i] && autos[i].getFrontX() >= metaX) {
                finished[i] = true;
                finishTimes[i] = System.currentTimeMillis() - raceStartTime;
            }
        }

        // Enviar STATE_UPDATE
        broadcastStateUpdate();

        // Verificar fin de carrera
        if (finished[0] && finished[1]) {
            endRace();
        }
    }

    private void updateSemaphore(float delta) {
        if (semaphoreState == 6) return; // FINALIZADO

        semaphoreTimer += delta;

        if (semaphoreState == 4) { // VERDE
            if (semaphoreTimer >= 1.0f) {
                semaphoreState = 6; // FINALIZADO
            }
            return;
        }

        if (semaphoreState == 5) { // SALIDA_FALSO
            if (semaphoreTimer >= 1.5f) {
                // Reiniciar semáforo (no debería pasar en online, el servidor controla)
                semaphoreState = 0;
                semaphoreTimer = 0f;
                semaphoreNextLightTime = (float) (Math.random() * 0.5 + 0.3);
            }
            return;
        }

        if (semaphoreTimer >= semaphoreNextLightTime) {
            semaphoreTimer = 0f;
            semaphoreNextLightTime = (float) (Math.random() * 0.5 + 0.3);

            switch (semaphoreState) {
                case 0: semaphoreState = 1; break; // LUZ_1
                case 1: semaphoreState = 2; break; // LUZ_2
                case 2: semaphoreState = 3; break; // LUZ_3
                case 3: semaphoreState = 4; break; // VERDE
            }
        }
    }

    private void broadcastStateUpdate() {
        Protocol.StateUpdateMessage msg = new Protocol.StateUpdateMessage();
        msg.serverTime = System.currentTimeMillis();
        msg.autos = new Protocol.NetworkAutoState[2];
        for (int i = 0; i < 2; i++) {
            msg.autos[i] = new Protocol.NetworkAutoState(
                    autos[i].id,
                    autos[i].posX,
                    autos[i].velocity,
                    autos[i].rpm,
                    autos[i].gear,
                    autos[i].nitroRemaining,
                    autos[i].state,
                    autos[i].spinning
            );
        }
        msg.semaphore = new Protocol.SemaphoreState(semaphoreState, semaphoreNextLightTime - semaphoreTimer);
        msg.raceActive = true;
        broadcast(msg);
    }

    private void endRace() {
        state = GameState.FINISHED;

        // Determinar ganador
        if (finishTimes[0] > 0 && finishTimes[1] > 0) {
            winnerId = (finishTimes[0] <= finishTimes[1]) ? slots[0].id : slots[1].id;
        } else if (finishTimes[0] > 0) {
            winnerId = slots[0].id;
        } else if (finishTimes[1] > 0) {
            winnerId = slots[1].id;
        } else {
            winnerId = -1;
        }

        broadcastRaceResult();

        // Resetear para nueva carrera después de 5s
        new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            synchronized (this) {
                resetRace();
            }
        }).start();
    }

    private void broadcastRaceResult() {
        Protocol.RaceResultMessage msg = new Protocol.RaceResultMessage();
        msg.winnerId = winnerId;
        msg.times = finishTimes.clone();
        msg.falseStart = falseStart;
        msg.playerNames = new String[]{slots[0] != null ? slots[0].name : "", slots[1] != null ? slots[1].name : ""};
        broadcast(msg);
    }

    private void resetRace() {
        state = GameState.WAITING_PLAYERS;
        for (PlayerSlot slot : slots) {
            if (slot != null) {
                slot.ready = false;
                slot.selectedCar = -1;
            }
        }
        raceController = null;
        Arrays.fill(autos, null);
        pendingChallenge = null;
        broadcastStateChange();
    }

    private void broadcast(Protocol.Message msg) {
        for (PlayerSlot slot : slots) {
            if (slot != null && slot.connected && slot.handler != null) {
                slot.handler.send(msg);
            }
        }
    }

    private static class Challenge {
        final int id;
        final int fromSlot;
        final int targetSlot;

        Challenge(int id, int fromSlot, int targetSlot) {
            this.id = id;
            this.fromSlot = fromSlot;
            this.targetSlot = targetSlot;
        }
    }
}