package MapClasses;

import MapClasses.MapGenerator;

public abstract class MapLayer{

    MapGenerator parentGen;
    boolean show;
    int lod;

    public MapLayer(MapGenerator parentGen){
        this.parentGen = parentGen;
        this.lod = parentGen.lod;
        this.show = true;
    }

    abstract void GenerateMapLayer(float[] map);

    public boolean isShowing(){
        return this.show;
    }

    public void EnableShow(){
        this.show = true;
    }

    public void DisableShow(){
        this.show = false;
    }

    public static void Normalize(float[][] map){
        float maxHeight = -Float.MAX_VALUE;
        float minHeight = Float.MAX_VALUE;

        for(int y = 0; y < map[0].length; y++){
            for(int x = 0; x < map.length; x++){
                if(map[x][y] > maxHeight)
                    maxHeight = map[x][y];
                else if(map[x][y] < minHeight)
                    minHeight = map[x][y];
            }
        }

        for(int y = 0; y < map[0].length; y ++){
            for(int x = 0; x < map.length; x++){
                map[x][y] = InverseLerp(minHeight, maxHeight, map[x][y]);
            }
        }
    }

    static float InverseLerp(float a, float b, float f){
        // Linear Interpolation from [a,b] to [0,1]
        // Returns for 'f': [a,b] the point in [0,1]
        // Equivalent to percentage of the way from a to b
        return (f-a)/(b-a);
    }
}
