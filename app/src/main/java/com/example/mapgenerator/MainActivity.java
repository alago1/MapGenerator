package com.example.mapgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

import MapClasses.MapGenerator;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mapSurfaceView;
    private MapRenderer mapRenderer;
    private MapGenerator mapGen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

//        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
//        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
//        int minLevelOfDetail = (int) (Math.sqrt(3*width*height/Math.pow(2, 15)) + 1);

        //TODO: Make sure the lod is valid (sufficiently big so that the FacesBuffer fits in a Short)
        mapSurfaceView = (GLSurfaceView) findViewById(R.id.map_surface_view);
        mapGen = new MapGenerator(100, 100, 10f, new float[2], 1);
        mapGen.UseNoiseMap(1, 4, 1.5f, 0.25f);
        mapGen.UseFalloffMap(3f, 2.2f);

        mapRenderer = new MapRenderer(mapGen);

        mapSurfaceView.setEGLContextClientVersion(2);
        mapSurfaceView.setRenderer(mapRenderer);
        mapSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
