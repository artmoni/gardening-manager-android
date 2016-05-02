package net.londatiga.android;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import org.gots.action.ActionOnSeed;
import org.gots.action.GrowingActionInterface;
import org.gots.bean.Allotment;
import org.gots.seed.GrowingSeed;

/**
 * Action item, displayed as menu with icon and text.
 *
 * @author Lorensius. W. L. T <lorenz@londatiga.net>
 *         <p/>
 *         Contributors: - Kevin Peck <kevinwpeck@gmail.com>
 */
public class ActionItem {
    ActionOnSeed seedAction;
    GrowingActionInterface gardenAction;
    int state;
    private Drawable icon;
    private Bitmap thumb;
    private String title;
    private int actionId = -1;
    private boolean selected;
    private boolean sticky;

    /**
     * Constructor
     *
     * @param actionId Action id for case statements
     * @param title    Title
     * @param icon     Icon to use
     */
    public ActionItem(int actionId, ActionOnSeed seedAction, Drawable icon, int state) {
        this.actionId = actionId;
        this.seedAction = seedAction;
        if (seedAction != null) {
            this.state = state;
            this.title = seedAction.getName();
            this.icon = icon;
        }

    }

    public ActionItem(int actionId, GrowingActionInterface gardenAction, Drawable icon) {
        this.title = gardenAction.getName();
        this.icon = icon;
        this.actionId = actionId;
        this.gardenAction = gardenAction;
    }

    /**
     * Get action title
     *
     * @return action title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set action title
     *
     * @param title action title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get action icon
     *
     * @return {@link Drawable} action icon
     */
    public Drawable getIcon() {
        return this.icon;
    }

    /**
     * Set action icon
     *
     * @param icon {@link Drawable} action icon
     */
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /**
     * @return Our action id
     */
    public int getActionId() {
        return actionId;
    }

    /**
     * Set action id
     *
     * @param actionId Action id for this action
     */
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    /**
     * @return true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * Set sticky status of button
     *
     * @param sticky true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    /**
     * Check if item is selected
     *
     * @return true or false
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Set selected flag;
     *
     * @param selected Flag to indicate the item is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Get thumb image
     *
     * @return Thumb image
     */
    public Bitmap getThumb() {
        return this.thumb;
    }

    /**
     * Set thumb
     *
     * @param thumb Thumb image
     */
    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public void execute(GrowingSeed seed) {
        seedAction.execute(seed);
    }

    public void execute(Allotment allotment, GrowingSeed seed) {
        gardenAction.execute(allotment, seed);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
