package ivan.coverflow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CoverFlow extends LinearLayout {
	
	private TextView title;
	private CoverFlowSurface surf;

	public CoverFlow(Context context) {
		this(context, null);
	}

	public CoverFlow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		title = new TextView(context);
		title.setText("Hello");
		title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		title.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				
		surf = new CoverFlowSurface(context);
				
		setOrientation(LinearLayout.VERTICAL);
		
		addView(title);
		addView(surf);
	}

}
