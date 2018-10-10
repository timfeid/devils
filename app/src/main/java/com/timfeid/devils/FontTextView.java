package com.timfeid.devils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontTextView extends android.support.v7.widget.AppCompatTextView {
    public FontTextView(Context context) {
        super(context);
        setFont();
    }
    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Oswald-Light.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
