package de.vv.android.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by Marko Nalis on 15.09.2015.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable{


    private boolean isChecked = false;

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        this.isChecked = isChecked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        this.isChecked = !this.isChecked;
        //changeColor(this.isChecked);
    }
}
