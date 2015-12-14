package net.bluemix.seek;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by john on 8/1/2015.
 */
public class HttpUploader extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... strs) {
        if (strs[0] == null)
            return null;

        String outPut = null;

        File imgFile = new File(strs[0]);

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
        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("image",ba1));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://imgservice.mybluemix.net/upload.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            // print responce
            outPut = EntityUtils.toString(entity);
            Log.i("GET RESPONSE", outPut);

            //is = entity.getContent();
            Log.e("log_tag ******", "good connection");

            bitmapOrg.recycle();

        } catch (Exception e) {
            Log.e("log_tag ******", "Error in http connection " + e.toString());
        }

        return outPut;
    }
}