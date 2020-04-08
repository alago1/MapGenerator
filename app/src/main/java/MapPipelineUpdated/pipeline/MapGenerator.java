package MapPipelineUpdated.pipeline;

import MapPipelineUpdated.util.MapLayer;

public class MapGenerator {

    private final MapSettings config;
    private static final String[] mapLayers = {"NoiseMap", "FalloffMap"};
    private static final String LAYERS_FULL_NAME = "MapPipelineUpdated.util.";

    MapGenerator(MapSettings mapSettings){
        this.config = mapSettings;
    }

    float GenerateMapValue(float[] coord){
        float value = 1;

        for(String layer : mapLayers){
            Boolean use = (Boolean) config.smartGet("use" + layer, false);
            if(!use)
                continue;

            try {
                MapLayer layer_class = (MapLayer) Class.forName(LAYERS_FULL_NAME + layer).newInstance();
                value = assemblingFunction(value, layer_class.GenerateLayerValue(coord, config));
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    //TODO: Enable more complex assembling functions and (big maybe) let user define the assembling function
    private float assemblingFunction(float a, float b){
        return a*b;
    }



}
