package MapClasses;

import java.util.Random;

public class NoiseMap extends MapLayer {

    private final int seed;

    private int octaves;
    private float persistence;
    private float lacunarity;

    private OpenSimplexNoise noise;


    public NoiseMap(MapGenerator parentGen, int seed, int octaves, float persistence, float lacunarity){
        // Sets all necessary variables in the object
        // Assumes the map is intended to show; if that is not the case call NoiseMap.DisableShow()
        super(parentGen);

        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        this.noise = new OpenSimplexNoise((long) seed);
    }


    @Override
    public void GenerateMapLayer(float[] map){
        int[] adj_dim = parentGen.getAdjustedDimensions();
        float[] noiseMap = new float[adj_dim[0]*adj_dim[1]];

        Random prng = new Random(seed);
        float[][] octaveOffsets = new float[octaves][2];
        for(int i = 0; i < octaves; i++){
            float offsetX = prng.nextFloat()*200000 - 100000 + parentGen.offset[0];
            float offsetY = prng.nextFloat()*200000 - 100000 + parentGen.offset[1];
            float[] offsetXY = {offsetX, offsetY};

            octaveOffsets[i] = offsetXY;
        }


        float maxNoiseHeight = -Float.MAX_VALUE;
        float minNoiseHeight = Float.MAX_VALUE;

        for(int y = 0; y < adj_dim[1]; y++){
            for(int x = 0; x < adj_dim[0]; x++){

                float amplitude = 1;
                float frequency = 1;
                float noiseHeight = 0;

                for(int i = 0; i < octaves; i++){
                    float sampleX = (x-adj_dim[0]/2f)/parentGen.scale * frequency + octaveOffsets[i][0];
                    float sampleY = (y-adj_dim[1]/2f)/parentGen.scale * frequency + octaveOffsets[i][1];

                    noiseHeight += (float) noise.eval(sampleX, sampleY) * amplitude;

                    amplitude *= persistence;
                    frequency *= lacunarity;
                }

                noiseMap[y*adj_dim[0] + x] = noiseHeight;

                if(noiseHeight > maxNoiseHeight){
                    maxNoiseHeight = noiseHeight;
                }else if(noiseHeight < minNoiseHeight) {
                    minNoiseHeight = noiseHeight;
                }
            }
        }


        for(int y = 0; y < adj_dim[1]; y++){
            for(int x = 0; x < adj_dim[0]; x++){
                map[y*adj_dim[0] + x] *= MapLayer.InverseLerp(minNoiseHeight, maxNoiseHeight, noiseMap[y*adj_dim[0] + x]);
            }
        }
    }

}
