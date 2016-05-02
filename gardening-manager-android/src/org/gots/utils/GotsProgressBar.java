/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.gots.R;

public class GotsProgressBar extends View {

    final public static int HORIZONTAL_STYLE = 1;
    final public static int VERTICAL_STYLE = 2;
    private int mMax = 1;
    private int mProgress = 1;
    private int orientation = HORIZONTAL_STYLE;

    public GotsProgressBar(Context context) {
        super(context);
        setMeasuredDimension(10, getMeasuredHeight());
    }

    public GotsProgressBar(Context context, int orientation) {
        super(context);
        setMeasuredDimension(10, getMeasuredHeight());
        this.orientation = orientation;
    }

    public GotsProgressBar(Context context, AttributeSet as) {
        super(context, as);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (orientation == VERTICAL_STYLE)
            setMeasuredDimension(10, MeasureSpec.getSize(heightMeasureSpec));
        else
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        Paint paint = new Paint();
        paint.setStyle(Style.FILL);

        paint.setColor(getContext().getResources().getColor(R.color.text_color_dark));
        canvas.drawLine(0, 0, 0, getHeight() - 1, paint);
        canvas.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1, paint);
        canvas.drawLine(0, 0, getWidth() - 1, 0, paint);
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);

        paint.setColor(Color.argb(200, 255, 255, 255));
        RectF rectbg;
        rectbg = new RectF(1, 1, getWidth() - 1, getHeight() - 1);
        // else
        // rectbg = new RectF(1, getHeight() - 1, 1, getWidth() - 1);
        canvas.drawRect(rectbg, paint);

        Float percent = new Float(mProgress) / new Float(mMax) * 100f;

        if (percent > 100f)
            percent = 100f;
        if (percent >= 90f)
            paint.setColor(getContext().getResources().getColor(R.color.action_error_color));

        else if (percent >= 80f)
            // paint.setColor(Color.rgb(255, 140, 0));
            paint.setColor(getContext().getResources().getColor(R.color.action_warning_color));

        else
            // paint.setARGB(255, 80, 150, 30);
            paint.setColor(getContext().getResources().getColor(R.color.action_ok_color));

        // int height=new Float(f*getHeight()).intValue();

        RectF rect2;
        if (orientation == VERTICAL_STYLE) {
            int f = (getHeight() - 1) * percent.intValue() / 100;
            rect2 = new RectF(1, getHeight() - 1, getWidth() - 1, getHeight() - f);
        } else {
            int f = (getWidth() - 1) * percent.intValue() / 100;
            rect2 = new RectF(1, 1, f, getHeight() - 1);

        }
        canvas.drawRect(rect2, paint);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

}
