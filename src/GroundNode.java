import processing.core.*;

public class GroundNode {
    PVector home;
    PVector loc;
    PVector f;
    PVector v;
    PApplet sk;

    PVector tmp;

    float resistRatio = 3f;

    GroundNode(PVector home, PApplet sk) {
        this.home = home;
        this.loc = home.copy();
        this.f = new PVector(0, 0);

        this.sk = sk;
    }

    void clearForce() {
        f.x = 0;
        f.y = 0;
        f.z = 0;
    }

    void applyForce() {
        PVector resist = loc.copy().sub(home).mult(-resistRatio);
        tmp = f.copy();
        f.add(resist);
        v = f.copy().mult(0.05f);
        loc.add(v);
    }

    void renderNode() {
        sk.noStroke();
        sk.ellipse(loc.x, loc.y, 2, 2);
//        sk.stroke(255, 0, 0 );
//        sk.line(loc.x, loc.y, home.x, home.y);
//        sk.line(loc.x, loc.y, loc.x + tmp.x, loc.y + tmp.y);
    }
}
