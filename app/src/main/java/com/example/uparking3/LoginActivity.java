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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username , password ;
    Button login;
    String URL_LOGIN= "https://u-parking.000webhostapp.com/UParkingApp/login.php" ;
    String FORGET_URL = "https://u-parking.000webhostapp.com/UParkingApp/forget_password.php";
    String CONFIRM_URL = "https://u-parking.000webhostapp.com/UParkingApp/confirm.php";

    //String CONFIRM_URL = "https://u-parking.000webhostapp.com/UParkingApp/confirm.php";
    TextView Create_Account , Forget_Password;
    EditText confirmOtpEditText;
    private EditText  phoneEditText;
    private Button changePhoneButton, buttonConfirm;
    private TextView cancelButton;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        Forget_Password = (TextView) findViewById(R.id.forget_password);
        Forget_Password.setOnClickListener(this);


        login.setOnClickListener(this);
        Create_Account = (TextView) findViewById(R.id.Create_Account);
        Create_Account.setOnClickListener(this);


    }


    public void login(final String Username, final String Password){
        final ProgressDialog loading = ProgressDialog.show(this, "تسجيل الدخول", "الرجاء الانتظار .......", false, false);

        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();

                if (response.equals("1")){
                    Toast.makeText(getApplicationContext(), "حسابك غير مفعل ", Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("2")){
                    username.setError("اسم المستخدم غير موجود");
                }

                else if (response.equals("3")){
                    password.setError("كلمة المرور غير صحيحة ");
                }

                else{

                        sessionManager.createSession(Username);
                        Intent intent = new Intent(LoginActivity.this, SearchPage.class);
                        intent.putExtra("username",Username);
                        Toast.makeText(getApplicationContext(), response+"  أهلاً ", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("username", Username);
                params.put("phone_number", Username);
                params.put("password", Password);

                return params;
            }};
        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public void onClick(View v) {

        if (v == Create_Account){
            Create_Account.setTextColor(getResources().getColor(android.R.color.black));
            startActivity(new Intent(LoginActivity.this, RegistersActivity.class));

        }

        if(v == Forget_Password){
            Forget_Password.setTextColor(getResources().getColor(android.R.color.black));
            Forget_pass();

        }

        if(v== login){
            final String password = this.password.getText().toString();
            final String username = this.username.getText().toString();
            if( !password.isEmpty() || !username.isEmpty() ) {
                login(username,password);

            } else {
                this.password.setError("ادخل كلمة المرور");
                this.username.setError("ادخل اسم المستخدم");
            }
        }

    }


    //This method would confirm the otp
    @SuppressLint("WrongViewCast")
    private void confirmOtp(final String username) throws JSONException {
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
                final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "يتم التحقق من الرمز المدخل", "الرجاء الانتظار ...", false,false);
                //Getting the user entered otp from edittext
                final String otp = confirmOtpEditText.getText().toString().trim();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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
                                Intent in = new Intent(LoginActivity.this ,Forget_password.class);
                                Bundle b = new Bundle();
                                // in case we want transfer more of information we will repeat just the sentence below
                                b.putString("username", username);
                                in.putExtras(b);
                                finish();
                                startActivity(in);

                            }
                            else{
                                loading.dismiss();
                                //Displaying a toast if the otp entered is wrong
                                Toast.makeText(LoginActivity.this,"رمز التحقق خاطئ" ,Toast.LENGTH_LONG).show();
                                //Asking user to enter otp again
                                confirmOtp(username);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                loading.dismiss();
                                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();

                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put("username", username);
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


    @SuppressLint("WrongViewCast")
    public void Forget_pass() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View changePhoneDialog = li.inflate(R.layout.dialog_change_phone, null);
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        changePhoneButton = (AppCompatButton) changePhoneDialog.findViewById(R.id.changePhoneButton);
        cancelButton = (AppCompatTextView) changePhoneDialog.findViewById(R.id.cancelButton);
        phoneEditText = (AppCompatEditText) changePhoneDialog.findViewById(R.id.phoneEditText);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //Adding our dialog box to the view of alert dialog
        alert.setView(changePhoneDialog);
        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //Displaying the alert dialog
        alertDialog.show();
        changePhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validPhone(phoneEditText.getText().toString().trim())) {
                    alertDialog.dismiss();
                    final ProgressDialog loading = ProgressDialog.show(LoginActivity.this, "سيتم ارسال رمز التحقق الى جوالك", "الرجاء الانتظار", false, false);
                    // request create queue
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    // response using URL
                    StringRequest request = new StringRequest(Request.Method.POST, FORGET_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            try {
                                if(response.equals("failure")){
                                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                                    Forget_pass();
                                }else{
                                    confirmOtp(response);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            alertDialog.dismiss();
                            loading.dismiss();
                            if (volleyError.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("phone_number", phoneEditText.getText().toString().trim());
                            return params;
                        }
                    };
                    // adding request to queue
                    requestQueue.add(request);
                } else {
                    phoneEditText.setError("ادخل رقم جوال صحيح");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //startActivity(new Intent (EditProfile.this , EditProfile.class));
            }
        });


    }


    protected boolean validPhone(String phone) {
        final String phonePattern = "\\+9665\\d{8}|05\\d{8}|\\+1\\(\\d{3}\\)\\d{3}-\\d{4}|\\+1\\d{10}|\\d{3}-\\d{3}-\\d{4}";
        if (!phone.matches(phonePattern)) {
            return false;
        }
        return true;

    }

}
