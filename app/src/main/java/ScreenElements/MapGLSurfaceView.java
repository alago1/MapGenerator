package ScreenElements;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapGLSurfaceView extends GLSurfaceView {

    private MapRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 50.0f / 180f;
    private float previousX;
    private float previousY;

    public MapGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        this.setEGLContextClientVersion(2);
    }

    public void setRenderer(MapRenderer renderer) {
        this.renderer = renderer;
        this.setRenderer((Renderer) renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX();
        float y = e.getY();

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;

//                if(y < getHeight()/2)
//                    dx *= -1;
//                if(x < getWidth()/2)
//                    dy *= -1;

                float new_mapAngle = renderer.getMapAngle() + (dx * TOUCH_SCALE_FACTOR);
                renderer.setMapAngle(new_mapAngle);

                if(!renderer.isCameraLocked()){
                    float new_cameraAngle = renderer.getCameraAngle() - (dy * TOUCH_SCALE_FACTOR);
                    if(new_cameraAngle < 90 && new_cameraAngle > -0.01f)
                        renderer.setCameraAngle(new_cameraAngle);
                }
                requestRender();
        }

        previousX = x;
        previousY = y;

        return true;
    }
}
