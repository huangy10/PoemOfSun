import processing.core.*;

public class Calibrator extends PApplet {
    final static float offsetX = 0;
    final static float offsetY = 0;
    final static float scaleW = 1;
    final static float scaleH = 1;
    static float screenWidth;
    static float screenHeight;

    private BodyDetector bodyDetector;

    static void initWithSketch(PApplet sk) {
        screenWidth = sk.width;
        screenHeight = sk.height;
    }

    static PVector kinectToScreen(PVector loc) {
        float kinectW = 640;
        float kinectH = 480;
        float y = kinectW - loc.x;
        float x = loc.y;

        return new PVector(x * 3.5f, y * 3.5f);
    }

    public void settings() {
        // this size is the same as the kinect image
        size(640, 480);
    }

    @Override
    public void setup() {
        bodyDetector = new BodyDetector(this);
        bodyDetector.setEnableCalibration();
    }

    @Override
    public void draw() {
        bodyDetector.update();
        bodyDetector.showDepthImage = true;
        bodyDetector.showBlob = true;
        bodyDetector.setBlobDetectionThreshold(0.5f);
        bodyDetector.render();
    }
}
