package com.afs.dragbits.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class Protocol {

    private Protocol() {}

    public static final Gson GSON = new GsonBuilder().create();

    // ==================== TIPOS DE MENSAJE ====================

    public static final String TYPE_JOIN = "JOIN";
    public static final String TYPE_INPUT = "INPUT";
    public static final String TYPE_READY = "READY";
    public static final String TYPE_CHALLENGE = "CHALLENGE";
    public static final String TYPE_CHALLENGE_RESPONSE = "CHALLENGE_RESPONSE";
    public static final String TYPE_LEAVE = "LEAVE";
    public static final String TYPE_PING = "PING";
    public static final String TYPE_REJOIN = "REJOIN";

    public static final String TYPE_WELCOME = "WELCOME";
    public static final String TYPE_STATE_UPDATE = "STATE_UPDATE";
    public static final String TYPE_RACE_START_COUNTDOWN = "RACE_START_COUNTDOWN";
    public static final String TYPE_RACE_RESULT = "RACE_RESULT";
    public static final String TYPE_CHALLENGE_RECEIVED = "CHALLENGE_RECEIVED";
    public static final String TYPE_CHALLENGE_RESULT = "CHALLENGE_RESULT";
    public static final String TYPE_PLAYER_JOINED = "PLAYER_JOINED";
    public static final String TYPE_PLAYER_LEFT = "PLAYER_LEFT";
    public static final String TYPE_PLAYER_READY = "PLAYER_READY";
    public static final String TYPE_ERROR = "ERROR";
    public static final String TYPE_PONG = "PONG";
    public static final String TYPE_SERVER_INFO = "SERVER_INFO";
    public static final String TYPE_DISCOVER = "DISCOVER";

    // ==================== CLASES BASE ====================

    public static abstract class Message {
        public String type;
        public long timestamp = System.currentTimeMillis();

        public Message(String type) {
            this.type = type;
        }

        public String toJson() {
            return GSON.toJson(this);
        }
    }

    /**
     * Deserializa un mensaje JSON al tipo correcto según el campo "type".
     */
    public static Message parseMessage(String json) {
        try {
            // Primero leer el tipo sin deserializar todo
            com.google.gson.JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String type = obj.get("type").getAsString();

            // Mapear tipo a clase concreta
            Class<? extends Message> clazz = getMessageClass(type);
            if (clazz != null) {
                return GSON.fromJson(json, clazz);
            }
        } catch (Exception e) {
            System.err.println("[Protocol] Error parseando mensaje: " + e.getMessage());
        }
        return null;
    }

    private static Class<? extends Message> getMessageClass(String type) {
        switch (type) {
            case TYPE_JOIN: return JoinMessage.class;
            case TYPE_INPUT: return InputMessage.class;
            case TYPE_READY: return ReadyMessage.class;
            case TYPE_CHALLENGE: return ChallengeMessage.class;
            case TYPE_CHALLENGE_RESPONSE: return ChallengeResponseMessage.class;
            case TYPE_LEAVE: return LeaveMessage.class;
            case TYPE_PING: return PingMessage.class;
            case TYPE_REJOIN: return RejoinMessage.class;
            case TYPE_DISCOVER: return DiscoverMessage.class;
            case TYPE_WELCOME: return WelcomeMessage.class;
            case TYPE_STATE_UPDATE: return StateUpdateMessage.class;
            case TYPE_RACE_START_COUNTDOWN: return RaceStartCountdownMessage.class;
            case TYPE_RACE_RESULT: return RaceResultMessage.class;
            case TYPE_CHALLENGE_RECEIVED: return ChallengeReceivedMessage.class;
            case TYPE_CHALLENGE_RESULT: return ChallengeResultMessage.class;
            case TYPE_PLAYER_JOINED: return PlayerJoinedMessage.class;
            case TYPE_PLAYER_LEFT: return PlayerLeftMessage.class;
            case TYPE_PLAYER_READY: return PlayerReadyMessage.class;
            case TYPE_ERROR: return ErrorMessage.class;
            case TYPE_PONG: return PongMessage.class;
            case TYPE_SERVER_INFO: return ServerInfoMessage.class;
            default: return null;
        }
    }

    // ==================== CLIENTE -> SERVIDOR ====================

    public static class JoinMessage extends Message {
        public String playerName;
        public int selectedCar; // 0=GOL, 1=TOYOTA, 2=CHALLENGER

        public JoinMessage() {
            super(TYPE_JOIN);
        }

        public JoinMessage(String playerName, int selectedCar) {
            super(TYPE_JOIN);
            this.playerName = playerName;
            this.selectedCar = selectedCar;
        }
    }

    public static class InputMessage extends Message {
        public boolean accelerator;
        public boolean clutch;
        public int leverX; // -1, 0, 1
        public int leverY; // -1, 0, 1
        public boolean nitro;

        public InputMessage() {
            super(TYPE_INPUT);
        }

        public InputMessage(boolean accelerator, boolean clutch, int leverX, int leverY, boolean nitro) {
            super(TYPE_INPUT);
            this.accelerator = accelerator;
            this.clutch = clutch;
            this.leverX = leverX;
            this.leverY = leverY;
            this.nitro = nitro;
        }
    }

    public static class ReadyMessage extends Message {
        public ReadyMessage() {
            super(TYPE_READY);
        }
    }

    public static class ChallengeMessage extends Message {
        public int targetPlayerId;

        public ChallengeMessage() {
            super(TYPE_CHALLENGE);
        }

        public ChallengeMessage(int targetPlayerId) {
            super(TYPE_CHALLENGE);
            this.targetPlayerId = targetPlayerId;
        }
    }

    public static class ChallengeResponseMessage extends Message {
        public int challengeId;
        public boolean accepted;

        public ChallengeResponseMessage() {
            super(TYPE_CHALLENGE_RESPONSE);
        }

        public ChallengeResponseMessage(int challengeId, boolean accepted) {
            super(TYPE_CHALLENGE_RESPONSE);
            this.challengeId = challengeId;
            this.accepted = accepted;
        }
    }

    public static class LeaveMessage extends Message {
        public LeaveMessage() {
            super(TYPE_LEAVE);
        }
    }

    public static class PingMessage extends Message {
        public long clientTime;

        public PingMessage() {
            super(TYPE_PING);
        }

        public PingMessage(long clientTime) {
            super(TYPE_PING);
            this.clientTime = clientTime;
        }
    }

    public static class RejoinMessage extends Message {
        public int playerId;
        public String playerName;

        public RejoinMessage() {
            super(TYPE_REJOIN);
        }

        public RejoinMessage(int playerId, String playerName) {
            super(TYPE_REJOIN);
            this.playerId = playerId;
            this.playerName = playerName;
        }
    }

    public static class DiscoverMessage extends Message {
        public DiscoverMessage() {
            super(TYPE_DISCOVER);
        }
    }

    // ==================== SERVIDOR -> CLIENTE ====================

    public static class WelcomeMessage extends Message {
        public int playerId;
        public String playerName;
        public boolean isHost;
        public String gameState; // WAITING_PLAYERS, VEHICLE_SELECT, COUNTDOWN, RACING, FINISHED
        public PlayerInfo[] players;

        public WelcomeMessage() {
            super(TYPE_WELCOME);
        }
    }

    public static class PlayerInfo {
        public int id;
        public String name;
        public int selectedCar; // -1 = none
        public boolean ready;
        public boolean connected;
    }

    public static class StateUpdateMessage extends Message {
        public long serverTime;
        public NetworkAutoState[] autos; // length 2
        public SemaphoreState semaphore;
        public boolean raceActive;

        public StateUpdateMessage() {
            super(TYPE_STATE_UPDATE);
        }
    }

    public static class NetworkAutoState {
        public int id; // 0 or 1
        public float posX;
        public float velocity;
        public float rpm;
        public int gear;
        public float nitroRemaining;
        public int state; // 0=ESTATICO, 1=AVANZANDO, 2=CAMBIANDO_MARCHA, 3=NITRO, 4=PATINANDO
        public boolean spinning;

        public NetworkAutoState() {}

        public NetworkAutoState(int id, float posX, float velocity, float rpm, int gear,
                         float nitroRemaining, int state, boolean spinning) {
            this.id = id;
            this.posX = posX;
            this.velocity = velocity;
            this.rpm = rpm;
            this.gear = gear;
            this.nitroRemaining = nitroRemaining;
            this.state = state;
            this.spinning = spinning;
        }
    }

    public static class SemaphoreState {
        public int state; // 0=APAGADO, 1=LUZ_1, 2=LUZ_2, 3=LUZ_3, 4=VERDE, 5=SALIDA_FALSO, 6=FINALIZADO
        public float timeRemaining;

        public SemaphoreState() {}

        public SemaphoreState(int state, float timeRemaining) {
            this.state = state;
            this.timeRemaining = timeRemaining;
        }
    }

    public static class RaceStartCountdownMessage extends Message {
        public int count; // 3, 2, 1, 0 (GO)

        public RaceStartCountdownMessage() {
            super(TYPE_RACE_START_COUNTDOWN);
        }

        public RaceStartCountdownMessage(int count) {
            super(TYPE_RACE_START_COUNTDOWN);
            this.count = count;
        }
    }

    public static class RaceResultMessage extends Message {
        public int winnerId; // -1 = empate / salida temprana
        public long[] times; // ms por jugador [id0, id1]
        public boolean falseStart;
        public String[] playerNames;

        public RaceResultMessage() {
            super(TYPE_RACE_RESULT);
        }
    }

    public static class ChallengeReceivedMessage extends Message {
        public int challengeId;
        public int fromPlayerId;
        public String fromPlayerName;

        public ChallengeReceivedMessage() {
            super(TYPE_CHALLENGE_RECEIVED);
        }
    }

    public static class ChallengeResultMessage extends Message {
        public int challengeId;
        public boolean accepted;

        public ChallengeResultMessage() {
            super(TYPE_CHALLENGE_RESULT);
        }
    }

    public static class PlayerJoinedMessage extends Message {
        public PlayerInfo player;

        public PlayerJoinedMessage() {
            super(TYPE_PLAYER_JOINED);
        }
    }

    public static class PlayerLeftMessage extends Message {
        public int playerId;
        public String playerName;
        public String reason; // "DISCONNECTED", "LEFT"

        public PlayerLeftMessage() {
            super(TYPE_PLAYER_LEFT);
        }
    }

    public static class PlayerReadyMessage extends Message {
        public int playerId;
        public int selectedCar;

        public PlayerReadyMessage() {
            super(TYPE_PLAYER_READY);
        }
    }

    public static class ErrorMessage extends Message {
        public String code;
        public String message;

        public ErrorMessage() {
            super(TYPE_ERROR);
        }

        public ErrorMessage(String code, String message) {
            super(TYPE_ERROR);
            this.code = code;
            this.message = message;
        }
    }

    public static class PongMessage extends Message {
        public long clientTime;
        public long serverTime;

        public PongMessage() {
            super(TYPE_PONG);
        }
    }

    public static class ServerInfoMessage extends Message {
        public String ip;
        public int tcpPort;
        public String serverName;
        public int playersConnected;
        public int maxPlayers;

        public ServerInfoMessage() {
            super(TYPE_SERVER_INFO);
        }
    }
}