package util;

import org.joml.Vector3f;

public class Chunk {

    public static int size = 32;

    private boolean shouldStep = true;
    private boolean shouldStepNextFrame = true;
    private Vector3f topLeft;
    private Vector3f bottomRight;

    public Chunk(Vector3f topLeft, Vector3f bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Chunk() {

    }

    public void setTopLeft(Vector3f topLeft) {
        this.topLeft = topLeft;
    }

    public Vector3f getTopLeft() {
        return topLeft;
    }

    public void setShouldStep(boolean shouldStep) {
        this.shouldStep = shouldStep;
    }

    public boolean getShouldStep() {
        return this.shouldStep;
    }

    public void setShouldStepNextFrame(boolean shouldStepNextFrame) {
        this.shouldStepNextFrame = shouldStepNextFrame;
    }

    public boolean getShouldStepNextFrame() {
        return this.shouldStepNextFrame;
    }

    public void setBottomRight(Vector3f bottomRight) {
        this.bottomRight = bottomRight;
    }

    public Vector3f getBottomRight() {
        return bottomRight;
    }

    public void shiftShouldStepAndReset() {
        this.shouldStep = this.shouldStepNextFrame;
        this.shouldStepNextFrame = false;
    }

}
