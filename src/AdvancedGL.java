import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import processing.core.*;
import processing.opengl.*;

import java.nio.*;
import java.util.ArrayList;

public class AdvancedGL extends PApplet {

    float t = 0;

    PShader shader;
    // content of elements in rings is (angle, radius, offset, idx)
    FloatBuffer ringBuffer;
    int ringVboId;
    int ringLoc;
    int partNum;
    ArrayList<Integer> partNumbers;

    PJOGL pgl;
    GL2ES2 gl;

    IntBuffer vbos;

    PGraphics pGraphics;

    @Override
    public void settings() {
        // enable P3D to use OpenGL features
        size(800, 600, P3D);
    }

    @Override
    public void setup() {
        pGraphics = createGraphics(width, height, P3D);
        shader = pGraphics.loadShader("frag.glsl", "vert.glsl");
        // get the total number of particles needed;
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
//        endPGL();
        pGraphics.endDraw();
    }

    @Override
    public void draw() {


        pGraphics.beginDraw();
        pgl = (PJOGL) pGraphics.beginPGL();
        gl = pgl.gl.getGL2ES2();

        pGraphics.background(0);
        pGraphics.translate(width / 2, height / 2);

        shader.bind();
        shader.set("time", t);

        gl.glEnableVertexAttribArray(ringLoc);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ringVboId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, partNum * 4 * Float.BYTES, ringBuffer, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(ringLoc, 4, GL.GL_FLOAT, false, 4 * Float.BYTES, 0);

        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        int x = 0;
        int j = 0;
        for (int i: partNumbers) {
            gl.glDrawArrays(GL.GL_POINTS, x, i);
            x += i;
            j += 1;
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glDisableVertexAttribArray(ringLoc);

        shader.unbind();
        pGraphics.endPGL();
        pGraphics.endDraw();

        image(pGraphics, 0, 0);
        surface.setTitle("Framerate: " + frameRate);
        t += 0.01;
    }

    private ArrayList<Integer> calculateParticleNumbers() {
        ArrayList<Integer> res = new ArrayList<>();
        float max_ring_size = 200;
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


    FloatBuffer allocateDirectFloatBuffer(int n) {
        return ByteBuffer.allocateDirect(n * Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
}
