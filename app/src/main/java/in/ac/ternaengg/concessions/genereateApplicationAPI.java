package in.ac.ternaengg.concessions;

import android.content.SharedPreferences;
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

public class genereateApplicationAPI extends AsyncTask<String, String, String> {

    interface generateApplicationAPIListner {
        void generatedApplication(String o);
    }
    private  generateApplicationAPIListner listner;

    public void setListner(generateApplicationAPIListner listner) {
        this.listner = listner;
    }

    public static  final String api_call_url = "http://192.168.43.15:27018/generateApplication";
    OutputStream out = null;
    SharedPreferences sharedPreferences;
    JSONObject postDict = new JSONObject();
    BufferedReader reader = null;
    public void execute(String applicant_id, String applicant_first_name,String applicant_last_name,String applicant_gender, String applicant_pass_duration,String applicant_pass_category,String applicant_pass_location_from,String applicant_pass_location_to){
        try {
            Log.d("generateApplicationAPI","[StudentLoginAPI] Applicant Gender - "+applicant_gender);
            Log.d("generateApplicationAPI","[StudentLoginAPI] Pass duration - "+applicant_pass_duration);
            Log.d("generateApplicationAPI","[StudentLoginAPI] Pass Category - "+applicant_pass_category);
            Log.d("generateApplicationAPI","[StudentLoginAPI] FROM Location - "+applicant_pass_location_from);
            Log.d("generateApplicationAPI","[StudentLoginAPI] TO Location - "+applicant_pass_location_to);
            postDict.put("applicant_id",applicant_id);
            postDict.put("applicant_first_name",applicant_first_name);
            postDict.put("applicant_last_name",applicant_last_name);
            postDict.put("applicant_gender",applicant_gender);
            postDict.put("applicant_pass_duration",applicant_pass_duration);
            postDict.put("applicant_pass_category",applicant_pass_category);
            postDict.put("applicant_pass_location_from",applicant_pass_location_from);
            postDict.put("applicant_pass_location_to",applicant_pass_location_to);
            Log.d("generateApplicationAPI","Failed To Put TUID and Password in Dictionary");
        }catch (JSONException e) {
            Log.d("generateApplicationAPI","Failed To Put in Dictionary");
        }
        if(postDict.length()>0)
        {
            execute(postDict.toString());
        }
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
        if(listner!=null)
        {
            listner.generatedApplication(o);
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
            Log.i("generateApplicationAPI","RESPONSE DATA - "+JSONResponse.toString());
            return JSONResponse;
        }catch (Exception e)
        {
            Log.d("generateApplicationAPI","[FAILED TO MAKE API CALL]");
            Log.d("generateApplicationAPI",e.toString());
        }
        return "failed";
    }
}
