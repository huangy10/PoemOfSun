import processing.core.*;

public class Ground {
    Sketch  sk;
    int cellSize;
    int     cellNbW, cellNbH;
    GroundNode[][]  nodes;

    int     fireFlareCacheSize = 3;
    FireFlare[] fireFlares;

    Ground(Sketch sk, int size) {
        this.sk = sk;
        this.cellSize = size;
        cellNbW = sk.width / size + 1;
        cellNbH = sk.height / size + 1;
        nodes = new GroundNode[cellNbW][cellNbH];
        for (int i = 0; i < cellNbW; i += 1)
            for (int j = 0; j < cellNbH; j += 1) {
                nodes[i][j] = new GroundNode(new PVector(i * size, j * size), sk);
            }

        fireFlares = new FireFlare[fireFlareCacheSize];
        for (FireFlare fireFlare: fireFlares) {
            fireFlare = new FireFlare(sk);
            fireFlare.isActivate = false;
        }
        for (int i = 0; i < fireFlares.length; i += 1) {
            fireFlares[i] = new FireFlare(sk);
            fireFlares[i].isActivate = false;
            fireFlares[i].id = i;
        }
    }

    void update() {
        GroundNode node;
        float dist, ratio;
        PVector dir;
        clearNodeForece();

        for (BodyTarget t : sk.bodyDetector.getTargets()) {
            PVector p = t.getCurrentScreenLocation();
            boolean isStatic = t.isStatic();
            if (isStatic && t.fireFlare == null) {
                FireFlare fireFlare = getFreeFireFlare();
                if (fireFlare != null) {
                    fireFlare.bindToBodyTraget(t);
                    fireFlare.setCenter(t.getCurrentScreenLocation());
                    fireFlare.makeActivate();
                } else {
                    PApplet.println("fail to get free fireflare");
                }
            } else if (!isStatic && t.fireFlare != null) {
                t.fireFlare.deactivate();
            }
            for (int i = 0; i < cellNbW; i += 1)
                for (int j = 0; j < cellNbH; j += 1) {
                    node = nodes[i][j];
                    dist = node.loc.dist(p);
                    ratio = 1 - dist / sk.width;
                    dir = p.copy().sub(node.loc).normalize();
                    node.f.add(dir.mult(ratio * cellSize * 5 * t.dir));
                }
        }

        applyForces();
    }

    private void clearNodeForece() {
        for (int i = 0; i < cellNbW; i += 1)
            for (int j = 0; j < cellNbH; j += 1) {
                nodes[i][j].clearForce();
            }
    }

    private void applyForces() {
        for (int i = 0; i < cellNbW; i += 1)
            for (int j = 0; j < cellNbH; j += 1) {
                nodes[i][j].applyForce();
            }
    }

    void render() {
        for (int i = 0; i < cellNbW; i += 1)
            for (int j = 0; j < cellNbH; j += 1) {
                nodes[i][j].renderNode();
//                sk.stroke(sk.noise(0, sk.t * 10) * 255,nodes[i][j].tmp.mag() * 2, nodes[i][j].f.mag() * 5);
                sk.stroke(nodes[i][j].f.mag() + 20);
                if (i > 0 && nodes[i][j].loc.dist(nodes[i - 1][j].loc) < 200)
                    sk.line(nodes[i][j].loc.x, nodes[i][j].loc.y, nodes[i - 1][j].loc.x, nodes[i - 1][j].loc.y);
                if (j > 0 && nodes[i][j].loc.dist(nodes[i][j - 1].loc) < 250)
                    sk.line(nodes[i][j].loc.x, nodes[i][j].loc.y, nodes[i][j - 1].loc.x, nodes[i][j - 1].loc.y);
            }

        for (FireFlare fireFlare: fireFlares) {
            fireFlare.render();
        }
    }

    FireFlare getFreeFireFlare() {
        for (FireFlare fireFlare: fireFlares) {
            if (fireFlare.isFree()) {
                PApplet.println("get free fireflare: " + fireFlare.id);
                return fireFlare;
            }
        }
        return null;
    }
}
