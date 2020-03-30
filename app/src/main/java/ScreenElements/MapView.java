package ScreenElements;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.mapgenerator.R;
import com.mapgenerator.android.util.LoggerConfig;
import com.mapgenerator.android.util.ShaderHelper;
import com.mapgenerator.android.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


import MapClasses.MapGenerator;

public class MapView {

    private final Context context;

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private int nVertices;
    private int nFaces;

    private final String vertexShaderCode;
    private final String fragmentShaderCode;

    private int program;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;

    private static final int VERTICES_STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    //FIXME: Figure out why jagged triangles are rendered in the left (Fix that)
    public MapView (Context context, float[] map, MapGenerator mapGen){
        //Setting context reference for class
        this.context = context;

        //Reading shader's code from .glsl files in res and assigning as string to variables
        vertexShaderCode = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader);
        fragmentShaderCode = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader);

        //Compiling Shaders and assigning to handles
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderCode);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderCode);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }

        GLES20.glUseProgram(program);



        int lod = mapGen.getLevelOfDetail();
        int[] adj_dim = mapGen.getAdjustedDimensions();
        int dx = 1;
        int dy = mapGen.mapWidth/lod;

        nVertices = map.length;
        nFaces = ((mapGen.mapWidth-1)/lod) * ((mapGen.mapHeight-1)/lod) * 2;

        //allocate (nVertices * (3 position components per vertex + 4 color components per vertex) * 4 bytes per float) bytes
        verticesBuffer = ByteBuffer.allocateDirect(nVertices*VERTICES_STRIDE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //allocate (nFaces * 3 vertices per face * 2 bytes per short) bytes
        facesBuffer = ByteBuffer.allocateDirect(nFaces*3*BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();


        int vertexIndex = -1;
        float max_length = Math.max(mapGen.mapWidth, mapGen.mapHeight);
        float[] coords = new float[3];
        float[] start_coords = {-1f, 1f, 0f};
        if(mapGen.mapWidth  == max_length){
            float ratio = mapGen.mapHeight/(float)mapGen.mapWidth; // min/max
            start_coords[1] *= ratio;
        }else{
            float ratio = mapGen.mapWidth/(float)mapGen.mapHeight;
            start_coords[0] *= ratio;
        }
        coords[0] = start_coords[0];
        coords[1] = start_coords[1];

        float shift = 2*lod/max_length;

        for(int y = 0; y < adj_dim[1]; y++){
            coords[0] = start_coords[0];
            for(int x = 0; x < adj_dim[0]; x++) {
                vertexIndex += 1;
                coords[2] = map[vertexIndex];
                verticesBuffer.put(coords);
                float[] texture = mapGen.getTexture(map[vertexIndex]);
                //debug purposes:
//                if(coords[0] < -0.8){
//                    verticesBuffer.put(new float[]{0, 1, 0, 1});
//                }else if(coords[1] < -0.8){
//                    verticesBuffer.put(new float[]{0.5f, 0.5f, 0, 1});
//                }else
                verticesBuffer.put(texture);


                if (x + 1 < adj_dim[0] && y + 1 < adj_dim[1]) {
                    facesBuffer.put((short) vertexIndex);
                    facesBuffer.put((short) (vertexIndex + dy));
                    facesBuffer.put((short) (vertexIndex + dy + dx));

                    facesBuffer.put((short) vertexIndex);
                    facesBuffer.put((short) (vertexIndex + dy + dx));
                    facesBuffer.put((short) (vertexIndex + dx));
                }

                coords[0] += shift;
            }
            coords[1] += -shift;
        }


        //Handles for attributes in Shaders
        int position = GLES20.glGetAttribLocation(program, "position");
        int color = GLES20.glGetAttribLocation(program, "color");

        //Setting position for future drawing
        facesBuffer.position(0);


        verticesBuffer.position(0);
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, VERTICES_STRIDE, verticesBuffer);

        verticesBuffer.position(POSITION_COMPONENT_COUNT); //start of color indices in each vertex
        GLES20.glEnableVertexAttribArray(color);
        GLES20.glVertexAttribPointer(color, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, VERTICES_STRIDE, verticesBuffer);
    }


    public void draw(float[] mvpMatrix){
        int matrix = GLES20.glGetUniformLocation(program, "matrix");

        GLES20.glUniformMatrix4fv(matrix, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, nFaces*3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);


//        GLES20.glDisableVertexAttribArray(position);
    }

}
