package org.gots.ui;

import org.gots.R;
import org.gots.ui.fragment.TutorialFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TutorialActivity extends AbstractActivity {

    private int current_fragment = 0;

    int[] tutorialList = { R.layout.tutorial_a, R.layout.tutorial_b, R.layout.tutorial_c };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        Button next = (Button) findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                current_fragment++;
                changeScreen();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void changeScreen() {
        if (current_fragment >= 0 && current_fragment < tutorialList.length) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            TutorialFragment fragment = new TutorialFragment(tutorialList[current_fragment]);
            fragmentTransaction.replace(R.id.fragmentTutorial, fragment);
            fragmentTransaction.commit();
        } else
            finish();
    }

}
