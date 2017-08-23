package benyamephrem.tilt.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import benyamephrem.tilt.R;


/**
 * Created by Vista on 3/6/15.
 */
public class InstructionsFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("How To Play")
                .setMessage(getResources().getString(R.string.instrictions_string))
                .setPositiveButton(getResources().getString(R.string.will_string), null);

        AlertDialog dialog = builder.create();
        return dialog;

    }
}
