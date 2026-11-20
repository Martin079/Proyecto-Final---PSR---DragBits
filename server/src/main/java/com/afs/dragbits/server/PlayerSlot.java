package com.afs.dragbits.server;

import com.afs.dragbits.network.Protocol;

/**
 * Representa un slot de jugador en la sala.
 */
public class PlayerSlot {

    public final int id;
    public String name;
    public int selectedCar = -1; // 0=GOL, 1=TOYOTA, 2=CHALLENGER
    public boolean ready = false;
    public boolean connected = true;
    public ClientHandler handler;

    public PlayerSlot(int id, String name, ClientHandler handler) {
        this.id = id;
        this.name = name;
        this.handler = handler;
    }

    public Protocol.PlayerInfo toPlayerInfo() {
        Protocol.PlayerInfo info = new Protocol.PlayerInfo();
        info.id = id;
        info.name = name;
        info.selectedCar = selectedCar;
        info.ready = ready;
        info.connected = connected;
        return info;
    }

    public void reset() {
        this.selectedCar = -1;
        this.ready = false;
    }
}