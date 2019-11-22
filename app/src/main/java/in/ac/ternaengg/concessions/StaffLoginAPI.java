package in.ac.ternaengg.concessions;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class StaffLoginAPI extends AsyncTask<String,String,String> {
    public static  final String api_call_url = "http://192.168.43.15:27018/loginStaff";
    OutputStream out = null;
    JSONObject postDict = new JSONObject();
    BufferedReader reader = null;

    public void execute(String username, String password){
        try {
            Log.d("StaffLoginAPI","[StudentLoginAPI] username - "+username);
            Log.d("StaffLoginAPI","[StudentLoginAPI] Password - "+password);
            postDict.put("username",username);
            postDict.put("password",password);
        } catch (JSONException e) {
            Log.d("StaffLoginAPI","Failed To Put Uername and Password in Dictionary");
        }
        if(postDict.length()>0)
        {
            execute(postDict.toString());
        }
    }

    interface StaffLoginAPIListner{
        void gotStaff(String o);
    }

    private StaffLoginAPIListner listner;

    public void setListner(StaffLoginAPIListner listner) {
        this.listner = listner;
    }


    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        if(listner!=null)
        {
            listner.gotStaff(o);
        }
    }
    @Override
    protected String doInBackground(String... strings) {
        String JSONResponse = null;
        String JSONData = strings[0];

        try{
            URL url = new URL(api_call_url);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Accept","application/json");

            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            writer.write(JSONData);
            writer.close();
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
            Log.i("StaffLoginAPI","RESPONSE DATA - "+JSONResponse.toString());
            return JSONResponse;
        }catch (Exception e)
        {
            Log.d("StaffLoginAPI","[FAILED TO MAKE API CALL]");
            Log.d("StaffLoginAPI",e.toString());
        }
        return "failed";
    }
}
