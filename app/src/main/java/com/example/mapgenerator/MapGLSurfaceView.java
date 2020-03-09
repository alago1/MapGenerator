package com.example.mapgenerator;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MapGLSurfaceView extends GLSurfaceView {

    private MapRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 50.0f / 320;
    private float previousX;
    private float previousY;

    public MapGLSurfaceView(Context context) {
        super(context);

        this.setEGLContextClientVersion(2);
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

                renderer.setAngle(renderer.getAngle() + ((dx+dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;

        return true;
    }
}
