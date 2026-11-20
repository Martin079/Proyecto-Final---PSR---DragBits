package com.afs.dragbits.network;

public final class Input {

    public boolean accelerator;
    public boolean clutch;
    public int leverX; // -1, 0, 1
    public int leverY; // -1, 0, 1
    public boolean nitro;

    public Input() {}

    public Input(boolean accelerator, boolean clutch, int leverX, int leverY, boolean nitro) {
        this.accelerator = accelerator;
        this.clutch = clutch;
        this.leverX = leverX;
        this.leverY = leverY;
        this.nitro = nitro;
    }

    public Input copy() {
        return new Input(accelerator, clutch, leverX, leverY, nitro);
    }

    public boolean hasGearChange() {
        return clutch && (leverX != 0 || leverY != 0);
    }

    @Override
    public String toString() {
        return "Input{acc=" + accelerator + ", clutch=" + clutch + ", lever=(" + leverX + "," + leverY + "), nitro=" + nitro + "}";
    }
}