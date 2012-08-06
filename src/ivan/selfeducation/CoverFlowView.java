package ivan.selfeducation;

import ivan.selfeducation.objects.Cover;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.MotionEvent;

public class CoverFlowView extends GLSurfaceView {

	Render mRender;
	
	private float x = 0;

	public CoverFlowView(Context context) {
		super(context);
		mRender = new Render(context);
		setRenderer(mRender);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
		setMeasuredDimension(size, size);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
	        x = event.getX();
	    }
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
	        final float xdiff = (x - event.getX());
	        queueEvent(new Runnable() {
	            public void run() {
	                mRender.moveCovers(xdiff / getWidth());
	            }
	        });
	        x = event.getX();
	    }
		return true;
	}

	private class Render implements GLSurfaceView.Renderer {

		Cover covers[];
		float sceneHalfWidth;
		int coversNum;
		float pos = 0f;
		
		float D;
		float d;
		float k;
		
		Context context;
		
		public Render(Context context) {
			sceneHalfWidth = 5f;
			coversNum = 5;
			
			this.context = context;
			D = 1.5f;
			d = 0.3f;
			k = D;
		}
		
		private float dToD(float x) {	
			double twoPi = Math.PI * 2;
			if(Math.abs(x) < d) {
				if(x < 0) {
					float x_ = x + d;
					float t = x_/d;
					float addX =  (float) (k*t - k/twoPi*Math.cos(twoPi*t - Math.PI/2));
					return -D + x + addX;
				} else {
					float x_ = x;
					float t = x_/d;
					float addX =  (float) (k*t - k/twoPi*Math.cos(twoPi*t - Math.PI/2));
					return x + addX;
				}
			} else {
				return (x < 0) ? (x - D) : (x + D);
			}
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {			
			if(h == 0) { h = 1; }
			
			gl.glViewport(0, 0, w, h);

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, 50f, w/h, 1f, 10f);

			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		public void onDrawFrame(GL10 gl) {
			gl.glClearColor(0f, 0f, 0f, 1f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
			GLU.gluLookAt(gl, 0f, 0f, 6f, 0f, 0f, 0f, 0f, 1f, 0f);
			for(Cover cover : covers) {
				cover.draw(gl);
			}
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			covers = new Cover[coversNum];
			for(int i = 0; i < coversNum; i++) {
				covers[i] = new Cover(1f);
				covers[i].loadGLTexture(gl, this.context);
			}
			initCoversPositions();
						
			gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
			gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
			gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
			gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
			gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
			
			//Really Nice Perspective Calculations
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
			
			
		}

		public void moveCovers(float delta) {
			pos = pos - delta * 2;
			initCoversPositions();
		}
		
		public void initCoversPositions() {
			for(int i = 0; i < coversNum; i++) {
				Cover cover = covers[i];
				cover.setPosition(dToD(pos + d * i));
				cover.setAngle(getAngelForPosition(cover.getPosition()));
			}
		}
		
		public float getAngelForPosition(float position) {
			if(Math.abs(position) < 1.5) {
			    return 90f- 180f * (position + 1.5f)/3;
			} else {
				return position < 0 ? 90f : -90f;	
			}
		}

	}

}
