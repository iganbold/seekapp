package net.bluemix.seek.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.bluemix.seek.ConnectionDetector;
import net.bluemix.seek.MySingleton;
import net.bluemix.seek.R;
import net.bluemix.seek.ReportActivity;
import net.bluemix.seek.ErrorToast;
import net.bluemix.seek.model.PhotoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTabFragment extends Fragment implements OnShowcaseEventListener {

    private final static String TAG = "FirstTabFragment";
    private final static int REQUEST_CODE=1;

    private FirstTabFragment thisFragment=this;
    private ShowcaseView sv;
    //private final ApiUtils apiUtils = new ApiUtils();
    private ConnectionDetector con;

    private List<PhotoModel> models=new ArrayList<>();
    private int[] listRIDs=new int[]
            {
                    R.id.imageView8,
                    R.id.imageView7,
                    R.id.imageView6,
                    R.id.imageView,
                    R.id.imageView2,
                    R.id.imageView3,
                    R.id.imageView5,
                    R.id.imageView15,
                    R.id.imageView14,
                    R.id.imageView13,
                    R.id.imageView9,
                    R.id.imageView10,
                    R.id.imageView11,
                    R.id.imageView12,
                    R.id.imageView22,
                    R.id.imageView21,
                    R.id.imageView20,
                    R.id.imageView16,
                    R.id.imageView17,
                    R.id.imageView18,
                    R.id.imageView19,
                    R.id.imageView29,
                    R.id.imageView28,
                    R.id.imageView27,
                    R.id.imageView23,
                    R.id.imageView24,
                    R.id.imageView25,
                    R.id.imageView26,
                    R.id.imageView58,
                    R.id.imageView57,
                    R.id.imageView56,
                    R.id.imageView52,
                    R.id.imageView53,
                    R.id.imageView54,
                    R.id.imageView55,
                    R.id.imageView48,
                    R.id.imageView47,
                    R.id.imageView46,
                    R.id.imageView45,
                    R.id.imageView49,
                    R.id.imageView50,
                    R.id.imageView51,
                    R.id.imageView41,
                    R.id.imageView40,
                    R.id.imageView38,
                    R.id.imageView39,
                    R.id.imageView42,
                    R.id.imageView43,
                    R.id.imageView44,
                    R.id.imageView37,
                    R.id.imageView36,
                    R.id.imageView35,
                    R.id.imageView31,
                    R.id.imageView32,
                    R.id.imageView33,
                    R.id.imageView34,
            };

    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;
    private TextView broadcastTV;
    private TextView countPeopleTV;
    private ImageView backroundIV;

    private ImageButton genBtn;
    private List<NetworkImageView> imageViewList=new ArrayList<>();

    private View view;

    private NetworkChangeReciever receiver;

    private boolean isShowen=false;

    public static FirstTabFragment newInstance(int sectionNumber) {
        FirstTabFragment fragment = new FirstTabFragment();
        return fragment;
    }

    public FirstTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = MySingleton.getInstance(getActivity()).getImageLoader();
        mRequestQueue = MySingleton.getInstance(getActivity()).getRequestQueue();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReciever();
        getActivity().registerReceiver(receiver, filter);

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_first_tab, null);
        runFirstTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_first_tab, container, false);
        genBtn = (ImageButton) rootView.findViewById(R.id.genBtn);
        genBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim_infinite));
        broadcastTV=(TextView)rootView.findViewById(R.id.broadcastMsgTV);
        countPeopleTV=(TextView)rootView.findViewById(R.id.countPeopleTV);
        backroundIV=(ImageView)rootView.findViewById(R.id.imageViewBackround);

        NetworkImageView networkImageView;
        for (int i = 0; i <listRIDs.length ; i++) {
            networkImageView=(NetworkImageView)rootView.findViewById(listRIDs[i]);
            networkImageView.setDefaultImageResId(R.drawable.focus);
            networkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.zoom_out));

                }
            });

            imageViewList.add(networkImageView);
        }

        genBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(container.getContext(), R.anim.rotate_anim));
                if(sv !=null) {
                    if (sv.isShown()) {
                        sv.hide();
                    }
                }
                loadDialogGenBtn();
            }
        });

        return rootView;
    }

    private void runFirstTime()
    {
        // Code to run once
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN_STEP2", true);
        if (isFirstRun)
        {
            view.post(new Runnable() {
                @Override
                public void run() {
                    showCaseWelcome();
                }
            });
        }
    }

    private void showCaseWelcome()
    {
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target=new ViewTarget(R.id.imageViewBackround,getActivity());

        sv=new ShowcaseView.Builder(getActivity(),true)
                .setTarget(target)
                .setContentTitle(getString(R.string.show_case_app_desc_title))
                .setContentText(getString(R.string.show_case_app_desc))
                .setStyle(R.style.CustomShowcaseTheme2)
                .setShowcaseEventListener(thisFragment)
                .build();
        sv.setButtonPosition(lps);
    }

    private void showCase()
    {
        isShowen=true;
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target=new ViewTarget(R.id.genBtn,getActivity());

        sv=new ShowcaseView.Builder(getActivity(),true)
                .setTarget(target)
                .setContentTitle("\n\n\n\n\n\n\n\n\n\n\n\n"+getString(R.string.show_case_get_btn_title))
                .setContentText(getString(R.string.show_case_gen_btn))
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(thisFragment)
                .build();
        sv.setButtonPosition(lps);

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN_STEP2", true);
        if (isFirstRun)
        {
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN_STEP2", false);
            editor.apply();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(receiver);
    }

    private void loadDialogGenBtn() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(GeneralButtonDialog.TAG);

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        GeneralButtonDialog dialog = GeneralButtonDialog.newInstance();
        dialog.show(ft, GeneralButtonDialog.TAG);
        dialog.setTargetFragment(this, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        genBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim_infinite));
        if (requestCode == REQUEST_CODE) {
            String filename = data
                    .getStringExtra(CameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Log.i(TAG, "filename: " + filename);
                Intent intent=new Intent(getActivity(), ReportActivity.class);
                intent.putExtra(CameraFragment.EXTRA_PHOTO_FILENAME,filename);
                startActivity(intent);
            }

        }
    }

    private StringRequest loadLatestPeople()
    {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, getString(R.string.latest_people_url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("LatestPeople: ",response);
                GsonBuilder builder=new GsonBuilder();
                Gson gson=builder.create();
                models=gson.fromJson(response, new TypeToken<ArrayList<PhotoModel>>() {}.getType());
                if(models!=null)
                {
                    for (int i = 0; i < listRIDs.length; i++) {
                        imageViewList.get(i).setImageUrl(models.get(i).getUrls().get(0),mImageLoader);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorToast.makeToast(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        return stringRequest;

    }

    private StringRequest loadBroadCastMSG()
    {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, getString(R.string.broadcast_msg_url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                broadcastTV.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorToast.makeToast(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                Log.v("", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        return stringRequest;

    }

    private StringRequest loadCountPeople()
    {
        StringRequest stringRequest=new StringRequest(Request.Method.GET, getString(R.string.count_people_url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                countPeopleTV.setText(response+"+");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorToast.makeToast(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                Log.v("", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        return stringRequest;

    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        if (isShowen==false)
        {
            showCase();
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    public class NetworkChangeReciever extends BroadcastReceiver {

        private String LOG_TAG="NetworkChangeReceiver";
        private boolean isConnected;
        @Override
        public void onReceive(final Context context, final Intent intent) {

            Log.v(LOG_TAG, "Receieved notification about network status");
            isNetworkAvailable(context);

        }

        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if(!isConnected){
                                Log.v(LOG_TAG, "Now you are connected to Internet!");
                                //CustomToast.makeToast(getActivity(), getString(R.string.yes_connection), Toast.LENGTH_LONG).show();
                                mRequestQueue.add(loadBroadCastMSG());
                                mRequestQueue.add(loadCountPeople());
                                mRequestQueue.add(loadLatestPeople());
                                isConnected = true;
                            }
                            return true;
                        }
                    }
                }
            }
            Log.v(LOG_TAG, "You are not connected to Internet!");
            ErrorToast.makeToast(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            isConnected = false;
            return false;
        }
    }

}
