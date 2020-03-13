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

    volatile float mapAngle;
    private float cameraAngle = 0; // angle from z-axis
    private int SPACE_RANK = 2;

    // Variables that probably will be deleted later:
    private float[] trig_cameraAngle = {1, 0}; //cos, sin
    float radius = 3f;


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

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1,5);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];
        float[] vPRMatrix = new float[16];

        GLES20.glClearColor(1.0f, 0.0f,0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //FIXME: Texture/Model disappears at certain mapAngles
        Matrix.setLookAtM(viewMatrix, 0, 0, radius*trig_cameraAngle[1], radius*trig_cameraAngle[0], 0f, 0f, 0f, 0f, -trig_cameraAngle[0], trig_cameraAngle[1]);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setRotateM(rotationMatrix, 0, mapAngle, 0, 0, -1.0f);
        Matrix.multiplyMM(vPRMatrix, 0, vPMatrix, 0, rotationMatrix, 0);

        // VPRMatrix * dimensionMatrix * position_3d = positon_on_screen
        // dimensionMatrix is the identity matrix with the value at the third row third column being 1 for 3D space and 0 for 2D space
        Matrix.multiplyMM(scratch, 0, vPRMatrix, 0, dimensionMatrix, 0);

        mapView.draw(scratch);
    }


     public float getMapAngle(){
        return mapAngle;
     }

     public void setMapAngle(float angle){
        mapAngle = angle;
     }

     public float getCameraAngle() { return cameraAngle; }

    public void setCameraAngle(float angle){
        cameraAngle = (float) (angle * Math.PI / 180); // from degrees to radians
        System.out.println("angle in radians: " + cameraAngle);
        trig_cameraAngle[0] = (float) Math.cos(cameraAngle);
        trig_cameraAngle[1] = (float) Math.sin(cameraAngle);
    }

    public void setSPACE_RANK(int SPACE_RANK) {
        this.SPACE_RANK = SPACE_RANK;
        dimensionMatrix[10] = SPACE_RANK - 2;
    }
}
