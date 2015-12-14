package net.bluemix.seek.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import net.bluemix.seek.MySingleton;
import net.bluemix.seek.R;

import java.util.Map;

/**
 * Created by john on 8/3/2015.
 */
public class PhotoDialog extends DialogFragment{
    public static String TAG="net.bluemix.seek.fragments.SuccessDialog";
    private Dialog dialog;
    private View view;

    private String url;
    private String tag;
    private String name;

    private ImageView tagIV;
    private ImageView requestIV;
    private TextView nameTV;
    private TextView statusTV;

    public static PhotoDialog newInstance()
    {
        PhotoDialog successDialog=new PhotoDialog();
        return successDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(),android.R.style.Theme_Holo_Dialog_NoActionBar);
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_photo, null);

        if(getArguments()!=null) {
            url = getArguments().getString(FindFragment.EXTRA_URL);
            tag = getArguments().getString(FindFragment.EXTRA_TAG);
            name=getArguments().getString(FindFragment.EXTRA_NAME);

            tagIV=(ImageView)view.findViewById(R.id.tagIV);
            requestIV=(ImageView)view.findViewById(R.id.requestImageIV);
            statusTV=(TextView)view.findViewById(R.id.statusTV);
            nameTV=(TextView)view.findViewById(R.id.nameTV);

            if(tag.toLowerCase().equals(getString(R.string.tag_red)))
            {
                tagIV.setImageResource(R.drawable.dead_mark);
                statusTV.setText(getString(R.string.tag_red_word));
            }
            else if(tag.toLowerCase().equals(getString(R.string.tag_green)))
            {
                tagIV.setImageResource(R.drawable.alive_mark);
                statusTV.setText(getString(R.string.tag_green_word));
            }
            else if(tag.toLowerCase().equals(getString(R.string.tag_gray)))
            {
                tagIV.setImageResource(R.drawable.not_mark);
                statusTV.setText(getString(R.string.tag_gray_word));
            }

            nameTV.setText(name.split("1")[0]);


            ImageRequest imageRequest=new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    requestIV.setImageBitmap(response);
                }
            },0,0,null, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(getActivity()).getRequestQueue().add(imageRequest);
        }

        final Drawable d = new ColorDrawable(Color.WHITE);
        d.setAlpha(460);

        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setContentView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationPhoto;

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
        super.onDestroyView();
    }
}
