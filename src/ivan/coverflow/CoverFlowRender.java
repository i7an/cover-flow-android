package ivan.coverflow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

public class CoverFlowRender implements GLSurfaceView.Renderer {

    public boolean restore = false;

	private final float PERSP_FOVY = 50f;
	private final float PERSP_NEAR = 1f;
	private final float PERSP_FAR  = 10f;

	private final float CLEAR_COLOR_RED    = 0f;
	private final float CLEAR_COLOR_GREEN  = 0f;
	private final float CLEAR_COLOR_BLUE   = 0.5f;

	private final float EYE_X = 0f;
	private final float EYE_Y = 0f;
	private final float EYE_Z = 3.6f;

	private final float d = 0.3f;
	private final float D = 1.5f; 

	private int[] textures;
	private float scene_offset = 0f;

	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	
	private Bitmap[] bitmaps;

	public CoverFlowRender(Bitmap[] bitmaps) {
		float vertices[] = {
				-1f, 1f, 0f,
				-1f, -1f, 0f,
				1f, 1f, 0f,
				1f, -1f, 0f						 
		};
		float texture[] = {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 0.0f,
				1.0f, 1.0f

		};
		
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = vertexByteBuffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		ByteBuffer textureByteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
		textureByteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = textureByteBuffer.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
		this.bitmaps = bitmaps;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClearColor(CLEAR_COLOR_RED, CLEAR_COLOR_GREEN, CLEAR_COLOR_BLUE, 1f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, EYE_X, EYE_Y, EYE_Z, 0f, 0f, 0f, 0f, 1f, 0f);

		float offset = scene_offset;
		for(int i = 0; i < textures.length; i++) {
			gl.glPushMatrix();
				gl.glFrontFace(GL10.GL_CCW);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
				gl.glTranslatef(getPosition(offset), 0, 0);
				gl.glRotatef(getAngel(offset), 0, 1, 0);
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glPopMatrix();
			
			offset += d;
		}

		if(restore) {
			float delta = offset - d * ((int)(offset / d));
			delta = (delta < 0) ? (d - delta) : delta;

			if(delta < 0.006f) {
				scene_offset -= delta;
				restore = false;
				return;
			}

			if(delta < d / 2) {
				scene_offset -= 0.006f;
			} else {
				scene_offset += 0.006f;
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		h = (h == 0) ? 1 : h;		
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, PERSP_FOVY, w/h, PERSP_NEAR, PERSP_FAR);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {					
		gl.glEnable(GL10.GL_TEXTURE_2D);			            // Enable Texture Mapping ( NEW )
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);    
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);           

		gl.glShadeModel(GL10.GL_SMOOTH);                        // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                // Black Background
		gl.glClearDepthf(1.0f);                                 // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST);                        // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL);                         // The Type Of Depth Testing To Do

		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
		genTextures(gl);
	}

	protected void genTextures(GL10 gl) {
		textures = new int[bitmaps.length];
		// generate texture names
		gl.glGenTextures(textures.length, textures, 0);
		for(int i = 0; i < textures.length; i++) { 
			//bind a named texture to a texturing target
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
			//Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			//specify a two-dimensional texture image from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmaps[i], 0); 
		}
	}

	private float getAngel(float offset) {		
		if(Math.abs(offset) < d) {
		    return - 90f * offset / d;
		} else {
			return (offset < 0f) ? 90f : -90f;
		}
	}
	
	private float getPosition(float offset) {
		// TODO: refactor this shit		
		double twoPi = Math.PI * 2;
		if(Math.abs(offset) < d) {
			if(offset < 0) {
				float x_ = offset + d;
				float t = x_/d;
				float addX =  (float) (D*t - D/twoPi*Math.cos(twoPi*t - Math.PI/2));
				return -D + offset + addX;
			} else {
				float x_ = offset;
				float t = x_/d;
				float addX =  (float) (D*t - D/twoPi*Math.cos(twoPi*t - Math.PI/2));
				return offset + addX;
			}
		} else {
			return (offset < 0) ? (offset - D) : (offset + D);
		}
	}
	
	public void shiftScene(float delta) {
		scene_offset -= delta;
	}

}
