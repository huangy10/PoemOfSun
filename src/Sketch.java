import processing.core.PApplet;
import org.openkinect.freenect.*;
import org.openkinect.processing.*;

public class Sketch extends PApplet {

    public Kinect kinect;

    @Override
    public void settings() {
        size(800, 800);
    }

    @Override
    public void setup() {
        kinect = new Kinect(this);
        initKinect();
    }

    @Override
    public void draw() {
        background(255);
        image(kinect.getDepthImage(), 0, 0);
        surface.setTitle("Framerate: " + frameRate);
    }

    private void initKinect() {
        kinect.initDepth();
        kinect.enableMirror(true);
    }
}
