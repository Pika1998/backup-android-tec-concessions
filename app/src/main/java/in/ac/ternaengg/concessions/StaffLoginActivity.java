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

import org.json.JSONException;
import org.json.JSONObject;

public class StaffLoginActivity extends AppCompatActivity implements View.OnClickListener, StaffLoginAPI.StaffLoginAPIListner {
EditText usernameInput,passwordInput;
Button staffLoginButton;
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        staffLoginButton = findViewById(R.id.staffLoginButton);
        usernameInput = findViewById(R.id.staffIdInput);
        passwordInput = findViewById(R.id.staffPasswordInput);
        sharedPreferences = getSharedPreferences("tec-concession-preferences",0);
        staffLoginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.staffLoginButton:{
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                StaffLoginAPI callApi = new StaffLoginAPI();
                callApi.setListner(this);
                callApi.execute(username,password);
            }
        }
    }

    @Override
    public void gotStaff(String o) {
        try {
            JSONObject response = new JSONObject(o);
            Log.d("StaffLoginActivity","staff Found");
            if(response.getBoolean("is_successful")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                JSONObject staff = new JSONObject(response.getString("staff"));
                editor.putString("staff_username", staff.getString("username"));
                editor.remove("first_name");
                editor.remove("last_name");
                editor.remove("tu_id");
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffLoginActivity.this);
                builder.setTitle("Logged In.");
                Log.d("StaffLoginActivity",""+staff);
                builder.setMessage(staff.getString("username"));
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent i = new Intent(getApplicationContext(),StaffHomeActivity.class);
                        startActivity(i);
                    }
                });
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffLoginActivity.this);
                builder.setTitle("Login Failed");
                builder.setMessage("Invalid Credentials");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
