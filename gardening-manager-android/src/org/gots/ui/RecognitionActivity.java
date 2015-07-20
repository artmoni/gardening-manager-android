package org.gots.ui;

import android.os.Bundle;

import org.gots.ui.BaseGotsActivity;
import org.gots.ui.fragment.LikeThatFragment;

/**
 * Created by sfleury on 20/07/15.
 */
public class RecognitionActivity extends BaseGotsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentLayout(new LikeThatFragment(),null);
    }

    @Override
    protected boolean requireFloatingButton() {
        return false;
    }
}
