package com.fantasticfive.shareback.concept2.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.fantasticfive.shareback.concept2.SharebackURLs;
import com.fantasticfive.shareback.concept2.exception.HttpIOException;
import com.fantasticfive.shareback.concept2.exception.NoInternetException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by sagar on 19/2/17.
 */
public class HTTPConnectionHandler extends AsyncTask<URL,Void,String>{

    private static final String TAG = "MY TAG";
    Callback callback;
    Context context;
    public HTTPConnectionHandler(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    public void makeRequest(String strURL, JSONObject params) throws MalformedURLException, NoInternetException, JSONException {
        String strParams = getGETDataString(params);
        URL url = new URL(strURL+"?"+strParams);
        if(isConnectedToInternet()){
            Log.i(TAG, "makeRequest: Calling URL "+url.toString());
            execute(url);
        }
        else{
            throw new NoInternetException();
        }
    }

    private String getGETDataString(JSONObject params) throws JSONException {
        StringBuilder result = new StringBuilder();
        Iterator<String> itr = params.keys();
        boolean first = true;

        while (itr.hasNext()){
            String key = itr.next();
            String value = params.getString(key);
            if(first) first = false;
            else result.append("&");

            result.append(key);
            result.append("=");
            result.append(value);
        }
        return result.toString();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];
        StringBuilder strBuilder = new StringBuilder();
        HttpURLConnection connection;

        //return testCreateSession();
        if(url.toString().contains(SharebackURLs.CREATE_SESSION))
            return testCreateSession();
        else if(url.toString().contains(SharebackURLs.FETCH_SESSION))
            return testFetchSession();
        else return null;

       /* try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(connection.getInputStream());
            int data = is.read();
            while (data != -1) {
                char current = (char) data;
                strBuilder.append(current);
                data = is.read();
            }
            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    @Override
    protected void onPostExecute(String result) {
        if(result == null)
            callback.onHttpConnectionFailure(new HttpIOException());
        else
            callback.onHttpResponse();
        super.onPostExecute(result);
    }

    protected String testCreateSession(){
        Log.i(TAG, "testCreateSession: Returning Sample Url: ");
        return "{sid:\"ABCDABCDABCDABCDABCDABCDABCDABCD\"}";
    }

    protected String testFetchSession(){
        Log.i(TAG, "testFetchSession: Returning Test SessionList");
        return "[{sid:\"ABCDABCDABCDABCDABCDABCDABCDABCD\",sname:\"DSPD\", iname:\"Mr. Sagar Pawar\"}]";
    }

    public interface Callback{
        void onHttpResponse();
        void onHttpConnectionFailure(HttpIOException e);
    }
}
