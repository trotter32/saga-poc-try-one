package io.github.sagapoctryone.service.movement;


public abstract class CompensationMovementReceiver extends MovementReceiver {

    public String getType() {
        return "compensation";
    }
}
