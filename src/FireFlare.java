import processing.core.*;

public class FireFlare {
    private Sketch sk;
    float MAX_RING_SIZE = 200;
    float ringSize = 20;
    float noiseStep = 0.006f;
    PGraphics pGraphics;

    private PVector center;

    FireFlare(Sketch sk) {
        this.sk = sk;
        pGraphics = sk.createGraphics(sk.width, sk.height);
        pGraphics.beginDraw();
        pGraphics.background(0);
        pGraphics.endDraw();
    }

    public void setCenter(PVector center) {
        this.center = center;
    }

    public void setCenter(float x, float y) {
        if (center == null)
            center = new PVector(x, y);
        else {
            center.x = x;
            center.y = y;
        }
    }

    void render() {
        if (center == null) return;
        int count = 0;
        int     nPoints = 0;
        float   s = 1;
        float   xOffset = 0;
        float   yOffset = 0;
        float   offsetInc = 0.006f;
        pGraphics.beginDraw();
        pGraphics.blendMode(PConstants.ADD);
        pGraphics.noFill();
        pGraphics.stroke(255, 64, 8, 128);
        pGraphics.background(0);
        pGraphics.pushMatrix();
        pGraphics.translate(center.x, center.y);
        while (s < ringSize) {
            nPoints = (int) (2 * PApplet.PI * s);
            // limit the max number of points for performance
            nPoints = PApplet.min(nPoints, 500);
            pGraphics.beginShape();
            for (int i = 0; i < nPoints; i += 1) {
                PVector p = PVector.fromAngle((float) i / nPoints * PApplet.PI * 2);
                p.mult(sk.noise(xOffset + p.x, yOffset + p.y, sk.t) * s);
                pGraphics.vertex(p.x, p.y);
                count += 1;
            }
            pGraphics.endShape(PConstants.CLOSE);

            xOffset += offsetInc;
            yOffset += offsetInc;

            s *= 1.005f;
        }
        pGraphics.popMatrix();
        pGraphics.endDraw();
        sk.image(pGraphics, 0, 0);
        PApplet.println(count);
        if (ringSize < MAX_RING_SIZE) ringSize += 2;
    }

}
