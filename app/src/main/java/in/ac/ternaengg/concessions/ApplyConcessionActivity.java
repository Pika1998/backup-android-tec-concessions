package in.ac.ternaengg.concessions;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONObject;

public class ApplyConcessionActivity extends AppCompatActivity implements View.OnClickListener, genereateApplicationAPI.generateApplicationAPIListner {
EditText fromLocationInput,toLocationInput;
Button applyButton;
RadioGroup genderRadioGroup,durationRadioGroup,categoryRadioGroup;
SharedPreferences sharedPreferences;
String applicant_id,applicant_first_name,applicant_last_name;
String gender,pass_category,pass_duration,fromLocation,toLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_concession);
        fromLocationInput = findViewById(R.id.fromLocationInput);
        toLocationInput = findViewById(R.id.toLocationInput);
        genderRadioGroup = findViewById(R.id.genderRadioButtonGroup);
        durationRadioGroup = findViewById(R.id.passDurationRadioGroup);
        categoryRadioGroup = findViewById(R.id.passCategoryRadioGroup);
        applyButton = findViewById(R.id.applyConcessionButton);
        applyButton.setOnClickListener(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
         applicant_id = sharedPreferences.getString("tu_id","");
         applicant_first_name = sharedPreferences.getString("first_name","");
         applicant_last_name = sharedPreferences.getString("last_name","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.applyConcessionButton:{
                int error=0;
               fromLocation = fromLocationInput.getText().toString().trim();
                toLocation = toLocationInput.getText().toString().trim();
                int genderId = genderRadioGroup.getCheckedRadioButtonId();

                /// Gender Radio Button Detection
                switch (genderId){
                    case R.id.maleRadioButtonInput:
                    {
                        Log.d("ApplyConcessionActivity","[GENDER SELECTED] Male");
                        gender="MALE";
                        break;
                    }
                    case R.id.femaleRadioButtonInput:{
                        Log.d("ApplyConcessionActivity","[GENDER SELECTED] Female");
                        gender="FEMALE";
                        break;
                    }
                    default:{
                        error=1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("Please Select Gender");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                /////
                int passDurationId = durationRadioGroup.getCheckedRadioButtonId();
                // Pass Duration Radio Group Logic
                switch (passDurationId)
                {
                    case R.id.monthlyRadioButton:{
                        Log.d("ApplyConcessionActivity","[PASS DURATION SELECTED] Monthly");
                        pass_duration="MONTHLY";
                        break;
                    }
                    case R.id.quaterlyRadioButton:{
                        Log.d("ApplyConcessionActivity","[PASS DURATION SELECTED] Quaterly");
                        pass_duration="QUATERLY";
                        break;
                    }
                    default:{
                        error=1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("Please Select Pass Duration");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                ////
                int passCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
                switch (passCategoryId)
                {
                    case R.id.firstClassRadioButton:{
                        Log.d("ApplyConcessionActivity","[PASS CATEGORY SELECTED] First Class");
                        pass_category="FIRST CLASS";
                        break;
                    }
                    case R.id.secondClassRadioButton:{
                        Log.d("ApplyConcessionActivity","[PASS CATEGORY SELECTED] Second Class");
                        pass_category="SECOND CLASS";
                        break;
                    }
                    default:{
                        error=1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("Please Select Pass Category");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
                if(toLocation.equals("") || toLocation.equals(null))
                {
                    error=1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please Enter Destination Location");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if(fromLocation.equals("") || fromLocation.equals(null))
                {
                    error=1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please Enter Source Location");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                if(error==0)
                {
                    genereateApplicationAPI callApi = new genereateApplicationAPI();
                    callApi.setListner(this);
                    callApi.execute(applicant_id,applicant_first_name,applicant_last_name,gender,pass_duration,pass_category,fromLocation,toLocation);
                }
            }

        }
    }

    @Override
    public void generatedApplication(String o) {
        try{
            Log.d("ApplyConcessionActivity","Called generatedApplication Function");
            JSONObject response = new JSONObject(o);
            if(response.getBoolean("is_successful"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                builder.setTitle("Applied");
                builder.setMessage("Your application has been submitted successfully");
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent i = new Intent(ApplyConcessionActivity.this,StudentHomeActivity.class);
                        startActivity(i);
                    }
                });
            }
            else
            {
                if(response.getString("status").equals("pending"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("You already have a pending application");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyConcessionActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Failed to apply for concession.");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        }catch (Exception e)
        {
            Log.e("ApplyConcessionActivity",e.toString());
        }
    }
}
