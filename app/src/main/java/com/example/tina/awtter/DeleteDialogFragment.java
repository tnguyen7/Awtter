package com.example.tina.awtter;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by richellevital on 7/12/15.
 */
public class DeleteDialogFragment extends DialogFragment {

    public DeleteDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] optionsArr = {"Delete comment"};

        builder.setTitle("Choose Action")
                .setItems(optionsArr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                ((CommentActivity)getActivity()).deleteComment();
                                return;
                        }
                    }
                });

        return builder.create();
    }
}

