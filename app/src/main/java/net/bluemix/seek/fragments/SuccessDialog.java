package net.bluemix.seek.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import net.bluemix.seek.R;

/**
 * Created by john on 8/3/2015.
 */
public class SuccessDialog extends DialogFragment{
    public static String TAG="net.bluemix.seek.fragments.SuccessDialog";
    private Dialog dialog;
    private View view;

    public static SuccessDialog newInstance()
    {
        SuccessDialog successDialog=new SuccessDialog();
        return successDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(),android.R.style.Theme_Holo_Dialog_NoActionBar);
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_success, null);

        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(460);

        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setContentView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        return dialog;
    }

    @Override
    public void onDestroyView() {
        if(getDialog()!=null && getRetainInstance())
        {
            getDialog().setDismissMessage(null);

        }
        getActivity().finish();
        super.onDestroyView();
    }
}
