package MapClasses;

import java.util.Arrays;

public class MapGenerator {

    int mapWidth;
    int mapHeight;
    float scale;
    float[] offset;
    int lod; // level of detail

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
        float[] mapSum = new float[mapWidth/lod * mapHeight/lod];
        Arrays.fill(mapSum, 1f);

        for(MapLayer map : generators){
            if(map == null || ! map.isShowing())
                continue;

            map.GenerateMapLayer(mapSum);
        }

        for(int y = 0; y < mapHeight/lod; y++){
            for(int x = 0; x < mapWidth/lod; x++){
                mapSum[y*mapWidth/lod + x] = Math.max(mapSum[y*mapWidth/lod + x], 0.001f);
                mapSum[y*mapWidth/lod + x] = Math.min(mapSum[y*mapWidth/lod + x], 0.999f);
            }
        }

        return mapSum;
    }

    public int[] getDimensions(){
        int[] dim = {mapWidth, mapHeight};
        return dim;
    }

    public int getLevelOfDetail(){
        return lod;
    }

    public int[] getAdjustedDimensions(){
        int[] adj_dim = {mapWidth/lod, mapHeight/lod};
        return adj_dim;
    }
}
