import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import processing.core.*;
import processing.opengl.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class FireFlare {
    private Sketch sk;
    float max_ring_size = 200;
    PGraphics pGraphics;
    private PVector center;
    float   ringTransparency = 0;
    // variales adopted from AdvancedGL class
    PShader shader;
    FloatBuffer   ringBuffer;
    int     ringLoc;
    int     ringVboId;
    int     partNum;
    ArrayList<Integer> partNumbers;

    PJOGL pgl;
    GL2ES2 gl;

    IntBuffer vbos;

    // bind status
    boolean isActivate = false;
    float   drawingRingLayer = 0;
    BodyTarget t;
    int id;
    float randomRotate;

    FireFlare(Sketch sk) {
        this.sk = sk;
        pGraphics = sk.createGraphics(sk.width, sk.height, PApplet.P3D);
        // load shader
        shader = pGraphics.loadShader("frag.glsl", "vert.glsl");

        partNumbers = calculateParticleNumbers();
        partNum = 0;
        for (int x: partNumbers) {
            partNum += x;
        }
        // generate buffers
        ringBuffer = allocateDirectFloatBuffer(partNum * 4);

        // fill ring buffer data
        float angle = 0, radius = 1, offset = 0, idx = 0;
        for (int i : partNumbers) {
            for (int j = 0; j < i; j += 1) {
                idx = j;
                angle = idx / i * PApplet.TWO_PI;
                ringBuffer.put(angle);
                ringBuffer.put(radius);
                ringBuffer.put(offset);
                ringBuffer.put(idx);
            }
            radius *= 1.005f;
            offset += 0.006f;
        }

        ringBuffer.rewind();

        pGraphics.beginDraw();
        pgl = (PJOGL) pGraphics.beginPGL();
        gl = pgl.gl.getGL2ES2();
        // generate vbos
        vbos = IntBuffer.allocate(1);
        gl.glGenBuffers(1, vbos);
        ringVboId = vbos.get(0);

        // get the location of the attribute variables
        shader.bind();
        ringLoc = gl.glGetAttribLocation(shader.glProgram, "ring");
        shader.unbind();

        pGraphics.endPGL();
        pGraphics.endDraw();
        //
        randomRotate = sk.random(PConstants.TWO_PI);
    }

    void setCenter(PVector center) {
        setCenter(center.x, center.y);
    }

    void setCenter(float x, float y) {
        if (this.center == null)
            this.center = new PVector(x, y);
        else {
            this.center.x = x;
            this.center.y = y;
        }
    }

    void render() {
        if (!isActivate && ringTransparency < 0.01f) {
            drawingRingLayer = 0;
//            PApplet.println("disappear");
            return;
        } else if (!isActivate) {
//            PApplet.println("fade");
            ringTransparency -= 0.01f;
        }
        pGraphics.beginDraw();
        pgl = (PJOGL) pGraphics.beginPGL();
        gl = pgl.gl.getGL2ES2();

        pGraphics.clear();
        pGraphics.translate(center.x, center.y);
//        pGraphics.rotateX(randomRotate);
        pGraphics.scale(1.5f);

        shader.bind();
        shader.set("time", sk.t);
        shader.set("ringTrans", ringTransparency);
        shader.set("randomRotate", randomRotate);

        gl.glEnableVertexAttribArray(ringLoc);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ringVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, partNum * 4 * Float.BYTES, ringBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(ringLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        int x = 0;
        int j = 0;
        for (int i: partNumbers) {
            if (j > drawingRingLayer) break;
            gl.glDrawArrays(GL.GL_POINTS, x, i);
            x += i;
            j += 1;
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableVertexAttribArray(ringLoc);

        shader.unbind();
        pGraphics.endPGL();
        pGraphics.endDraw();

        sk.image(pGraphics, 0, 0);


        if (drawingRingLayer < partNumbers.size()) {
            if (drawingRingLayer < 600)
                drawingRingLayer += 10;
            else
                drawingRingLayer += 2;
        }
    }

    void deactivate() {
        isActivate = false;
        t.fireFlare = null;
        t = null;
//        drawingRingLayer = 0;

        PApplet.println("Deactivated: " + id);
    }

    void makeActivate() {
        isActivate = true;
        ringTransparency = 1;
        drawingRingLayer = 0;
    }

    boolean isFree() {
        return !isActivate && ringTransparency < 0.01f;
    }

    void bindToBodyTraget(BodyTarget bodyTarget) {
        t = bodyTarget;
        bodyTarget.fireFlare = this;
    }

    private ArrayList<Integer> calculateParticleNumbers() {
        ArrayList<Integer> res = new ArrayList<>();
        float s = 1;
        int nPoints;
        while (s < max_ring_size) {
            nPoints = (int) (PApplet.TWO_PI * s);
            nPoints = PApplet.min(nPoints, 500);
            res.add(nPoints);
            s *= 1.005f;
        }
        return res;
    }


    private  FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
}
