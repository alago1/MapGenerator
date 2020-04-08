package MapPipelineUpdated.pipeline;

import android.util.Log;

import java.nio.Buffer;

public class MapAssembler {

    private final MapSettings config;
    private final MapTexturer texturer;

    public MapAssembler(MapSettings mapSettings){
        this.config = mapSettings;
        this.texturer = new MapTexturer(mapSettings);
    }

    public Buffer[] assembleMap(){
        return texturer.GenerateTexture();
    }


}
