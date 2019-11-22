package in.ac.ternaengg.concessions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
Button goToStudentLoginButton,goTostaffLoginbutton;
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
        String first_name = sharedPreferences.getString("first_name",null);
        String last_name = sharedPreferences.getString("last_name",null);
        String staff_username = sharedPreferences.getString("staff_username",null);
        if(first_name!=null&&last_name!=null)
        {
            Intent i = new Intent(getApplicationContext(),StudentHomeActivity.class);
            startActivity(i);
        }
        else if(staff_username!=null)
        {
            Intent i = new Intent(getApplicationContext(),StaffHomeActivity.class);
            startActivity(i);
        }
        else
        {
            setContentView(R.layout.activity_main);
            goToStudentLoginButton = findViewById(R.id.goToStudentLoginButton);
            goTostaffLoginbutton = findViewById(R.id.goToStaffLoginButton);
            goToStudentLoginButton.setOnClickListener(this);
            goTostaffLoginbutton.setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.goToStudentLoginButton:{
                Intent goToStudentLoginIntent = new Intent(getApplicationContext(),StudentLoginActivity.class);
                startActivity(goToStudentLoginIntent);
                break;
            }
            case R.id.goToStaffLoginButton:{
                Intent goToStaffLoginIntent = new Intent(getApplicationContext(),StaffLoginActivity.class);
                startActivity(goToStaffLoginIntent);
                break;
            }
        }
    }
}
