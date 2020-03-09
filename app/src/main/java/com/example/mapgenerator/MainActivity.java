package com.example.mapgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

import MapClasses.MapGenerator;
import MapClasses.TerrainType;

public class MainActivity extends AppCompatActivity {

    private MapGLSurfaceView mapSurfaceView;
    private MapRenderer mapRenderer;
    private MapGenerator mapGen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

//        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
//        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
//        int minLevelOfDetail = (int) (Math.sqrt(3*width*height/Math.pow(2, 15)) + 1);

        //TODO: Make sure the lod is valid (sufficiently big so that the FacesBuffer fits in a Short)
        mapSurfaceView = new MapGLSurfaceView(this);
        mapGen = new MapGenerator(500, 500, 10f, new float[]{0.0f, 0.0f}, 4);
        mapGen.UseNoiseMap(1, 4, 1.5f, 0.25f);
        mapGen.UseFalloffMap(3f, 2.2f);
        mapGen.CustomizeTexture(MapGenerator.TextureStyle.COLOR, TexturePack());

        mapRenderer = new MapRenderer(mapGen);

        mapSurfaceView.setRenderer(mapRenderer);
        mapSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(mapSurfaceView);
    }


    public TerrainType[] TexturePack(){
        TerrainType[] terrains = new TerrainType[7];

        terrains[0] = new TerrainType("Dark Blue:Deep Sea", 0.4f, new float[]{0.08f, 0.0f, 1.0f, 1.0f});
        terrains[1] = new TerrainType("Blue:Sea",0.55f, new float[]{0.0f, 0.39f, 1.0f, 1.0f});
        terrains[2] = new TerrainType("Yellow:Sand", 0.56f, new float[]{1.0f, 0.84f, 0.43f, 1.0f});
        terrains[3] = new TerrainType("Green:Grass", 0.63f, new float[]{0.49f, 0.9f, 0.27f, 1.0f});
        terrains[4] = new TerrainType("Dark Green:High Grass", 0.75f, new float[]{0.18f, 0.39f, 0.0f, 1.0f});
        terrains[5] = new TerrainType("Brown:Rock", 0.9f, new float[]{0.29f, 0.13f, 0.0f, 1.0f});
        terrains[6] = new TerrainType("White:Snow", 1f, new float[]{1.0f, 1.0f, 1.0f, 1.0f});

        return terrains;
    }
}
