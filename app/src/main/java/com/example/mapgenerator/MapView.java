package com.example.mapgenerator;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


import MapClasses.MapGenerator;

public class MapView {

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private FloatBuffer colorBuffer;
    private int nVertices;
    private int nFaces;

    private final String vertexShaderCode = "attribute vec4 position;"+
                                        "attribute vec4 color;"+
                                        "uniform mat4 matrix;"+
                                        "varying vec4 interpolated_color;"+
                                        "void main() {"+
                                        "   interpolated_color = color;"+
                                        "   gl_Position = matrix * position;"+
                                        "}";

    private final String fragmentShaderCode = "precision mediump float;"+
                                        "varying vec4 interpolated_color;"+
                                        "void main(){"+
                                        "   gl_FragColor = interpolated_color;"+
                                        "}";

    private int program;

    //FIXME: Figure out why jagged triangles are rendered in the left (Fix that)
    public MapView (float[] map, MapGenerator mapGen, int nDimensions){
        int dx = 1;
        int dy = mapGen.mapWidth/mapGen.lod;

        nVertices = ((mapGen.mapWidth-1)/mapGen.lod +1)*((mapGen.mapHeight-1)/mapGen.lod +1);
        nFaces = ((mapGen.mapWidth-1)/mapGen.lod) * ((mapGen.mapHeight-1)/mapGen.lod) * 2;

        ByteBuffer vertexBB = ByteBuffer.allocateDirect(nVertices*3*4);
        vertexBB.order(ByteOrder.nativeOrder());
        verticesBuffer = vertexBB.asFloatBuffer();

        ByteBuffer faceBB = ByteBuffer.allocateDirect(nFaces*3*2);
        faceBB.order(ByteOrder.nativeOrder());
        facesBuffer = faceBB.asShortBuffer();

        ByteBuffer colorBB = ByteBuffer.allocateDirect(nVertices*4*4);
        colorBB.order(ByteOrder.nativeOrder());
        colorBuffer = colorBB.asFloatBuffer();


        int vertex_index = -1;
        float[] coords = {-1f, -1f, 0f};
        float dx_coord = 2*mapGen.lod/(float)mapGen.mapWidth;
        float dy_coord = 2*mapGen.lod/(float)mapGen.mapHeight;

        for(int y = 0; y < mapGen.mapHeight; y+=mapGen.lod){
            coords[0] = -1f;
            for(int x = 0; x < mapGen.mapWidth; x+=mapGen.lod) {
                vertex_index += 1;
                if (nDimensions == 3)
                    coords[2] = map[y * mapGen.mapWidth + x];
                verticesBuffer.put(coords);
                float[] texture = MapGenerator.texture(map[y*mapGen.mapWidth + x]);
//                System.out.println(map[y*mapGen.mapWidth + x]);
//                System.out.println(texture[0] + " " + texture[1] + " " + texture[2] + " " + texture[3]);
                colorBuffer.put(texture);


                if (x + mapGen.lod < mapGen.mapWidth && y + mapGen.lod < mapGen.mapHeight) {
                    facesBuffer.put((short) vertex_index);
                    facesBuffer.put((short) (vertex_index + dy));
                    facesBuffer.put((short) (vertex_index + dy + dx));

                    facesBuffer.put((short) vertex_index);
                    facesBuffer.put((short) (vertex_index + dy + dx));
                    facesBuffer.put((short) (vertex_index + dx));
                }

                coords[0] += dx_coord;
            }
            coords[1] += dy_coord;
        }

        verticesBuffer.position(0);
        colorBuffer.position(0);
        facesBuffer.position(0);

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
    }


    public void draw(float[] mvpMatrix){
        int position = GLES20.glGetAttribLocation(program, "position");
        int color = GLES20.glGetAttribLocation(program, "color");

        GLES20.glEnableVertexAttribArray(position);
        GLES20.glEnableVertexAttribArray(color);

        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glVertexAttribPointer(color, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        int matrix = GLES20.glGetUniformLocation(program, "matrix");

        GLES20.glUniformMatrix4fv(matrix, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nFaces*3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);


        GLES20.glDisableVertexAttribArray(color);
        GLES20.glDisableVertexAttribArray(position);
    }

}
