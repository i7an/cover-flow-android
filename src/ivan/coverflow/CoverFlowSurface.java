package ivan.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class CoverFlowSurface extends GLSurfaceView {

	CoverFlowRender mRender;
	
	private float x = 0;

	public CoverFlowSurface(Context context) {
		super(context);
		Bitmap[] bitmaps = new Bitmap[4];
		bitmaps[0] = getBitmapFormRes(R.drawable.sasha);
		bitmaps[1] = getBitmapFormRes(R.drawable.diamond);
		bitmaps[2] = getBitmapFormRes(R.drawable.angelina);
		bitmaps[3] = getBitmapFormRes(R.drawable.lisa);
		mRender = new CoverFlowRender(bitmaps);
		setRenderer(mRender);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		setMeasuredDimension(size, size / 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
	        x = event.getX();
	        mRender.scrollStop();
	    }
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
	        final float xdiff = (x - event.getX());
	        queueEvent(new Runnable() {
	            public void run() {
	                mRender.shiftScene(xdiff / getWidth());
	            }
	        });
	        x = event.getX();
	    }
	    if(event.getAction() == MotionEvent.ACTION_UP) {
	        mRender.scrollLeft();
	    }
		return true;
	}
	
	private Bitmap getBitmapFormRes(int res) {
		return BitmapFactory.decodeResource(getContext().getResources(), res); 
	}
	

}


