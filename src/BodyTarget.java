import processing.core.*;

import java.util.ArrayList;

public class BodyTarget {
    final static int MAX_MOVE_INTER_FRAME = 30;
    final static int MAX_DISAPPEAR_INTERVAL = 5;
    final static int MIN_CATCH_COUNT = 5;

    private PVector loc;
    private PApplet sk;

    float dir = -4;

    private long    birthFrame = 0;
    private long    lastUpdateFrame = 0;
    private int     catchCount = 0;

    private ArrayList<PVector>  historyLoc;
    private int                 historyCapacity = 30;
    private int                 staticThreshold = 40;

    BodyTarget(PVector initialLoc, PApplet sk) {
        this.birthFrame = sk.frameCount;
        this.lastUpdateFrame = sk.frameCount;
        this.loc = initialLoc;
        this.sk = sk;

        historyLoc = new ArrayList<>();
    }

    boolean updateLocation(float x, float y) {
        PVector newLoc = new PVector(x, y);
        if (newLoc.dist(loc) > MAX_MOVE_INTER_FRAME) return false;
        recordHistoryLocation(loc);
        loc = newLoc;

        lastUpdateFrame = sk.frameCount;
        catchCount += 1;
        return true;
    }

    void updateLocationForced(float x, float y) {
        recordHistoryLocation(loc.copy());
        loc.x = x;
        loc.y = y;

        lastUpdateFrame = sk.frameCount;
        catchCount += 1;
    }

    private void recordHistoryLocation(PVector oldLoc) {
        if (historyLoc.size() >= historyCapacity) {
            historyLoc.remove(0);
        }

        historyLoc.add(oldLoc);
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

    boolean isStatic() {
        // Check the location within the latest frames
        float maxX = 0;
        float maxY = 0;
        float minX = 100000;
        float minY = 100000;
        if (historyLoc.size() == 0) return false;

        for (PVector p: historyLoc) {
            if (p.x > maxX) maxX = p.x;
            else if (p.x < minX) minX = p.x;

            if (p.y > maxY) maxY = p.y;
            else if (p.y < minY) minY = p.y;
        }

        return maxX - minX < staticThreshold || maxY - minY < staticThreshold;
    }
}
