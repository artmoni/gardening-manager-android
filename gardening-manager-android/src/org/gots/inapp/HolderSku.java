package org.gots.inapp;

/**
 * Created by sfleury on 19/10/15.
 */
public class HolderSku {
    private String sku;
    private boolean consumable = false;

    public HolderSku(String featureSKU, boolean consumable) {
        setSku(featureSKU);
        setConsumable(consumable);
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public void setConsumable(boolean consumable) {
        this.consumable = consumable;
    }
}
