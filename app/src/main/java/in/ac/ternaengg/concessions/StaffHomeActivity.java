package in.ac.ternaengg.concessions;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.LocaleData;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class StaffHomeActivity extends AppCompatActivity implements View.OnClickListener, GetApplicationsAPI.GetApplicationsAPIListner, AcceptApplicationAPI.AcceptApplicantAPIListener, RejectApplicationAPI.RejectApplicationAPIListener {
TextView staffUsernameTextView;
Button staffLogoutButton;
RecyclerView applicationRecyclerView;
SharedPreferences sharedPreferences;
JSONArray applications;
AcceptApplicationAPI acceptApplication ;
RejectApplicationAPI rejectapplication;
applicationRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);
        staffLogoutButton = findViewById(R.id.staffLogoutButton);
        applicationRecyclerView = findViewById(R.id.applicationsRecyclerView);
        sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
        String username = sharedPreferences.getString("staff_username","");
        staffUsernameTextView = findViewById(R.id.staffUserIdHolder);
        staffUsernameTextView.setText(username);
        staffLogoutButton.setOnClickListener(this);
        acceptApplication = new AcceptApplicationAPI();
        rejectapplication = new RejectApplicationAPI();
        rejectapplication.setListener(this);
        acceptApplication.setListener(this);
        GetApplicationsAPI callApi = new GetApplicationsAPI();
        callApi.setListner(this);
        adapter = new applicationRecyclerViewAdapter();
        callApi.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.staffLogoutButton:{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(StaffHomeActivity.this,MainActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void gotApplications(String o) {
        try {
            JSONObject response = new JSONObject(o);
            Log.d("StaffHomeActivity","Applications Found");
            if(response.getBoolean("is_successful")) {
                applications = response.getJSONArray("applications");
                Log.d("StaffHomeActivity","Got Applications :");
                Log.d("StaffHomeActivity",applications.toString());

                applicationRecyclerView.setLayoutManager(new LinearLayoutManager(StaffHomeActivity.this));
                applicationRecyclerView.setAdapter(adapter);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Failed to fetch applications");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gotResponse(String o,int pos) {
        try {
            JSONObject response = new JSONObject(o);
            Log.d("StaffHomeActivity","Got Response");
            if(response.getBoolean("is_successful")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
                builder.setTitle("Accepted.");
                builder.setMessage("Application Accepted Successfully");
                AlertDialog dialog = builder.create();
                dialog.show();
                applications.remove(pos);
                applicationRecyclerView.removeViewAt(pos);
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(pos,applications.length());
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
                builder.setTitle("Failed");
                builder.setMessage("Failed To Accept Application");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void gotRejectApplicationResponse(String o, int pos) {
        try {
            JSONObject response = new JSONObject(o);
            Log.d("StaffHomeActivity","Got Response");
            if(response.getBoolean("is_successful")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
                builder.setTitle("Rejected.");
                builder.setMessage("Application Rejected Successfully");
                AlertDialog dialog = builder.create();
                dialog.show();
                applications.remove(pos);
                applicationRecyclerView.removeViewAt(pos);
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(pos,applications.length());
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffHomeActivity.this);
                builder.setTitle("Failed");
                builder.setMessage("Failed To Reject Application");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Recycler View Adapter
    class applicationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        class Application extends RecyclerView.ViewHolder{
            TextView applicantNameTextView,applicantFromLocationTextView,applicantToLocationTextView,applicantClassTextView,applicantGenderTextView,applicationDateTextView;
            Button applicationAcceptButton,applicationRejectButton;
            TextView applicantPassDurationTextView;
            public Application(@NonNull View itemView) {
                super(itemView);
                applicantNameTextView = itemView.findViewById(R.id.applicantNameTextView);
                applicantFromLocationTextView = itemView.findViewById(R.id.applicantFromLocationTextView);
                applicantToLocationTextView = itemView.findViewById(R.id.applicantToLocationTextView);
                applicantClassTextView = itemView.findViewById(R.id.applicantClassHolder);
                applicantGenderTextView = itemView.findViewById(R.id.applicantGenderTextView);
                applicantPassDurationTextView = itemView.findViewById(R.id.applicantPassDurationTextView);
                applicationDateTextView = itemView.findViewById(R.id.applicationDateHolder);
                applicationAcceptButton = itemView.findViewById(R.id.acceptApplicationButton);
                applicationRejectButton = itemView.findViewById(R.id.rejectApplicationButton);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.application_view,viewGroup,false);
            return new Application(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            try {
                final JSONObject application =  applications.getJSONObject(i);
                Log.d("StaffHomeActivity","Applicant No"+i);
                Log.d("StaffHomeActivity",application.toString());
                ((Application) viewHolder).applicantNameTextView.setText(""+application.getString("applicant_first_name")+" "+application.getString("applicant_last_name"));
                ((Application) viewHolder).applicantGenderTextView.setText(application.getString("applicant_gender"));
                ((Application) viewHolder).applicantToLocationTextView.setText(application.getString("applicant_pass_location_to"));
                ((Application) viewHolder).applicantFromLocationTextView.setText(application.getString("applicant_pass_location_from"));
                ((Application) viewHolder).applicantClassTextView.setText(application.getString("applicant_pass_category"));
                ((Application) viewHolder).applicantPassDurationTextView.setText(application.getString("applicant_pass_duration"));
                final String applicant_id = application.getString("applicant_id");
                String applicationDate = (String) application.get("applied_on");
                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                Date date = df2.parse(applicationDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                ((Application) viewHolder).applicationDateTextView.setText(""+cal.get(Calendar.DATE)+" - "+(cal.get(Calendar.MONTH)+1)+" - "+cal.get(Calendar.YEAR));
                Log.d("StaffHomeActivity","DATE IS "+applicationDate);

                ((Application) viewHolder).applicationAcceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            acceptApplication.start(applicant_id,i);
                    }
                });

                ((Application) viewHolder).applicationRejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectapplication.start(applicant_id,i);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return applications.length();
        }
    }
}



