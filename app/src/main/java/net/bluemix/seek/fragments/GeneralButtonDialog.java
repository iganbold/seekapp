package net.bluemix.seek.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import net.bluemix.seek.CameraActivity;
import net.bluemix.seek.R;
import net.bluemix.seek.ReportActivity;
import net.bluemix.seek.SeekActivity;

/**
 * Created by john on 7/28/2015.
 */
public class GeneralButtonDialog extends DialogFragment implements OnShowcaseEventListener{

    public static final String TAG="net.bluemix.seek.fragments.GeneralButtonDialog";
    public static int donateTAG=0;
    private Dialog dialog;

    private ImageButton genBtn;
    private ImageButton reportBtn;
    private ImageButton seekBtn;
    private ImageButton donateBtn;
    private View view;
    private TextView thankYouTV;
    private ImageView donateIV;

    private Button btn50Cent;
    private Button btn1Dol;
    private Button btn5Dol;
    private Button btn10Dol;

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btn50Cent:
                    donateAction(1);
                    donateImage(R.drawable.hug);
                    break;
                case R.id.btn1Dol:
                    donateAction(2);
                    donateImage(R.drawable.hope);
                    break;
                case R.id.btn5Dol:
                    donateAction(3);
                    donateImage(R.drawable.smile);
                    break;
                case R.id.btn10Dol:
                    donateAction(4);
                    donateImage(R.drawable.thank);
                    break;
                case R.id.donateBtn:
                    moneyBtnVisible();
            }

        }
    };

    private void donateAction(int tag)
    {
        donateTAG=tag;
        thankYouTV.setVisibility(View.VISIBLE);
        thankYouTV.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        moneyBtnInvisible();
    }

    private void donateImage(int image)
    {
        donateIV.setImageResource(image);
        donateIV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_donate_logo));
    }


    private void moneyBtnInvisible()
    {
        btn50Cent.setVisibility(View.INVISIBLE);
        btn10Dol.setVisibility(View.INVISIBLE);
        btn1Dol.setVisibility(View.INVISIBLE);
        btn5Dol.setVisibility(View.INVISIBLE);
    }

    private void moneyBtnVisible()
    {
        btn50Cent.setVisibility(View.VISIBLE);
        btn10Dol.setVisibility(View.VISIBLE);
        btn1Dol.setVisibility(View.VISIBLE);
        btn5Dol.setVisibility(View.VISIBLE);
    }

    private void thankYouBtnInvisible()
    {
        thankYouTV.setVisibility(View.INVISIBLE);
    }

    private ShowcaseView sv;

    //private DialogFragment dialogFragmentThis=this;

    public static GeneralButtonDialog newInstance()
    {
        GeneralButtonDialog generalButtonFragment=new GeneralButtonDialog();
        return generalButtonFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(),android.R.style.Theme_Holo_Dialog_NoActionBar);
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_general_button, null);

        thankYouTV=(TextView)view.findViewById(R.id.thankYouTV);
        genBtn=(ImageButton)view.findViewById(R.id.getBtnDialog);
        reportBtn=(ImageButton)view.findViewById(R.id.reportBtn);
        seekBtn=(ImageButton)view.findViewById(R.id.seekBtn);
        donateBtn=(ImageButton)view.findViewById(R.id.donateBtn);
        donateIV=(ImageView)view.findViewById(R.id.donateIV);
        btn50Cent=(Button)view.findViewById(R.id.btn50Cent);
        btn1Dol=(Button)view.findViewById(R.id.btn1Dol);
        btn5Dol=(Button)view.findViewById(R.id.btn5Dol);
        btn10Dol=(Button)view.findViewById(R.id.btn10Dol);

        donateBtn.setOnClickListener(onClickListener);
        btn1Dol.setOnClickListener(onClickListener);
        btn5Dol.setOnClickListener(onClickListener);
        btn10Dol.setOnClickListener(onClickListener);
        btn50Cent.setOnClickListener(onClickListener);

        donateIV.setVisibility(View.INVISIBLE);
        thankYouBtnInvisible();
        moneyBtnInvisible();

        switch (donateTAG)
        {
            case 1:
                donateImage(R.drawable.hug);
                break;
            case 2:
                donateImage(R.drawable.hope);
                break;
            case 3:
                donateImage(R.drawable.smile);
                break;
            case 4:
                donateImage(R.drawable.thank);
                break;
        }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.dialog_general_button,null);


        genBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.startAnimation(AnimationUtils.loadAnimation(container.getContext(),R.anim.rotate_anim));
                getDialog().dismiss();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moneyBtnInvisible();
                Intent i=new Intent(getActivity(), CameraActivity.class);
                //startActivity(i);
                startActivityForResult(i, getTargetRequestCode());
                //getDialog().dismiss();
            }
        });

        seekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyBtnInvisible();
                Intent i=new Intent(getActivity(), SeekActivity.class);
                //startActivityForResult(i, getTargetRequestCode());
                startActivity(i);
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if(getDialog()!=null && getRetainInstance())
        {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == getTargetRequestCode()) {

            String filename = data
                    .getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Log.i(TAG, "filename: " + filename);
            }

            getTargetFragment().onActivityResult(getTargetRequestCode(), 0, data);

        }
        getDialog().dismiss();
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }
}
