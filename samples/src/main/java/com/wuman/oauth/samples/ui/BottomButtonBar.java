package com.wuman.oauth.samples.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuman.oauth.samples.R;


public class BottomButtonBar extends LinearLayout {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BottomButtonBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setId(R.id.bottom_button_bar);
    }

    public BottomButtonBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setId(R.id.bottom_button_bar);
    }

    public BottomButtonBar(Context context) {
        super(context);
        setId(R.id.bottom_button_bar);
    }

    public Button getButton(int id) {
        Object tag = getTag(id);
        if (tag == null) {
            tag = findViewById(id);
            setTag(id);
        }
        if (tag instanceof Button) {
            return ((Button) tag);
        }
        return null;
    }

    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setOnClickListener(listener);
            }
        }
    }

    public void setVisibility(int visibility, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setVisibility(visibility);
            }
        }
    }

    public void setEnabled(boolean enabled, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setEnabled(enabled);
            }
        }
    }

    public void setText(int resId, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setText(resId);
            }
        }
    }

    public void setText(int resId, TextView.BufferType type, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setText(resId, type);
            }
        }
    }

    public void setText(CharSequence text, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setText(text);
            }
        }
    }

    public void setText(CharSequence text, TextView.BufferType type, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setText(text, type);
            }
        }
    }

    public void setBackgroundResource(int resId, int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setBackgroundResource(resId);
            }
        }
    }

    public void setContentDescription(CharSequence contentDescription,
                                      int... ids) {
        for (int id : ids) {
            Button button = getButton(id);
            if (button != null) {
                button.setContentDescription(contentDescription);
            }
        }
    }

    public void setVisibility(Context context, boolean visible) {
        if (visible && getVisibility() != View.VISIBLE) {
            setAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.pull_up_bottom));
            setVisibility(View.VISIBLE);
        } else if (!visible && getVisibility() != View.GONE) {
            setAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.pull_down_bottom));
            setVisibility(View.GONE);
        }
    }

}
