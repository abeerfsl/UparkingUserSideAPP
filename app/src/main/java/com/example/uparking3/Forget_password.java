package com.example.uparking3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Forget_password extends AppCompatActivity implements View.OnClickListener {

    String username ;
    EditText password , c_password;
    Button change ;
    String FORGET_URL = "https://u-parking.000webhostapp.com/UParkingApp/new_password.php";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);

        password = (EditText) findViewById(R.id.password);
        c_password = (EditText) findViewById(R.id.c_password);
        change = (Button) findViewById(R.id.change);
        change.setOnClickListener(this);
        Bundle  b = getIntent().getExtras();

        username = b.getString("username");

    }


    public void Update_password(){

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        if (isPasswordValid(password.getText().toString())) {
            final ProgressDialog loading = ProgressDialog.show(Forget_password.this, "يتم تحديث البيانات", "الرجاء الانتظار", false, false);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, FORGET_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();
                    if(response.equals("success")){
                    Intent in = new Intent(Forget_password.this, LoginActivity.class);
                    Toast.makeText(Forget_password.this, "تم تحديث كلمة المرور بنجاح", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(in);
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    loading.dismiss();
                    Toast.makeText(Forget_password.this, error.toString(), Toast.LENGTH_LONG).show();

                }

            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password.getText().toString().trim());

                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }

    }

        public boolean isPasswordValid(String newPassword) {
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";
            Pattern pattern;
            Matcher matcher;
            pattern = Pattern.compile(PASSWORD_PATTERN);
            //String  newPassword=newPasswordEditText.getText().toString().trim();
            matcher = pattern.matcher(newPassword);

            if (!((c_password.getText().toString().trim()).equals(newPassword))) {
                c_password.setError("كلمة المرور المدخلة غير متطابقة");
                return false;
            } else if (!matcher.matches()) {
                password.setError("كلمة مرور ضعيفة");
                return false;
            }
            return true;
        }

    @Override
    public void onClick(View v) {
        if (v== change){

            if (password.getText().toString().isEmpty() || c_password.getText().toString().isEmpty()){
                Toast.makeText(Forget_password.this, "الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();

            }else{
                Update_password();
            }
        }
    }
}
