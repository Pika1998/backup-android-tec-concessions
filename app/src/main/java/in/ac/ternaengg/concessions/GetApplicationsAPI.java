package in.ac.ternaengg.concessions;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetApplicationsAPI extends AsyncTask<String,String,String> {

    interface  GetApplicationsAPIListner {
        void gotApplications(String o);
    }

    private GetApplicationsAPIListner listner;

    public void setListner(GetApplicationsAPIListner listner) {
        this.listner = listner;
    }

    public static  final String api_call_url = "http://192.168.43.15:27018/getAllPendingApplications";
    BufferedReader reader = null;

    public void execute(){
        execute("test");
    }
    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        if(listner!=null)
        {
            listner.gotApplications(o);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String JSONResponse = null;
        try{
            URL url = new URL(api_call_url);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();

            InputStream inputStream =urlConnection.getInputStream();
            StringBuffer buffer =new StringBuffer();
            if(inputStream==null)
            {
                return  "failed";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return "failed";
            }
            JSONResponse = buffer.toString();
            Log.i("GetApplicationsAPI","RESPONSE DATA - "+JSONResponse.toString());
            return JSONResponse;
        }catch (Exception e)
        {
            Log.d("GetApplicationsAPI","[FAILED TO MAKE API CALL]");
            Log.d("GetApplicationsAPI",e.toString());
        }
        return "failed";
    }
}
