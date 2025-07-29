package com.example.bouncingcube;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CubeRenderer implements GLSurfaceView.Renderer {
    private Cube cube;
    private float[] mvpMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    
    // Animation and physics variables
    private float cubeX = 0.0f;
    private float cubeY = 0.0f;
    private float velocityX = 0.02f;
    private float velocityY = 0.015f;
    private float rotationAngle = 0.0f;
    private float aspectRatio = 1.0f;
    
    // Boundaries for bouncing
    private static final float CUBE_SIZE = 0.3f;
    private static final float BOUNCE_DAMPING = 0.98f;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set white background
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        cube = new Cube();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // Update physics
        updatePhysics();
        
        // Set up the camera (view matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        
        // Create model matrix with translation and rotation
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, cubeX, cubeY, 0);
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 1.0f, 1.0f, 0.5f);
        
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
        
        // Draw the cube
        cube.draw(mvpMatrix);
        
        // Update rotation
        rotationAngle += 2.0f;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        
        aspectRatio = (float) width / height;
        
        // Create a projection matrix from device screen geometry
        Matrix.frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 2, 7);
    }
    
    private void updatePhysics() {
        // Update position
        cubeX += velocityX;
        cubeY += velocityY;
        
        // Calculate boundaries based on aspect ratio
        float boundX = aspectRatio - CUBE_SIZE;
        float boundY = 1.0f - CUBE_SIZE;
        
        // Bounce off left and right walls
        if (cubeX > boundX || cubeX < -boundX) {
            velocityX = -velocityX * BOUNCE_DAMPING;
            cubeX = cubeX > boundX ? boundX : -boundX;
        }
        
        // Bounce off top and bottom walls
        if (cubeY > boundY || cubeY < -boundY) {
            velocityY = -velocityY * BOUNCE_DAMPING;
            cubeY = cubeY > boundY ? boundY : -boundY;
        }
    }

    private static class Cube {
        private final FloatBuffer vertexBuffer;
        private final ShortBuffer drawListBuffer;
        private final int program;
        private int positionHandle;
        private int colorHandle;
        private int mvpMatrixHandle;

        // Cube vertices
        private static final float[] vertices = {
            // Front face
            -0.15f, -0.15f,  0.15f,
             0.15f, -0.15f,  0.15f,
             0.15f,  0.15f,  0.15f,
            -0.15f,  0.15f,  0.15f,
            // Back face
            -0.15f, -0.15f, -0.15f,
             0.15f, -0.15f, -0.15f,
             0.15f,  0.15f, -0.15f,
            -0.15f,  0.15f, -0.15f,
        };

        // Order to draw vertices
        private static final short[] drawOrder = {
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 5, 6, 4, 6, 7,
            // Left face
            7, 3, 0, 7, 0, 4,
            // Right face
            1, 5, 6, 1, 6, 2,
            // Top face
            3, 2, 6, 3, 6, 7,
            // Bottom face
            0, 1, 5, 0, 5, 4
        };

        // Colors for each vertex (RGB)
        private static final float[] colors = {
            0.8f, 0.2f, 0.2f, 1.0f, // Red
            0.2f, 0.8f, 0.2f, 1.0f, // Green
            0.2f, 0.2f, 0.8f, 1.0f, // Blue
            0.8f, 0.8f, 0.2f, 1.0f, // Yellow
            0.8f, 0.2f, 0.8f, 1.0f, // Magenta
            0.2f, 0.8f, 0.8f, 1.0f, // Cyan
            0.6f, 0.4f, 0.2f, 1.0f, // Brown
            0.4f, 0.4f, 0.4f, 1.0f  // Gray
        };

        private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying vec4 fColor;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  fColor = vColor;" +
            "}";

        private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 fColor;" +
            "void main() {" +
            "  gl_FragColor = fColor;" +
            "}";

        public Cube() {
            // Initialize vertex buffer
            ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);

            // Initialize color buffer
            ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
            cb.order(ByteOrder.nativeOrder());
            colorBuffer = cb.asFloatBuffer();
            colorBuffer.put(colors);
            colorBuffer.position(0);

            // Initialize draw list buffer
            ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

            // Prepare shaders and OpenGL program
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
        }

        private FloatBuffer colorBuffer;

        public void draw(float[] mvpMatrix) {
            // Use the program
            GLES20.glUseProgram(program);

            // Get handle to vertex shader's vPosition member
            positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

            // Get handle to vertex shader's vColor member
            colorHandle = GLES20.glGetAttribLocation(program, "vColor");
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

            // Get handle to shape's transformation matrix
            mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

            // Draw the cube
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex arrays
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
        }

        private int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }
    }
}