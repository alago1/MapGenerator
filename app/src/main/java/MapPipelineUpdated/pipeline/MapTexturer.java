package MapPipelineUpdated.pipeline;

import android.util.Log;

import com.mapgenerator.android.util.LoggerConfig;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public class MapTexturer {

    private static final int BYTES_PER_SHORT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private final MapSettings config;
    private final MapMesh mesh;

    MapTexturer(MapSettings mapSettings){
        this.config = mapSettings;
        this.mesh = new MapMesh(mapSettings);
    }

    Buffer[] GenerateTexture(){
        Integer mapWidth = (Integer) config.smartGet("mapWidth", 100);
        Integer mapHeight = (Integer) config.smartGet("mapHeight", 100);
        Integer lod = (Integer) config.smartGet("lod", 1);

        //TODO: let stride value be the sum of values of all keys in vertices_stride
        MapSettings stride = config.getObjectSettings("vertices_stride");
        int VERTICES_STRIDE = ((Integer) stride.smartGet("position", 3) + (Integer) stride.smartGet("color", 4))*BYTES_PER_FLOAT;

        String textureType = (String) config.smartGet("texture", "grayscale");

        int nVertices = ((mapWidth-1)/lod + 1) * ((mapHeight-1)/lod + 1);
        Buffer[] meshData = mesh.GenerateMeshBuffer();

        if(textureType.equals("grayscale")){
            GrayscaleTexturePack(meshData, nVertices, VERTICES_STRIDE);
        }else if(textureType.equals("color")){
            ColorTexturePack(meshData, nVertices, VERTICES_STRIDE);
        }else{
            if(LoggerConfig.ON){
                Log.w("MapTexturer", "Invalid Texture type: " + textureType + ". Defaulted to grayscale");
            }
            GrayscaleTexturePack(meshData, nVertices, VERTICES_STRIDE);
        }


        return meshData;
    }

    private void GrayscaleTexturePack(Buffer[] meshData, int nVertices, int vertex_stride){
        MapSettings stride = config.getObjectSettings("vertices_stride");
        Integer position_stride = (Integer) stride.smartGet("position", 3);
        Integer color_stride = (Integer) stride.smartGet("color", 4);
        if(color_stride != 4){
            if(LoggerConfig.ON){
                Log.w("MapTexturer", "Invalid color_stride: " + color_stride);
            }
        }

        FloatBuffer verticesBuffer = (FloatBuffer) meshData[0];
        float height;

        for(int i = 0; i < nVertices; i++){
            verticesBuffer.position(i*vertex_stride + (position_stride-1)*BYTES_PER_FLOAT);
            height = verticesBuffer.get();

            verticesBuffer.put(new float[]{1-height, 1-height, 1-height, 1.0f});
        }

        verticesBuffer.position(0);

    }

    private void ColorTexturePack(Buffer[] meshData, int nVertices, int stride){
        //TODO: this + implement Terrains
    }

}
