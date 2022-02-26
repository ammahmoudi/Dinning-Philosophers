package com.amg.dinningroom.models;

import com.amg.dinningroom.Timer;

public class Philosopher extends Thread {
    int pId;
    Integer leftC;
    Integer rightC;
    Status status=Status.WAITING_FOR_START;
    boolean leftHand=false;


    public Philosopher(int id) {
        this.pId = id;


    }

    public Integer getLeftC() {
        return leftC;
    }

    public void setLeftC(Integer leftC) {
        this.leftC = leftC;
    }

    public Integer getRightC() {
        return rightC;
    }

    public void setRightC(Integer rightC) {
        this.rightC = rightC;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isLeftHand() {
        return leftHand;
    }

    public void setLeftHand(boolean leftHand) {
        this.leftHand = leftHand;
    }


}
