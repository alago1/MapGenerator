package ScreenElements;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapGLSurfaceView extends GLSurfaceView {

    private MapRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 50.0f / 320;
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

                if(y > getHeight()/2)
                    dx *= -1;
                if(x < getWidth()/2)
                    dy *= -1;

                float new_angle = renderer.getMapAngle() + +((dx+dy) * TOUCH_SCALE_FACTOR);
                renderer.setMapAngle(new_angle);
                requestRender();
        }

        previousX = x;
        previousY = y;

        return true;
    }
}
