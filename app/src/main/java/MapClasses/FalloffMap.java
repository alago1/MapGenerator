package MapClasses;

import MapClasses.MapGenerator;
import MapClasses.MapLayer;

public class FalloffMap extends MapLayer {

    private float intensity; // 3f
    private float fallOffset; // 2.2f

    FalloffMap(MapGenerator parentGen, float intensity, float fallOffset){
        super(parentGen);

        this.intensity = intensity;
        this.fallOffset = fallOffset;
    }

    public void GenerateMapLayer(float[] map){
        int[] dim = {parentGen.mapWidth, parentGen.mapHeight};

        for(int y = 0; y < dim[1]; y++){
            for(int x = 0; x < dim[0]; x++){
                float dx = (x)/(float)dim[0];
                float dy = (y)/(float)dim[1];
                float value = Math.abs(dx-dy) + Math.abs(dx+dy);
                map[y*dim[0] + x] *= 1-SmoothEnds(value);
            }
        }
//        MapLayer.Normalize(falloffMap);
    }


    private static float fastPow(float a, float b){
        // not completely accurate but fast and good approximation of a^b

        int pwr_int = (int) b;
        float prod = 1;

        for(int i = 0; i < pwr_int; i++){
            prod *= a;
        }
        float percent = 1 - b + (float) pwr_int;

        return percent * prod + (1 - percent) * prod * a;
    }

    private float SmoothEnds(float value){

        //return Math.Pow(value, intensity) / (Math.Pow(value, intensity) + Math.Pow(mapOffset-mapOffset*value, intensity));
        float a = fastPow(value, intensity);
        float b = fastPow(fallOffset - fallOffset *value, intensity);

        return a/(a+b);
        //return fastPow(value, intensity)/(fastPow(value, intensity) + fastPow(mapOffset - mapOffset *value, intensity));
    }
}
