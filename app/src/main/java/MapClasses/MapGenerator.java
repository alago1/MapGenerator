package MapClasses;

import java.util.Arrays;

public class MapGenerator {

    public final int mapWidth;
    public final int mapHeight;
    float scale;
    float[] offset;
    public final int lod; // level of detail

    private MapLayer[] generators;

    public MapGenerator(int mapWidth, int mapHeight, float scale, float[] offset, int lod){
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.scale = scale;
        this.offset = offset;
        this.lod = lod;
        this.generators = new MapLayer[MapTypes.values().length];
    }

    public void UseNoiseMap(int seed, int octaves, float persistence, float lacunarity){
        generators[MapTypes.NOISE.ordinal()] = new NoiseMap(this, seed, octaves, persistence, lacunarity);
    }

    public void UseFalloffMap(float intensity, float fallOffset){
        generators[MapTypes.FALLOFF.ordinal()] = new FalloffMap(this, intensity, fallOffset);
    }

    public float[] composeMap(){
        float[] mapSum = new float[mapWidth * mapHeight];
        Arrays.fill(mapSum, 1f);

        for(MapLayer map : generators){
            if(map == null || ! map.isShowing())
                continue;

            map.GenerateMapLayer(mapSum);
        }

        for(int y = 0; y < mapHeight; y++){
            for(int x = 0; x < mapWidth; x++){
                mapSum[y*mapWidth + x] = Math.max(mapSum[y*mapWidth + x], 0.001f);
                mapSum[y*mapWidth + x] = Math.min(mapSum[y*mapWidth + x], 0.999f);
            }
        }

        return mapSum;
    }


    public int getLevelOfDetail(){
        return lod;
    }

    public int[] getAdjustedDimensions(){
        return new int[]{mapWidth/lod, mapHeight/lod};
    }

    public static float[] texture(float height){
//        float[] a = {1-height, 1-height, 1-height, 1.0f};
//        return a;
        if(height < 0.3){
            return new float[]{0.3f, 0.0f, 1.0f, 1.0f};
        }
        if(height < 0.4f){
            return new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        }
        if(height < 0.7f){
            return new float[]{1.0f, 0.0f, 0.0f, 1.0f};
        }
        return new float[]{0.0f, 1.0f, 0.0f, 1.0f};
    }
}
