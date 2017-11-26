import processing.core.*;
import ddf.minim.*;

public class Sketch extends PApplet {

    BodyDetector bodyDetector;
    VisualDisplayer visualDisplayer;
    AudioPlayer player;
    Minim minim;
    float t = 0;
    final boolean ENABLE_FULL_SCREEN = false;

    @Override
    public void settings() {
        if (ENABLE_FULL_SCREEN) {
            fullScreen(P3D, SPAN);
        } else {
            size(800, 800, P3D);
        }
//        size(800, 800, P3D);
//        fullScreen(P3D, SPAN);
    }

    @Override
    public void setup() {
//        frame.setResizable(true);
        bodyDetector = new BodyDetector(this);
        bodyDetector.disableRendering();
//        bodyDetector.showBlob = true;
        bodyDetector.setBlobDetectionThreshold(0.5f);
//        bodyDetector.setEnableDebug();

        visualDisplayer = new VisualDisplayer(this);
        visualDisplayer.setup();

        minim = new Minim(this);
        player = minim.loadFile("bg.wav", 2048);
        player.loop();
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
    public void exit() {
        player.close();
        super.exit();
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
