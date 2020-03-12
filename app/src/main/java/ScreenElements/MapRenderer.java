package ScreenElements;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import MapClasses.MapGenerator;

public class MapRenderer implements GLSurfaceView.Renderer {

    private MapGenerator mapGen;
    private MapView mapView;
    private float[] map;

    private float[] vPMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] rotationMatrix = new float[16];
    private float[] dimensionMatrix = {1, 0, 0, 0,
                                        0, 1, 0, 0,
                                        0, 0, 0, 0,
                                        0, 0, 0, 1}; //initialized to 2D

    volatile float mAngle;
    private int Number_Of_Dimensions = 2;

    public MapRenderer(MapGenerator mapGen){
        this.mapGen = mapGen;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        map = mapGen.composeMap();
        mapView = new MapView(map, mapGen);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width/height;

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        float[] vPRMatrix = new float[16];

        GLES20.glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 0, -1.0f);
        Matrix.multiplyMM(vPRMatrix, 0, vPMatrix, 0, rotationMatrix, 0);

        Matrix.multiplyMM(scratch, 0, vPRMatrix, 0, dimensionMatrix, 0);

        mapView.draw(scratch);
    }


     public float getAngle(){
        return mAngle;
     }

     public void setAngle(float angle){
        mAngle = angle;
     }

    public void setNumber_Of_Dimensions(int number_Of_Dimensions) {
        this.Number_Of_Dimensions = number_Of_Dimensions;
        dimensionMatrix[10] = number_Of_Dimensions - 2;
    }
}
