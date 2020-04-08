package MapPipelineUpdated.pipeline;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MapMesh {

    private final MapSettings config;
    private final MapGenerator mapGenerator;

    private static final int BYTES_PER_SHORT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;

    MapMesh(MapSettings mapSettings){
        this.config = mapSettings;
        this.mapGenerator = new MapGenerator(mapSettings);;
    }


    /**
     * @return Buffer[]{FloatBuffer verticesBuffer, ShortBuffer faceBuffer}
     */
    Buffer[] GenerateMeshBuffer(){
        Integer mapWidth = (Integer) config.smartGet("mapWidth", 100);
        Integer mapHeight = (Integer) config.smartGet("mapHeight", 100);
        Integer lod = (Integer) config.smartGet("lod", 1);

        //TODO: let stride value be the sum of values of all keys in vertices_stride
        MapSettings stride = config.getObjectSettings("vertices_stride");
        Integer VERTICES_STRIDE = ((Integer) stride.smartGet("position", 3) + (Integer) stride.smartGet("color", 4))*BYTES_PER_FLOAT;

        Integer nVertices = ((mapWidth-1)/lod + 1) * ((mapHeight-1)/lod + 1);
        Integer nFaces = ((mapWidth-1)/lod) * ((mapHeight-1)/lod) * 2;


        //allocate (nVertices * (3 position components per vertex + 4 color components per vertex) * 4 bytes per float) bytes
        verticesBuffer = ByteBuffer.allocateDirect(nVertices*VERTICES_STRIDE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //TODO: this needs to be changed (along with other things ofc) to 4 vertices per face if square rendering is to be included as a feature
        //allocate (nFaces * 3 vertices per face * 2 bytes per short) bytes
        facesBuffer = ByteBuffer.allocateDirect(nFaces*3*BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();


        int vertexIndex = -1;
        float max_length = Math.max(mapWidth, mapHeight);
        float[] coords = new float[3];
        float[] start_coords = {-1f, 1f, 0f}; //top-left of the screen
        if(mapWidth  == max_length){
            float ratio = mapHeight/(float)mapWidth; // min/max
            start_coords[1] *= ratio;
        }else{
            float ratio = mapWidth/(float)mapHeight;
            start_coords[0] *= ratio;
        }
        coords[0] = start_coords[0];
        coords[1] = start_coords[1];

        float shift = 2*lod/max_length;

        for(int y = 0; y < mapHeight/lod; y++){
            coords[0] = start_coords[0];
            for(int x = 0; x < mapWidth/lod; x++) {
                vertexIndex += 1;
                coords[2] = mapGenerator.GenerateMapValue(new float[]{x, y});
                verticesBuffer.put(coords);

                //assumes triangle primitives
                if (x + 1 < mapWidth/lod && y + 1 < mapHeight/lod) {
                    facesBuffer.put((short) vertexIndex);
                    facesBuffer.put((short) (vertexIndex + mapWidth/lod));
                    facesBuffer.put((short) (vertexIndex + mapWidth/lod + 1));

                    facesBuffer.put((short) vertexIndex);
                    facesBuffer.put((short) (vertexIndex + mapWidth/lod + 1));
                    facesBuffer.put((short) (vertexIndex + 1));
                }

                coords[0] += shift;
                verticesBuffer.position(VERTICES_STRIDE/BYTES_PER_FLOAT * (vertexIndex+1));
            }
            coords[1] += -shift;
        }

        return new Buffer[]{verticesBuffer, facesBuffer};
    }



}
