package ivan.selfeducation;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class CoverFlowActivity extends Activity {
	
	private GLSurfaceView mGLView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new CoverFlowView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
    
}