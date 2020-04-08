package MapPipelineUpdated.util;

import MapPipelineUpdated.pipeline.MapSettings;

public abstract class MapLayer{

    public abstract float GenerateLayerValue(float[] coord, MapSettings config);


    static float InverseLerp(float a, float b, float f){
        // Linear Interpolation from [a,b] to [0,1]
        // Returns for 'f': [a,b] the point in [0,1]
        // Equivalent to percentage of the way from a to b
        return (f-a)/(b-a);
    }
}
