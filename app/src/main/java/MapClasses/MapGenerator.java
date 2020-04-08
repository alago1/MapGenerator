package MapClasses;

import java.util.Arrays;

public class MapGenerator {

    public final int mapWidth;
    public final int mapHeight;
    float scale;
    float[] offset;
    private int lod; // level of detail

    private float[] mapComposition;

    private MapLayer[] generators;

    private TextureStyle MapTexture;
    private TerrainType[] terrains;
    public static enum TextureStyle {GRAYSCALE, COLOR};

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
        int nVertices = ((mapWidth-1)/lod +1)*((mapHeight-1)/lod +1);
        float[] mapSum = new float[nVertices];
        Arrays.fill(mapSum, 1f);

        for(MapLayer map : generators){
            if(map == null || ! map.isShowing())
                continue;

            map.GenerateMapLayer(mapSum, lod);
        }

        for(int i = 0; i < mapSum.length; i++){
//            mapSum[i] = (float) (Math.floor(mapSum[i]*10)/10.0);

            mapSum[i] = Math.max(mapSum[i], 0.001f);
            mapSum[i] = Math.min(mapSum[i], 0.999f);
        }

        mapComposition = mapSum;
        return mapComposition;
    }


    public int getLevelOfDetail(){
        return lod;
    }

    public int[] getAdjustedDimensions(){
        return new int[]{mapWidth/lod, mapHeight/lod};
    }

    public void CustomizeTexture(TextureStyle style, TerrainType[] terrains){
        this.MapTexture = style;
        this.terrains = terrains;
    }


    public float[] getTexture(float height){
        if(MapTexture == TextureStyle.GRAYSCALE)
            return new float[]{1-height, 1-height, 1-height, 1.0f};

        if(MapTexture == TextureStyle.COLOR) {
            for (int i = 0; i < terrains.length; i++) {
                if (height < terrains[i].getHeight())
                    return terrains[i].getColor();
            }
            return new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        }

        return new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    }
}
