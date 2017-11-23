import processing.core.*;
import org.openkinect.freenect.*;
import org.openkinect.processing.*;
import blobDetection.*;

public class Sketch extends PApplet {

    BodyDetector bodyDetector;
    VisualDisplayer visualDisplayer;
    float t = 0;

    @Override
    public void settings() {
        size(1280, 960, P3D);
    }

    @Override
    public void setup() {
        bodyDetector = new BodyDetector(this);
        bodyDetector.disableRendering();
        bodyDetector.setEnableDebug();

        visualDisplayer = new VisualDisplayer(this);
        visualDisplayer.setup();
        frameRate(60);
    }

    @Override
    public void draw() {
        bodyDetector.update();
        //
        visualDisplayer.update();
        //
        bodyDetector.render();
        visualDisplayer.render();
        surface.setTitle("Framerate: " + frameRate);

        t += 0.01f;
    }

    @Override
    public void mousePressed() {
//        BodyTarget t = bodyDetector.getTargets().get(0);
//        if (t.dir == 1) {
//            t.dir = -4;
//        } else {
//            t.dir = 1;
//        }
    }
}
