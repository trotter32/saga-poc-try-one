package io.github.sagapoctryone.service.movement;


public abstract class GenericMovementReceiver extends MovementReceiver {

    public String getType() {
        return "movement";
    }
}
