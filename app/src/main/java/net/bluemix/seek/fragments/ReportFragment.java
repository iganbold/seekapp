package net.bluemix.seek.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.bluemix.seek.ConnectionDetector;
import net.bluemix.seek.ErrorToast;
import net.bluemix.seek.GPSTracker;
import net.bluemix.seek.HttpUploader;
import net.bluemix.seek.MySingleton;
import net.bluemix.seek.R;
import net.bluemix.seek.model.Geometry;
import net.bluemix.seek.model.PhotoModel;
import net.bluemix.seek.model.TrainAlbumResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private SimpleDraweeView reportIV;
    private ImageButton uploadBtn;
    private ImageButton deadBtn;
    private ImageButton aliveBtn;
    private ImageButton notSureBtn;
    private EditText nameET;
    private EditText noteET;
    private ProgressBar uploadPBar;



    private PhotoModel model;
    private GPSTracker gps;

    private ConnectionDetector con;

    public ReportFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_report, container, false);
        reportIV=(SimpleDraweeView)view.findViewById(R.id.reportIV);
        uploadBtn=(ImageButton)view.findViewById(R.id.uploadBtn);
        deadBtn=(ImageButton)view.findViewById(R.id.deadBtn);
        aliveBtn=(ImageButton)view.findViewById(R.id.aliveBtn);
        notSureBtn=(ImageButton)view.findViewById(R.id.notSureBtn);
        nameET=(EditText)view.findViewById(R.id.nameET);
        noteET=(EditText)view.findViewById(R.id.noteET);
        uploadPBar=(ProgressBar)view.findViewById(R.id.uploadPBar);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        con=new ConnectionDetector(getActivity());

        uploadPBar.setVisibility(View.INVISIBLE);

        model=new PhotoModel();
        gps=new GPSTracker(getActivity());

        setLocation();

        aliveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aliveBtn.setImageResource(R.drawable.check_white);
                deadBtn.setImageResource(R.drawable.dead);
                notSureBtn.setImageResource(R.drawable.not);
                model.setTag(getString(R.string.tag_green));
            }
        });

        deadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadBtn.setImageResource(R.drawable.check_black);
                aliveBtn.setImageResource(R.drawable.alive);
                notSureBtn.setImageResource(R.drawable.not);
                model.setTag(getString(R.string.tag_red));
            }
        });

        notSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notSureBtn.setImageResource(R.drawable.check_gray);
                aliveBtn.setImageResource(R.drawable.alive);
                deadBtn.setImageResource(R.drawable.dead);
                model.setTag(getString(R.string.tag_gray));
            }
        });


        if (bundle != null) {
            final String imgPath=bundle.getString(CameraFragment.EXTRA_PHOTO_FILENAME);

            File imgFile = new File(imgPath);
            if(imgFile.exists()){
                Uri uri = Uri.fromFile(imgFile);
                reportIV.setImageURI(uri);
                uploadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(model.getTag()==null)
                        {
                            ErrorToast.makeToast(getActivity(),getString(R.string.info_tag),Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(con.isConnectingToInternet())
                            {
                                uploadPBar.setVisibility(View.VISIBLE);
                                uploadBtn.setVisibility(View.INVISIBLE);
                                MySingleton.getInstance(getActivity()).getRequestQueue().add(uploadFile(imgPath));
                            }
                            else
                            {
                                ErrorToast.makeToast(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        }
    }

    private void displayShort(String text)
    {
        Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
    }

    private void displayLong(String text)
    {
        Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT).show();
    }

    private void setLocation()
    {
        if(gps.canGetLocation())
        {
            Geometry geo=new Geometry();
            geo.setCordinates(new double[]{gps.getLongitude(),gps.getLatitude()});
            model.setGeo(geo);
        }
        else
        {
            gps.showSettingsAlert();
            model.setGeo(null);
        }
    }

    private JsonObjectRequest postFace(String jsonBoDy) throws JSONException {

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,
                getString(R.string.report_url),
                new JSONObject(jsonBoDy),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        GsonBuilder builder=new GsonBuilder();
                        Gson gson=builder.create();
                        TrainAlbumResponse trainAlbumResponse=gson.fromJson(response.toString(),TrainAlbumResponse.class);

                        if(trainAlbumResponse.getError()!=null)
                        {
                            ErrorToast.makeToast(getActivity(), trainAlbumResponse.getError(), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            loadDialogSuccess();
                        }
                        uploadBtn.setVisibility(View.VISIBLE);
                        uploadPBar.setVisibility(View.INVISIBLE);
                        Log.v("Post Face result :", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayLong(error.toString());
                        Log.v("Post Face Error :", error.toString());
                        uploadBtn.setVisibility(View.VISIBLE);
                        uploadPBar.setVisibility(View.INVISIBLE);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept","*/*");
                headers.put("Cache-Control","no-cache");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        request.setRetryPolicy(policy);

        return request;
    }

    private String prepareJson(PhotoModel model)
    {
        GsonBuilder builder=new GsonBuilder();
        Gson gson=builder.create();
        return gson.toJson(model);
    }

    private String textCompbine(String text)
    {
        String[] strs=text.split(" ");
        String result="";

            for (int i = 0; i < strs.length; i++) {
                result+=strs[i].toLowerCase();
            }

        return result;
    }

    private void loadDialogSuccess() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(SuccessDialog.TAG);

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        SuccessDialog dialog=SuccessDialog.newInstance();
        dialog.show(ft, GeneralButtonDialog.TAG);
        //dialog.setTargetFragment(this,REQUEST_CODE);
    }

    private StringRequest uploadFile(final String imgPath)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST,getString(R.string.image_url_upload), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyImage path:", response);

                String name=textCompbine(nameET.getText().toString());
                String note=noteET.getText().toString();

                model.setEntryId(name);
                model.setNote(note);
                model.setUrls(new ArrayList<String>(Arrays.asList(getString(R.string.image_url) + response)));
                model.setTime(System.currentTimeMillis() + "");
                try {
                    String jsonBody=prepareJson(model);
                    Log.v("Jsonbody: ",jsonBody);
                    MySingleton.getInstance(getActivity()).addToRequestQueue(postFace(jsonBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                    displayShort(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError: ",error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                File imgFile = new File(imgPath);
                Bitmap bitmapOrg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream bao = new ByteArrayOutputStream();

                //Resize the image
                double width = bitmapOrg.getWidth();
                double height = bitmapOrg.getHeight();
                double ratio = 400/width;
                int newheight = (int)(ratio*height);

                bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);

                //Here you can define .PNG as well
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                byte[] ba = bao.toByteArray();
                String encodeFile = Base64.encodeToString(ba, Base64.DEFAULT);

                bitmapOrg.recycle();
                Map<String,String> params = new HashMap<String, String>();
                params.put("image",encodeFile);
                return params;
            }

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

}
