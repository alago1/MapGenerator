package MapPipelineUpdated.util;

import android.util.Log;

import com.mapgenerator.android.util.LoggerConfig;

import MapPipelineUpdated.pipeline.MapSettings;

public class FalloffMap extends MapLayer {

    private static Float intensity;
    private static Float fallOffset;
    private static String shape;

    @Override
    public float GenerateLayerValue(float[] coord, MapSettings config) {
        if(intensity == null || fallOffset == null) {
            MapSettings falloff_config = config.getObjectSettings("FalloffMap");
            intensity = (Float) falloff_config.smartGet("intensity", 3f);
            fallOffset = (Float) falloff_config.smartGet("fallOffset", 2.2f);
            String shape = (String) falloff_config.smartGet("shape", "square");
        }

        Integer mapWidth = (Integer) config.smartGet("mapWidth", 100);
        Integer mapHeight = (Integer) config.smartGet("mapHeight", 100);


        float dx = (coord[0]-mapWidth/2f)/(float)mapWidth;
        float dy = (coord[1]-mapHeight/2f)/(float)mapHeight;

        float value;
        if(shape.equals("square")){
            value = squareFunction(dx, dy);
        }else if(shape.equals("circle")){
            value = circleFunction(dx, dy);
        }else{
            value = squareFunction(dx, dy);

            if(LoggerConfig.ON){
                Log.w("Map Generator", "Falloff Map could not find shape function type: " + shape + ". Defaulted to Square function.");
            }
        }

        return 1-SmoothEnds(value);
    }

    private static float squareFunction(float dx, float dy){
        return Math.abs(dx + dy) + Math.abs(dx-dy);
    }

    private static float circleFunction(float dx, float dy){
        return fastPow(dx*dx + dy*dy, 0.5f);
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
