package musicq.apps.obg.service;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Checkable;

import musicq.apps.obg.R;

/**
 * Created by d1jun on 2017-12-27.
 */

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    public CheckableLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.add_checkbox);

        if (cb.isChecked() != checked) {
            cb.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.add_checkbox);
        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.add_checkbox);
        setChecked(cb.isChecked() ? false : true);
    }
}
