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


    private float[] vPMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] dimensionMatrix = {1, 0, 0, 0,
                                        0, 1, 0, 0,
                                        0, 0, 0, 0,
                                        0, 0, 0, 1}; //initialized to 2D

    private static final float RADIUS_2D = 1f;
    private static final float RADIUS_3D = 2.5f;

    volatile float mapAngle = (float) Math.PI/2; // angle from x-axis (radians)
    private float cameraAngle = 0; // angle from z-axis
    private float stored_cameraAngle;
    private int SPACE_RANK = 2;

    private float RADIUS = RADIUS_2D;
    private boolean cameraLock = true;
    private float[] cameraPosition;
    private float[] cameraUp;



    public MapRenderer(Context context, MapGenerator mapGen){
        this.context = context;
        this.mapGen = mapGen;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        map = mapGen.composeMap();
        updateCameraPosition();
        mapView = new MapView(context, map, mapGen);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float aspect_ratio = (float) width/height;

        //FIXME: By changing frustumM to orthoM it is clear that the graphing error happens at mapAngle going from positive to 0 and towards 180
        Matrix.frustumM(projectionMatrix, 0, -aspect_ratio, aspect_ratio, -1, 1, 1,100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        float[] rotateX = new float[16];
        float[] rotateZ = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //FIXME: Texture/Model disappears at certain mapAngles
        Matrix.setLookAtM(viewMatrix, 0, cameraPosition[0], cameraPosition[1], cameraPosition[2], 0f, 0f, 0.0f, cameraUp[0], cameraUp[1], cameraUp[2]);

//        Matrix.setLookAtM(viewMatrix, 0, 0, 0, RADIUS_3D, 0f, 0f, 0.0f, 0, -1, 0);

        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // VPMatrix * dimensionMatrix * position_3d = positon_on_screen
        // dimensionMatrix is the identity matrix with the value at the third row third column being 1 for 3D space and 0 for 2D space
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, dimensionMatrix, 0);

//        Matrix.setRotateM(rotateZ, 0, mapAngle*60, 0, 0, -1);
//        Matrix.setRotateM(rotateX, 0, -cameraAngle*60, 1, 0, 0);

//        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotateZ, 0);
//        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, scratch, 0);

        mapView.draw(scratch);
    }


     public float getMapAngle(){
        return mapAngle;
     }

     public void setMapAngle(float angle){
        mapAngle = (float) (angle%(2*Math.PI));
        updateCameraPosition();
        if(LoggerConfig.ON)
          Log.w("Camera", "mapAngle in degrees: " + mapAngle*180/Math.PI);
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
            cameraAngle = (float) (angle%(2*Math.PI));
            updateCameraPosition();
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
        updateCameraPosition();
    }

    public void updateCameraPosition(){
        float[] trig_mapAngle = {(float) Math.cos(mapAngle), (float) Math.sin(mapAngle)};
        if(isCameraLocked()){
            cameraPosition = new float[]{0, 0, RADIUS};
            cameraUp = new float[]{trig_mapAngle[0], trig_mapAngle[1], 0};
        }else {
            float[] trig_cameraAngle = new float[]{(float) Math.cos(cameraAngle), (float) Math.sin(cameraAngle)};

            cameraPosition[0] = RADIUS * trig_mapAngle[0] * trig_cameraAngle[1];
            cameraPosition[1] = RADIUS * trig_mapAngle[1] * trig_cameraAngle[1];
            cameraPosition[2] = RADIUS * trig_cameraAngle[0];

            cameraUp[0] = trig_mapAngle[0] * trig_cameraAngle[0];
            cameraUp[1] = trig_mapAngle[1] * trig_cameraAngle[0];
            cameraUp[2] = -trig_cameraAngle[1];
        }
    }
}
