package in.ac.ternaengg.concessions;

import android.content.SharedPreferences;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StudentApplicationsActivity extends AppCompatActivity implements GetUserApplicationsAPI.GetUserapplicationsAPIListener {
RecyclerView userApplicationsRecyclerView;
GetUserApplicationsAPI callApi;
SharedPreferences sharedPreferences;
JSONArray applications;
userApplicationsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_applications);
        userApplicationsRecyclerView = findViewById(R.id.userApplicationsRecyclerView);
        callApi = new GetUserApplicationsAPI();
        sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
        String applicant_id = sharedPreferences.getString("tu_id","");
        callApi.setListener(this);
        callApi.start(applicant_id);
        adapter = new userApplicationsAdapter();
    }

    @Override
    public void gotApplications(String o) {
        try {
            JSONObject response = new JSONObject(o);
            Log.d("StudentApplicationsACtivity",""+response.toString());
            Log.d("StaffHomeActivity","Applications Found");
            if(response.getBoolean("is_successful")) {
                applications = response.getJSONArray("applications");
                Log.d("StudentApplicationsActivity","Got Applications :");
                Log.d("StudentApplicationsActivity",applications.toString());

                userApplicationsRecyclerView.setLayoutManager(new LinearLayoutManager(StudentApplicationsActivity.this));
                userApplicationsRecyclerView.setAdapter(adapter);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentApplicationsActivity.this);
                builder.setTitle("Error");
                builder.setMessage("Failed to fetch applications");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class userApplicationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        class Application extends RecyclerView.ViewHolder{
            TextView applicantNameTextView,applicantFromLocationTextView,applicantToLocationTextView,applicantClassTextView,applicantGenderTextView,applicationDateTextView;
            Button applicationAcceptButton,applicationRejectButton;
            TextView applicantPassDurationTextView;
            ImageView applicationStatusImageView;
            TextView applicationStatusTextView;
            public Application(@NonNull View itemView) {
                super(itemView);
                applicantNameTextView = itemView.findViewById(R.id.applicantNameTextView);
                applicantFromLocationTextView = itemView.findViewById(R.id.applicantFromLocationTextView);
                applicantToLocationTextView = itemView.findViewById(R.id.applicantToLocationTextView);
                applicantClassTextView = itemView.findViewById(R.id.applicantClassHolder);
                applicantGenderTextView = itemView.findViewById(R.id.applicantGenderTextView);
                applicantPassDurationTextView = itemView.findViewById(R.id.applicantPassDurationTextView);
                applicationDateTextView = itemView.findViewById(R.id.applicationDateHolder);
                applicationStatusImageView = itemView.findViewById(R.id.applicationStatusImageView);
                applicationStatusTextView = itemView.findViewById(R.id.applicationStatusTextview);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_application_view,viewGroup,false);
            return new Application(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            try {
                final JSONObject application =  applications.getJSONObject(i);
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
                String status = application.getString("application_ready");
                ((Application) viewHolder).applicationStatusTextView.setText(status);
                if(status.equals("pending"))
                {
                    ((Application) viewHolder).applicationStatusImageView.setImageResource(R.mipmap.pending);
                }
                if(status.equals("accepted")) {
                    ((Application) viewHolder).applicationStatusImageView.setImageResource(R.mipmap.accepted);
                }
                if(status.equals("rejected"))
                {
                    ((Application) viewHolder).applicationStatusImageView.setImageResource(R.mipmap.rejected);

                }
                Log.d("StaffHomeActivity","DATE IS "+applicationDate);
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





