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
        pGraphics.beginDraw();
        pgl = (PJOGL) pGraphics.beginPGL();
        gl = pgl.gl.getGL2ES2();

        pGraphics.background(0);
        pGraphics.translate(pGraphics.width / 2, pGraphics.height / 2);

        shader.bind();
        shader.set("time", sk.t);

        gl.glEnableVertexAttribArray(ringLoc);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ringVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, partNum * 4 * Float.BYTES, ringBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(ringLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        int x = 0;
        for (int i: partNumbers) {
            gl.glDrawArrays(GL.GL_POINTS, x, i);
            x += i;
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableVertexAttribArray(ringLoc);

        shader.unbind();
        pGraphics.endPGL();
        pGraphics.endDraw();

        sk.image(pGraphics, 0, 0);
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
