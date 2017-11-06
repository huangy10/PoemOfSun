import processing.core.*;

public class BodyTarget {
    final static int MAX_MOVE_INTER_FRAME = 30;
    final static int MAX_DISAPPEAR_INTERVAL = 5;
    final static int MIN_CATCH_COUNT = 5;

    private PVector loc;
    private PApplet sk;

    private long    birthFrame = 0;
    private long    lastUpdateFrame = 0;
    private int     catchCount = 0;

    BodyTarget(PVector initialLoc, PApplet sk) {
        this.birthFrame = sk.frameCount;
        this.lastUpdateFrame = sk.frameCount;
        this.loc = initialLoc;
        this.sk = sk;
    }

    boolean updateLocation(float x, float y) {
        PVector newLoc = new PVector(x, y);
        if (newLoc.dist(loc) > MAX_MOVE_INTER_FRAME) return false;
        loc = newLoc;

        lastUpdateFrame = sk.frameCount;
        catchCount += 1;
        return true;
    }

    void updateLocationForced(float x, float y) {
        loc.x = x;
        loc.y = y;

        lastUpdateFrame = sk.frameCount;
        catchCount += 1;
    }

    PVector getCurrentScreenLocation() {
        return Calibrator.kinectToScreen(loc);
    }

    boolean isAlive() {
        return (sk.frameCount - lastUpdateFrame) <= MAX_DISAPPEAR_INTERVAL;
    }

    boolean isVisible() {
        return catchCount > MIN_CATCH_COUNT;
    }
}
