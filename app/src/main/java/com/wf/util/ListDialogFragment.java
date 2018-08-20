package com.wf.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

/**
 * Created by ljh102 on 2016/4/17.
 * 列表选择弹出窗口
 */
public class ListDialogFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder= new AlertDialog.Builder(this.getContext());
        builder.setTitle(mTitle).setItems(mCharSequences, mOnClickListener);
        mOnClickListener = null;

//        AlertDialog alertDialog = builder.create();
//        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mOnClickListener = null;
//            }
//        });
        return builder.create();
    }

    static private CharSequence[] mCharSequences;
    static private DialogInterface.OnClickListener mOnClickListener;
    static private String mTitle;

    static public ListDialogFragment show(FragmentManager fragmentManager, String title,  CharSequence[]charSequences, DialogInterface.OnClickListener onClickListener){
        mTitle = title;
        mCharSequences = charSequences;
        mOnClickListener = onClickListener;
        ListDialogFragment dialogFragment = new ListDialogFragment();
        dialogFragment.show(fragmentManager, "ListDialogFragment");
        return dialogFragment;
    }
}
