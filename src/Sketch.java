import processing.core.*;
import org.openkinect.freenect.*;
import org.openkinect.processing.*;
import blobDetection.*;

public class Sketch extends PApplet {

    BodyDetector bodyDetector;

    @Override
    public void settings() {
        size(640, 480);
    }

    @Override
    public void setup() {
        bodyDetector = new BodyDetector(this);
        bodyDetector.disableRendering();
        bodyDetector.setEnableDebug();
        frameRate(30);
    }

    @Override
    public void draw() {
        background(255);
        bodyDetector.update();

        bodyDetector.render();
        surface.setTitle("Framerate: " + frameRate);
    }
}
