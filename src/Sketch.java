import processing.core.*;
import org.openkinect.freenect.*;
import org.openkinect.processing.*;
import blobDetection.*;

public class Sketch extends PApplet {

    BodyDetector bodyDetector;
    VisualDisplayer visualDisplayer;

    @Override
    public void settings() {
        size(640, 480);
    }

    @Override
    public void setup() {
        bodyDetector = new BodyDetector(this);
        bodyDetector.disableRendering();
        bodyDetector.setEnableDebug();

        visualDisplayer = new VisualDisplayer(this);
        visualDisplayer.setup();
        frameRate(30);
    }

    @Override
    public void draw() {
        background(255);
        bodyDetector.update();
        //
        visualDisplayer.update();
        //
        bodyDetector.render();
        visualDisplayer.render();
        surface.setTitle("Framerate: " + frameRate);
    }
}
