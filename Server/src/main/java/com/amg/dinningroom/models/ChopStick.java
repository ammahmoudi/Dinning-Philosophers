package com.amg.dinningroom.models;

public class ChopStick {
    public int id;
    public boolean inUse = false;
    public int handler;
    public boolean leftWants = false;
    public boolean rightWants = false;


    public ChopStick(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (inUse) return "false";
        return "true";
    }

    synchronized public boolean isInUse() {
        return inUse;
    }

    synchronized public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    synchronized public int getHandler() {
        return handler;
    }

    synchronized public void setHandler(int handler) {
        this.handler = handler;
    }

    synchronized public boolean isLeftWants() {
        return leftWants;
    }

    synchronized public void setLeftWants(boolean leftWants) {
        this.leftWants = leftWants;
    }

    synchronized public boolean isRightWants() {
        return rightWants;
    }

    synchronized public void setRightWants(boolean rightWants) {
        this.rightWants = rightWants;
    }
}
