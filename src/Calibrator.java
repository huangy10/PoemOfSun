import processing.core.*;

public class Calibrator extends PApplet {
    final static float offsetX = 0;
    final static float offsetY = 0;
    final static float scaleW = 1;
    final static float scaleH = 1;

    private BodyDetector bodyDetector;

    static PVector kinectToScreen(PVector loc) {
        return loc;
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
        bodyDetector.render();
    }
}
