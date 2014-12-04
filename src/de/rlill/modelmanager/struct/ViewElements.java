package de.rlill.modelmanager.struct;

import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Today;

public abstract class ViewElements {
    protected View adaptedView;
    private SparseArray<TextView> attributeTextViews;
    private Model contextModel;
    private Today contextToday;
    private int contextInt;
    private Fragment updateableView;

	public ViewElements(View view) {
    	adaptedView = view;
        attributeTextViews = new SparseArray<TextView>();

        // find all buttons/images
        if (view instanceof ViewGroup) {
        	registerThis((ViewGroup)view);
        }
	}

	public Model getContextModel() {
		return contextModel;
	}

	public void setContextModel(Model contextModel) {
		this.contextModel = contextModel;
	}

	public Today getContextToday() {
		return contextToday;
	}

	public void setContextToday(Today contextToday) {
		this.contextToday = contextToday;
	}

	public int getContextInt() {
		return contextInt;
	}

	public void setContextInt(int contextInt) {
		this.contextInt = contextInt;
	}

	public void setUpdateableView(Fragment updateableView) {
		this.updateableView = updateableView;
	}

	public View getAdaptedView() {
		return adaptedView;
	}

	public SparseArray<String> getFormularData() { return null; }

	public void updateParentView() {
		if (updateableView != null) updateableView.onResume();
	}

	protected TextView findTextView(int id) {
		TextView tv = attributeTextViews.get(id);
		if (tv != null) return tv;
		tv = (TextView) adaptedView.findViewById(id);
		attributeTextViews.put(id, tv);
		return tv;
	}

	private void registerThis(ViewGroup vg) {
    	for (int i = 0; i < vg.getChildCount(); i++) {
    		View v = vg.getChildAt(i);
    		if (v instanceof ViewGroup) registerThis((ViewGroup)v);
    		else if ((v instanceof Button) || (v instanceof ImageView)) v.setTag(this);
    	}
	}
}
