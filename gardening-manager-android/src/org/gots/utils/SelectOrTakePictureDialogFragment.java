package org.gots.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectOrTakePictureDialogFragment extends DialogFragment {
    CharSequence[] items = {"Select Picture", "Take Photo"};

    PictureSelectorListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Picture").setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mListener.onSelectInGallery(SelectOrTakePictureDialogFragment.this);
                        break;
                    case 1:
                        mListener.onTakePicture(SelectOrTakePictureDialogFragment.this);
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PictureSelectorListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement PictureSelectorListener");
        }
    }

    public interface PictureSelectorListener {
        public void onSelectInGallery(DialogFragment fragment);

        public void onTakePicture(DialogFragment fragment);
    }

}
