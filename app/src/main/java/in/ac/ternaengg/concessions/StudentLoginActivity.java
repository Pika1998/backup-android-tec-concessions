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

public class StudentLoginActivity extends AppCompatActivity implements View.OnClickListener, StudentLoginAPI.StudentLoginAPIListner {
Button loginButton;
EditText tu_id_input, password_input;
SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        tu_id_input = findViewById(R.id.studentIdInput);
        password_input = findViewById(R.id.studentPasswordInput);
        loginButton = findViewById(R.id.studentLoginButton);
        loginButton.setOnClickListener(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("tec-concession-preferences",0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.studentLoginButton:{
                String tu_id = tu_id_input.getText().toString().trim();
                String password = password_input.getText().toString().trim();
                StudentLoginAPI callApi = new StudentLoginAPI();
                callApi.setListner(this);
                callApi.execute(tu_id,password);

            }
        }
    }

    @Override
    public void gotStudent(String o) {

            try {
                JSONObject response = new JSONObject(o);
                Log.d("StudentLoginActivity","Student Found");
                if(response.getBoolean("is_successful")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    JSONObject student = new JSONObject(response.getString("student"));
                    editor.putString("first_name", student.getString("first_name"));
                    editor.putString("last_name", student.getString("last_name"));
                    editor.putString("tu_id",student.getString("tu_id"));
                    editor.commit();
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentLoginActivity.this);
                    builder.setTitle("Logged In.");
                    Log.d("StudentLoginActivity",""+student);
                    builder.setMessage("" + student.getString("first_name") + " " + student.getString("last_name"));
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent i = new Intent(getApplicationContext(),StudentHomeActivity.class);
                            startActivity(i);
                        }
                    });
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentLoginActivity.this);
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
