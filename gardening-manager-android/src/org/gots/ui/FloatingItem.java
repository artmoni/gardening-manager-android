package org.gots.ui;

import android.view.View;
import android.view.View.OnLongClickListener;

public class FloatingItem {
    private String title;

    private View.OnClickListener onClickListener;

    private int ressourceId;

    private OnLongClickListener onLongClickListener;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    public OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public int getRessourceId() {
        return ressourceId;
    }

    public void setRessourceId(int ressourceId) {
        this.ressourceId = ressourceId;
    }

}
