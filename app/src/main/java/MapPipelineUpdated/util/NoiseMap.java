package MapPipelineUpdated.util;

import java.util.Random;

import MapClasses.OpenSimplexNoise;
import MapPipelineUpdated.pipeline.MapSettings;

public class NoiseMap extends MapLayer {

    private static OpenSimplexNoise noise;
    private static float[][] octaveOffsets;

    @Override
    public float GenerateLayerValue(float[] coord, MapSettings config) {
        //Variables related to noise function
        MapSettings noise_config = config.getObjectSettings("NoiseMap");

        Integer seed = (Integer) noise_config.smartGet("seed", 1);
        Integer octaves = (Integer) noise_config.smartGet("octaves", 4);
        Float persistence = (Float) noise_config.smartGet("persistence", 1.5f);
        Float lacunarity = (Float) noise_config.smartGet("lacunarity", 0.25f);

        //Variables related to map
        Integer[] dim = {(Integer) config.smartGet("mapWidth", 100), (Integer) config.smartGet("mapHeight", 100)};
        Float scale = (Float) config.smartGet("scale", 10f);
        Float[] mapOffset = {(Float) config.smartGet("offsetX", 0), (Float) config.smartGet("offsetY", 0)};


        //if noise has not been initialized, then this is the first iteration, initialize noise and octaveOffsets
        if(noise == null){
            initializeClassVariables(seed, octaves, mapOffset);
        }


        float amplitude = 1;
        float frequency = 1;
        float noiseHeight = 0;

        for(int i = 0; i < octaves; i++){
            float sampleX = (coord[0]-dim[0]/2f)/scale * frequency + octaveOffsets[i][0];
            float sampleY = (coord[1]-dim[1]/2f)/scale * frequency + octaveOffsets[i][1];

            noiseHeight += (float) noise.eval(sampleX, sampleY) * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return noiseHeight;
    }


    private void initializeClassVariables(Integer seed,  Integer octaves, Float[] mapOffset) {
        //initializes noise since it would be expensive to destruct and construct a new one for every vertex
        //octaveOffsets is likely not __that__ expensive (since octaves tends to be small) but there
        //is no reason not to initialize it here since we'll have to create initialize noise anyway

        noise = new OpenSimplexNoise(seed);
        octaveOffsets = new float[octaves][2];

        Random prng = new Random(seed);
        for (int i = 0; i < octaves; i++) {
            float offsetX = prng.nextFloat() * 200000 - 100000 + mapOffset[0];
            float offsetY = prng.nextFloat() * 200000 - 100000 + mapOffset[1];
            float[] offsetXY = {offsetX, offsetY};

            octaveOffsets[i] = offsetXY;
        }
    }

}
