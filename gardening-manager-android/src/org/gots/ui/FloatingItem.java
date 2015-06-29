package org.gots.ui;

import android.view.View;

public class FloatingItem {
    private String title;

    private View.OnClickListener onClickListener;

    private int ressourceId;

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

    public int getRessourceId() {
        return ressourceId;
    }

    public void setRessourceId(int ressourceId) {
        this.ressourceId = ressourceId;
    }

}
