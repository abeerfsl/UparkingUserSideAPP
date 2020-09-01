package com.example.uparking3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistersActivity extends AppCompatActivity implements View.OnClickListener {


    EditText username , phone_number , password , c_password ,confirmOtpEditText;
    Button register , buttonConfirm;
    String URL_REGISTER= "https://u-parking.000webhostapp.com/UParkingApp/Register.php" ;
    String CONFIRM_URL = "https://u-parking.000webhostapp.com/UParkingApp/confirm.php";
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        phone_number = (EditText) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.password);
        c_password = (EditText) findViewById(R.id.c_password);

        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(this);


    }

    public void insert_user() {

        final String Username = username.getText().toString().trim();
        final String Phone_number = phone_number.getText().toString().trim();
        final String Password = password.getText().toString().trim();


        if (this.isValidData()) {
            //Displaying a progress dialog
            final ProgressDialog loading = ProgressDialog.show(this, "يتم انشاء الحساب", "الرجاء الانتظار .......", false, false);


            requestQueue = Volley.newRequestQueue(RegistersActivity.this);
            // response using URL
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loading.dismiss();
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        String jsonArray=jsonObject.getString("success");
                        if (jsonArray.equals("0")) {
                            Toast.makeText(getApplicationContext(), "اسم المستخدم او رقم الجوال موجود لدينا مسبقاً", Toast.LENGTH_LONG).show();
                        } else {
                            //Asking user to confirm otp
                            confirmOtp();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    loading.dismiss();
                    if (volleyError.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
                        Toast.makeText(getApplicationContext(), "نأسف لقد انتهى الوقت", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("username", Username);
                    params.put("password", Password);
                    params.put("phone_number", Phone_number);

                    return params;
                }
            };
            requestQueue.add(stringRequest);

        }

    }

    public boolean isValidData() {
        final String Username = this.username.getText().toString().trim();
        final String Phone_number = this.phone_number.getText().toString().trim();
        final String Password = this.password.getText().toString().trim();
        final String C_password = this.c_password.getText().toString().trim();

        String  phonePattern = "\\+9665\\d{8}|05\\d{8}|\\+1\\(\\d{3}\\)\\d{3}-\\d{4}|\\+1\\d{10}|\\d{3}-\\d{3}-\\d{4}";

        // Data Validation
        if (Username.isEmpty()|| Password.isEmpty() || C_password.isEmpty() || Phone_number.isEmpty()) {
            Toast.makeText(RegistersActivity.this, "الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if (!isValidPassword(Password)){
            password.setError("يجب ان تحتوي كلمة المرور على احرف كبيرة وصغيرة وارقام واحرف ");
            return false;
        }
        else if (!Phone_number.matches(phonePattern)){
            phone_number.setError("مثلا : 05xxxxxxxx");
            return false;

        }
        else if(!C_password.equals(Password)) {
            password.setError("كلمة المرور غير متطابقة");
            c_password.setError("كلمة المرور غير متطابقة");
            return false;
        } else {

            return true ;
        }
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }



    //This method would confirm the otp
    @SuppressLint("WrongViewCast")
    private void confirmOtp() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        confirmOtpEditText = (AppCompatEditText) confirmDialog.findViewById(R.id.editTextOtp);
        TextView cancelTextView= (AppCompatTextView) confirmDialog.findViewById(R.id.cancelTextView);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);
        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //Displaying the alert dialog
        alertDialog.show();
        //On the click of the confirm button from alert dialog
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding the alert dialog
                alertDialog.dismiss();
                //Displaying a progressbar
                final ProgressDialog loading = ProgressDialog.show(RegistersActivity.this, "يتم التاكد من رمز التحقق المدخل", "الرجاء الانتظار ....", false,false);
                //Getting the user entered otp from edittext
                final String otp = confirmOtpEditText.getText().toString().trim();
                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, CONFIRM_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //if the server response is success
                        try {
                            if((response.toString().trim()).equalsIgnoreCase("success")){
                                //dismissing the progressbar
                                loading.dismiss();
                                //Starting a new activity
                                startActivity(new Intent(RegistersActivity.this, SuccessActivity.class));
                                finish();
                            }
                            else{
                                loading.dismiss();
                                //Displaying a toast if the otp entered is wrong
                                Toast.makeText(RegistersActivity.this,"رمز التحقق خاطئ" ,Toast.LENGTH_LONG).show();
                                //Asking user to enter otp again
                                confirmOtp();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put("username", username.getText().toString().trim());
                        params.put("otp",otp);
                        return params;
                    }
                };

                //Adding the request to the queue
                requestQueue.add(stringRequest);
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {

        if (v == register){
            insert_user();
        }
    }


}
