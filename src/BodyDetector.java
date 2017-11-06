import processing.core.*;
import org.openkinect.processing.*;
import blobDetection.*;

import java.util.ArrayList;

public class BodyDetector {
    final static float MIN_BLOB_AREA_NORMALIZED = 0.02f;
    private Kinect kinect;
    private BlobDetection blobDetection;
    private PImage depthImage;
    private PApplet sk;
    private ArrayList<BodyTarget> targets;

    private float blobDetectionThreshold = 0.6f;

    // render options
    boolean showDepthImage = true;
    boolean showBlob = true;

    // debug options
    private boolean enableDebug = false;

    BodyDetector(PApplet sk) {
        this.sk = sk;
        initKinect();
        initBlobDetection();

        targets = new ArrayList<>();
        depthImage = new PImage(kinect.width, kinect.height);
    }

    private void initKinect() {
        kinect = new Kinect(sk);
        kinect.initDepth();
        kinect.enableMirror(true);
    }

    private void initBlobDetection() {
        blobDetection = new BlobDetection(kinect.width, kinect.height);
        // true for bright area
        blobDetection.setPosDiscrimination(true);
        blobDetection.setThreshold(blobDetectionThreshold);
    }

    public void setBlobDetectionThreshold(float blobDetectionThreshold) {
        this.blobDetectionThreshold = blobDetectionThreshold;
        blobDetection.setThreshold(blobDetectionThreshold);
    }

    void update() {
        if (enableDebug) {
            debugUpdate();
            return;
        }
        int[] rawDepthData = kinect.getRawDepth();
        for (int i = 0; i < rawDepthData.length; i += 1) {
            depthImage.pixels[i] = sk.lerpColor(sk.color(255), sk.color(0),
                    rawDepthData[i] / 2048f);
        }
        depthImage.updatePixels();
        blobDetection.computeBlobs(depthImage.pixels);
        processBlobs();
    }

    private void debugUpdate() {
        BodyTarget t;
        if (targets.isEmpty()) {
            t = new BodyTarget(new PVector(sk.mouseX, sk.mouseY), sk);
            targets.add(t);
        } else {
            t = targets.get(0);
        }
        t.updateLocation(sk.mouseX, sk.mouseY);
    }

    void render() {
        if (showDepthImage)
            sk.image(depthImage, 0, 0);
        if (showBlob) {
            PVector pos;
            sk.fill(255, 0, 0);
            sk.noStroke();
            for (BodyTarget t: targets) {
                if (!t.isVisible()) continue;
                pos = t.getCurrentScreenLocation();
                sk.ellipse(pos.x, pos.y, 10, 10);
            }
        }
    }

    private void processBlobs() {
        Blob b;
        for (int i = 0; i < blobDetection.getBlobNb(); i += 1) {
            b = blobDetection.getBlob(i);
            if (b.w * b.h < MIN_BLOB_AREA_NORMALIZED) continue;
            boolean accepted = false;
            for (BodyTarget t : targets) {
                if (t.updateLocation(b.x * kinect.width, b.y * kinect.height)) {
                    accepted = true;
                    break;
                }
            }
            if (!accepted) {
                targets.add(newTargetFromBlob(b));
            }
        }

        weepOutOldTargets();
    }

    private BodyTarget newTargetFromBlob(Blob b) {
        PApplet.println("Add new target");
        return new BodyTarget(new PVector(b.x * kinect.width, b.y * kinect.height), sk);
    }

    private void weepOutOldTargets() {
        ArrayList<BodyTarget> remove = new ArrayList<>();
        for (BodyTarget t : targets)
            if (!t.isAlive())
                remove.add(t);
        if (remove.size() != 0) PApplet.println("Remove: " + remove.size());
        targets.removeAll(remove);
    }

    ArrayList<BodyTarget> getTargets() {
        return targets;
    }

    void disableRendering() {
        showBlob = false;
        showDepthImage = false;
    }

    void setEnableDebug() {
        enableDebug = true;
        PApplet.println("Body detector enable debug mode");
    }
}
