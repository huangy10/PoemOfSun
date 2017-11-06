import processing.core.*;

public class VisualDisplayer {

    private Sketch sk;

    private Ground ground;

    VisualDisplayer(Sketch sk) {
        this.sk = sk;
        ground = new Ground(sk, 20);
    }

    void setup() {
        sk.ellipseMode(PApplet.CENTER);
        sk.stroke(255, 0, 0);
        sk.fill(0);
    }

    void update() {
        ground.update();
    }

    void render() {
        sk.background(0);
        ground.render();
    }
}
