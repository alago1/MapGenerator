package com.example.mapgenerator;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import MapClasses.MapGenerator;

public class MapRenderer implements GLSurfaceView.Renderer {

    private MapGenerator mapGen;
    private MapView mapView;
    private float[] map;
    private int[] dimensions;

    public MapRenderer(MapGenerator mapGen){
        this.mapGen = mapGen;
        this.dimensions = mapGen.getDimensions();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        map = mapGen.composeMap();
        mapView = new MapView(map, mapGen.getAdjustedDimensions(), 3);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mapView.draw();
    }
}
