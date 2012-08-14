package ivan.coverflow;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	
	CoverFlow coverFlow;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coverFlow = new CoverFlow(this);
        setContentView(coverFlow);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        coverFlow.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        coverFlow.onResume();
    }
    
}