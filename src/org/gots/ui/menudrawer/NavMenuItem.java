package org.gots.ui.menudrawer;

public class NavMenuItem {
    private String title;

    private String count;

    private int icon;

    private boolean isCounterVisible = false;

    public NavMenuItem(String title, int icon, String count) {
        setTitle(title);
        setIcon(icon);
        setCount(count);
    }

    public String getCount() {
        return count;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setCounterVisible(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCounterVisible() {
        return isCounterVisible;
    }
}
