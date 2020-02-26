package com.example.mapgenerator;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class MapView {

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private int length_map;

    private final String vertexShaderCode = "attribute vec4 position;"+
                                        "uniform mat4 matrix;"+
                                        "void main() {"+
                                        "   gl_Position = matrix * position;"+
                                        "}";

    private final String fragmentShaderCode = "precision mediump float;"+
                                        "void main(){"+
                                        "   gl_FragColor = vec4(1, 0.5, 0, 1.0);"+
                                        "}";

    private int program;


    public MapView (float[] map, int[] adj_Dimensions, int nDimensions){
        length_map = map.length;
        ByteBuffer vertexBB = ByteBuffer.allocateDirect(map.length*3*4);
        vertexBB.order(ByteOrder.nativeOrder());
        verticesBuffer = vertexBB.asFloatBuffer();

        ByteBuffer faceBB = ByteBuffer.allocateDirect((2*map.length)*3*2);
        faceBB.order(ByteOrder.nativeOrder());
        facesBuffer = faceBB.asShortBuffer();

        for(int y = 0; y < adj_Dimensions[1]; y++){
            for(int x = 0; x < adj_Dimensions[0]; x++){
                verticesBuffer.put((float) x);
                verticesBuffer.put((float) y);
                if(nDimensions == 3)
                    verticesBuffer.put(map[y * adj_Dimensions[0] + x]);
                else
                    verticesBuffer.put(0f);
                if(x+1 < adj_Dimensions[0] && y+1 < adj_Dimensions[1]){
                    facesBuffer.put((short) (y*adj_Dimensions[0] + x));
                    facesBuffer.put((short) ((y+1)*adj_Dimensions[0] + x));
                    facesBuffer.put((short) (y*adj_Dimensions[0] + x + 1));

                    facesBuffer.put((short) ((y+1)*adj_Dimensions[0] + x));
                    facesBuffer.put((short) ((y+1)*adj_Dimensions[0] + x + 1));
                    facesBuffer.put((short) (y*adj_Dimensions[0] + x + 1));
                }
            }
        }
        verticesBuffer.position(0);
        facesBuffer.position(0);

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertexShader);

        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);

        GLES20.glUseProgram(program);
    }


    public void draw(){
        int position = GLES20.glGetAttribLocation(program, "position");

        GLES20.glEnableVertexAttribArray(position);

        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 3*4, verticesBuffer);


        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];
        float[] productMatrix = new float[16];

        Matrix.frustumM(projectionMatrix, 0,-1, 1,-1, 1,2, 9);

        Matrix.setLookAtM(viewMatrix, 0,0, 3, -4,0, 0, 0,0, 1, 0);

        Matrix.multiplyMM(productMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        int matrix = GLES20.glGetUniformLocation(program, "matrix");

        GLES20.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, length_map*2*3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        GLES20.glDisableVertexAttribArray(position);
    }
}
