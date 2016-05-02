package org.gots.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.gots.R;

public class TutorialFragment extends BaseGotsFragment {

    TextView textView;
    private int mCurrentTutorialRessource;
    private ImageView image;

    public TutorialFragment() {
    }

    public TutorialFragment(int currentRessource) {
        mCurrentTutorialRessource = currentRessource;
    }

    @Override
    public void update() {
        requireAsyncDataRetrieval();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (mCurrentTutorialRessource == 0)
            mCurrentTutorialRessource = R.layout.tutorial_a;
        view = inflater.inflate(mCurrentTutorialRessource, container, false);
        image = (ImageView) view.findViewById(R.id.imageView1);
        textView = (TextView) view.findViewById(R.id.textView1);
        if (textView != null) {
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "bilbo.ttf");
            textView.setTypeface(tf);
            textView.setTextSize(30);
        }
//       TextView description = (TextView) view.findViewById(R.id.textView2);
//        if (description != null) {
//            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "infini-gras.ttf");
//            description.setTypeface(tf);
//        }
        return view;
    }

    @Override
    public void onResume() {
        if (image != null) {
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_from_top);
            image.startAnimation(myFadeInAnimation);

        }
        super.onResume();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }
}