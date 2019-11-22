package in.ac.ternaengg.concessions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentHomeActivity extends AppCompatActivity implements View.OnClickListener {
TextView NameHolder,IdHolder;
Button logoutButton,goToApplyApplicationButton,viewApplicationsButton;
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        NameHolder = findViewById(R.id.studentHomeNameHolder);
        IdHolder = findViewById(R.id.studentHomeIdHolder);
         sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
        String Name = ""+sharedPreferences.getString("first_name","")+" "+sharedPreferences.getString("last_name","");
        NameHolder.setText(Name);
        String Id = sharedPreferences.getString("tu_id","");
        IdHolder.setText(Id);
        logoutButton = findViewById(R.id.logOutButton);
        goToApplyApplicationButton = findViewById(R.id.applyForConcessionButton);
        viewApplicationsButton = findViewById(R.id.viewApplicationsButton);

        logoutButton.setOnClickListener(this);
        goToApplyApplicationButton.setOnClickListener(this);
        viewApplicationsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.logOutButton:{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                break;
            }
            case R.id.applyForConcessionButton:{
                Intent i = new Intent(getApplicationContext(),ApplyConcessionActivity.class);
                startActivity(i);
                break;
            }
            case R.id.viewApplicationsButton:{
                Intent i = new Intent(getApplicationContext(),StudentApplicationsActivity.class);
                startActivity(i);
                break;
            }
        }
    }
}
