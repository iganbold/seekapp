package net.bluemix.seek.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.bluemix.seek.ConnectionDetector;
import net.bluemix.seek.CustomArrayAdapter;
import net.bluemix.seek.ErrorToast;
import net.bluemix.seek.MySingleton;
import net.bluemix.seek.R;

import net.bluemix.seek.model.Uid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FindFragment extends Fragment implements AbsListView.OnItemClickListener, OnShowcaseEventListener {

    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String EXTRA_URL="url";
    public static final String EXTRA_TAG="tag";
    public static final String EXTRA_NAME="name";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton selectBtn;
    private ImageButton cameraBtn;
    private ImageButton urlBtn;
    private SimpleDraweeView resultIV;
    private ProgressBar loadPBar;
    private ImageButton urlUploadBtn;
    private EditText urlET;

    private FindFragment thisFragment;
    private ConnectionDetector con;
    private ShowcaseView sv;

    private View view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private List<Uid> uidList=new ArrayList<>();

    // TODO: Rename and change types of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FindFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view=getActivity().getLayoutInflater().inflate(R.layout.fragment_item,null);
        con=new ConnectionDetector(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        runFirstTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        thisFragment=this;
        selectBtn=(ImageButton)view.findViewById(R.id.selectBtn);
        resultIV=(SimpleDraweeView)view.findViewById(R.id.resultIV);
        urlET=(EditText)view.findViewById(R.id.urlET);
        urlUploadBtn=(ImageButton)view.findViewById(R.id.urlUploadBtn);
        urlBtn=(ImageButton)view.findViewById(R.id.urlBtn);
        cameraBtn=(ImageButton)view.findViewById(R.id.seekBtn);
        loadPBar=(ProgressBar)view.findViewById(R.id.loadPBar);
        loadPBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadPBar.setVisibility(View.INVISIBLE);
        urlUploadBtn.setVisibility(View.INVISIBLE);
        urlET.setVisibility(View.INVISIBLE);

        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_grow_fade_in_from_bottom));
                urlET.setText("");
                visibleFields();
                clearAdapter();
                checkShowCase();
            }
        });

        urlUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_grow_fade_in_from_bottom));
                String url = urlET.getText().toString();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    if (con.isConnectingToInternet()) {
                        MySingleton.getInstance(getActivity()).getRequestQueue().add(loadStringRequest(url));
                        Uri uri = Uri.parse(url);
                        resultIV.setImageURI(uri);
                        loadPBar.setVisibility(View.VISIBLE);
                        invisibleFields();
                    } else {
                        ErrorToast.makeToast(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                    }

                } else {
                    ErrorToast.makeToast(getActivity(), "URL IS NOT CORRECT", Toast.LENGTH_LONG).show();
                }
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_grow_fade_in_from_bottom));
                invisibleFields();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                checkShowCase();
            }
        });

        return view;
    }

    private void checkShowCase()
    {
        if(sv!=null && sv.isShown())
        {
            sv.hide();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void runFirstTime()
    {
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN_STEP3", true);
        if (isFirstRun)
        {
            // Code to run once
            view.post(new Runnable() {
                @Override
                public void run() {
                    showCase();
                }
            });

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN_STEP3", false);
            editor.commit();
        }
    }

    private void showCase()
    {
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ViewTarget target=new ViewTarget(R.id.urlBtn,getActivity());

        sv=new ShowcaseView.Builder(getActivity(),true)
                .setTarget(target)
                .setContentTitle("#URL & #ALBUM face search")
                .setContentText("Option 1\nIf you like to seek person by image url then copy and paste the url of the face photo.\n" +
                        "\nOption 2\n" +
                        "If you like to seek people by taken photo then just select a photo from your album.")
                .setStyle(R.style.CustomShowcaseTheme3)
                .setShowcaseEventListener(thisFragment)
                .build();
        sv.setButtonPosition(lps);
    }

    public void visibleFields()
    {
        urlUploadBtn.setVisibility(View.VISIBLE);
        urlET.setVisibility(View.VISIBLE);
    }

    public void invisibleFields()
    {
        urlUploadBtn.setVisibility(View.INVISIBLE);
        urlET.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.

            mListener.onFragmentInteraction(""+position);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(PhotoDialog.TAG);

            if (prev != null) {
                ft.remove(prev);
            }

            ft.addToBackStack(null);

            Bundle bundle=new Bundle();
            if(uidList.get(position).getModel()!=null) {
                bundle.putString(EXTRA_URL, uidList.get(position).getModel().getUrls().get(0));
                bundle.putString(EXTRA_TAG, uidList.get(position).getModel().getTag());
                bundle.putString(EXTRA_NAME, uidList.get(position).getModel().getEntryId());
            }

            PhotoDialog dialog = PhotoDialog.newInstance();
            dialog.setArguments(bundle);
            dialog.show(ft, PhotoDialog.TAG);
        }

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==PICK_IMAGE_REQUEST)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                File file=new File(picturePath);
                Uri uri=Uri.fromFile(file);
                resultIV.setImageURI(uri);
                upload(picturePath);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void upload(String picturePath)
    {
        if(con.isConnectingToInternet())
        {
            StringRequest request=uploadFile(picturePath);
            MySingleton.getInstance(getActivity()).getRequestQueue().add(request);
            loadPBar.setVisibility(View.VISIBLE);
        }else
        {
            ErrorToast.makeToast(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
    }

    private StringRequest uploadFile(final String imgPath)
    {
        StringRequest stringRequest=new StringRequest(Request.Method.POST,getString(R.string.image_url_upload), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyImage path:",response);
                MySingleton.getInstance(getActivity()).addToRequestQueue(loadStringRequest(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorToast.makeToast(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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

    private StringRequest loadStringRequest(final String url)
    {
        String localUrl;
        if(url.startsWith("http://") || url.startsWith("https://"))
        {
            localUrl=url;
        }
        else
        {
            localUrl=getString(R.string.image_url)+url;
        }

        StringRequest stringRequest=new StringRequest(Request.Method.GET, getString(R.string.seek_url)+localUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder builder=new GsonBuilder();
                Gson gson=builder.create();
                uidList=gson.fromJson(response, new TypeToken<ArrayList<Uid>>() {}.getType());
                if(uidList!=null && uidList.size()!=0)
                {
                    mAdapter=new CustomArrayAdapter(getActivity(),R.layout.single_row_layout,uidList);
                    ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
                    ((CustomArrayAdapter)mAdapter).notifyDataSetChanged();
                    mListView.setOnItemClickListener(thisFragment);
                }
                else
                {
                    clearAdapter();
                    Toast.makeText(getActivity(), getString(R.string.no_match), Toast.LENGTH_SHORT).show();
                }

                loadPBar.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorToast.makeToast(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("Load Server:", error.toString());
                loadPBar.setVisibility(View.INVISIBLE);
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

    private void clearAdapter()
    {
        if(mAdapter!=null) {
            ((ArrayAdapter<Uid>) mAdapter).clear();
            ((ArrayAdapter<Uid>) mAdapter).notifyDataSetChanged();
        }
    }

}
