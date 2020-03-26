package MapClasses;

public class TerrainType {

    String name;
    float height;
    float[] color;

    public TerrainType(String name, float height, float[] color){
        this.name = name;
        this.height = height;
        this.color = color;
        assert color.length == 4;
    }

    public float getHeight() {
        return height;
    }

    public float[] getColor() {
        return color;
    }
}
