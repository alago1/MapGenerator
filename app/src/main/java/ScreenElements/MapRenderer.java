package ScreenElements;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.mapgenerator.android.util.LoggerConfig;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import MapClasses.MapGenerator;

public class MapRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private MapGenerator mapGen;
    private MapView mapView;
    private float[] map;


    private final float[] modelMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] dimensionMatrix = {1, 0, 0, 0,
                                        0, 1, 0, 0,
                                        0, 0, 0, 0,
                                        0, 0, 0, 1}; // initialized to 2D

    private static final float RADIUS_2D = 2.5f;
    private static final float RADIUS_3D = 4f;

    private float mapAngle = 0; // angle from x-axis (degrees)
    private float cameraAngle = 0; // angle from z-axis (degrees)
    private float stored_cameraAngle;
    private int SPACE_RANK = 2;

    private float RADIUS = RADIUS_2D;
    private boolean cameraLock = true;



    public MapRenderer(Context context, MapGenerator mapGen){
        this.context = context;
        this.mapGen = mapGen;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        map = mapGen.composeMap();
        mapView = new MapView(context, map, mapGen);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float aspect_ratio = (float) width/height;

        Matrix.perspectiveM(projectionMatrix, 0, 45, aspect_ratio, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        float[] temp = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, 0, 0, -RADIUS);
        Matrix.rotateM(modelMatrix, 0, -cameraAngle, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, mapAngle, 0, 0, 1);

        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);

        // temp * dimensionMatrix * position_3d = positon_on_screen
        // dimensionMatrix is the identity matrix with the value at the third row third column being 1 for 3D space and 0 for 2D space
        Matrix.multiplyMM(scratch, 0, temp, 0, dimensionMatrix, 0);

        mapView.draw(scratch);
    }


     public float getMapAngle(){
        return mapAngle;
     }

     public void setMapAngle(float angle){
        mapAngle = angle%360.0f;
//        if(LoggerConfig.ON)
//          Log.w("Camera", "mapAngle in degrees: " + mapAngle*180/Math.PI);
     }

     public float getCameraAngle() { return cameraAngle; }

    public boolean isCameraLocked() {
        return cameraLock;
    }

    public void lockCamera(){
        cameraLock = true;
        if(LoggerConfig.ON)
            Log.w("Camera", "Locked Camera");
    }

    public void unlockCamera(){
        cameraLock = false;
        if(LoggerConfig.ON)
            Log.w("Camera", "Unlocked Camera");
    }

    public void setCameraAngle(float angle){
        if(!isCameraLocked()) {
            cameraAngle = angle%360.0f;
//            if(LoggerConfig.ON)
//              Log.w("Camera", "cameraAngle in degrees: " + cameraAngle*180/Math.PI);
        }
    }

    public void setSPACE_RANK(int NEW_SPACE_RANK) {
        this.SPACE_RANK = NEW_SPACE_RANK;
        this.RADIUS = NEW_SPACE_RANK == 2 ? RADIUS_2D : RADIUS_3D;
        if(NEW_SPACE_RANK == 2) {
            stored_cameraAngle = getCameraAngle();
            setCameraAngle(0);
            lockCamera();
        }else{
            unlockCamera();
            setCameraAngle(stored_cameraAngle);
        }
        dimensionMatrix[10] = NEW_SPACE_RANK - 2;
    }

}
