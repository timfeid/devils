package com.timfeid.devils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class BoldFontTextView extends android.support.v7.widget.AppCompatTextView {
    public BoldFontTextView(Context context) {
        super(context);
        setFont();
    }
    public BoldFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public BoldFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Oswald-Bold.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
