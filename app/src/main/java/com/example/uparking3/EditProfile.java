package com.example.uparking3;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends MainMenu implements View.OnClickListener {
    private static final String USER_PROFILE_URL = "https://u-parking.000webhostapp.com/UParkingApp/UserProfile.php";
    private static final String CHANGE_PASSWORD_URL = "https://u-parking.000webhostapp.com/UParkingApp/ChangePassword.php";
    private static final String CHANGE_PHONE_URL = "https://u-parking.000webhostapp.com/UParkingApp/ChangePhone.php";
    private static final String UPDATE_PROFILE_URL = "https://u-parking.000webhostapp.com/UParkingApp/UpdateProfile.php";
    private static final String CONFIRM_URL = "https://u-parking.000webhostapp.com/UParkingApp/confirm.php";



    private RadioGroup genderRadioGroup;
    private Spinner citySpinner;
    private EditText  oldPasswordEditText, newPasswordEditText, confirmPasswordEditText, phoneEditText, confirmOtpEditText;
    private Button editProfileButton, changePasswordButton, changePhoneButton, buttonConfirm;
    private TextView cancelButton, usernameTextView, link_updatePhone, link_updatePassword;
    private String username, gender,city, newphone;
    private String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_profile);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_edit_profile, null, false);
        Drawer.addView(contentView, 0);


        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        link_updatePhone = (TextView) findViewById(R.id.link_updatePhone);
        link_updatePassword = (TextView) findViewById(R.id.link_updatePassword);
        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        editProfileButton = (Button) findViewById(R.id.editProfileButton);

        citySpinner=(Spinner) findViewById(R.id.citySpinner);
        items = new String[]{"مكة", "جدة"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        citySpinner.setAdapter(adapter);

        username = getIntent().getStringExtra("user_ID").trim();
        retrieveUser(username);
        usernameTextView.setText(username);
        link_updatePhone.setOnClickListener(this);
        link_updatePassword.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == editProfileButton) {
            updateProfile();
            Intent in = new Intent(EditProfile.this, EditProfile.class);
            in.putExtra("user_ID", username);
            startActivity(in);
        } else if (v == link_updatePassword) {
            updatePassword();
        } else if (v == link_updatePhone) {
            updatePhone();
        }
    }


    public void retrieveUser(final String username) {
        final ProgressDialog loading = ProgressDialog.show(this, "", "الرجاء الانتظار .......", false, false);

        RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("info");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        gender = jsonObject1.getString("gender");
                        if (gender.equalsIgnoreCase("male")) {
                            genderRadioGroup.check(R.id.male_radio_btn);
                        } else if (gender.equalsIgnoreCase("female")) {
                            genderRadioGroup.check(R.id.female_radio_btn);
                        } else genderRadioGroup.clearCheck();
                        genderRadioGroup.setEnabled(false);
                        city = jsonObject1.getString("city");
                        int index = -1;
                        for (int j =0 ;j<items.length;j++) {
                            if (items[j].equals(city)) {
                                index = j;
                                break;
                            }
                        }
                        citySpinner.setSelection(index);
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_ID", username);
                return params;
            }
        };

        requestQueue.add(stringRequest);


    }//Done

    public void updateProfile() {

        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        final String gender;
        if (selectedId == R.id.female_radio_btn)
            gender = "Female";
        else
            gender = "Male";
        final String city= String.valueOf(citySpinner.getSelectedItem());//citySpinner.getSelectedItem().toString().trim();
        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "يتم تحديث البيانات", "الرجاء الانتظار", false, false);
        // request create queue
        RequestQueue Queue = Volley.newRequestQueue(getApplicationContext());
        // response using URL
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(EditProfile.this, "تم تحديث البيانات بنجاح", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loading.dismiss();
                if (volleyError.getClass().equals(TimeoutError.class)) {
                    // Show timeout error message
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "نأسف ، تأكد من الشبكة", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_ID", username);
                params.put("gender", gender);
                params.put("address", city);
                return params;
            }
        };
        // adding request to queue
        Queue.add(request);
    }//Done

    @SuppressLint("WrongViewCast")
    public void updatePassword() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View changePasswordDialog = li.inflate(R.layout.dialog_change_password, null);
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        changePasswordButton = (AppCompatButton) changePasswordDialog.findViewById(R.id.changePasswordButton);
        cancelButton = (AppCompatTextView) changePasswordDialog.findViewById(R.id.cancelButton);
        oldPasswordEditText = (AppCompatEditText) changePasswordDialog.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = (AppCompatEditText) changePasswordDialog.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (AppCompatEditText) changePasswordDialog.findViewById(R.id.confirmPasswordEditText);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //Adding our dialog box to the view of alert dialog
        alert.setView(changePasswordDialog);
        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //Displaying the alert dialog
        alertDialog.show();
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String newPW = newPasswordEditText.getText().toString().trim();
                final String oldPW = oldPasswordEditText.getText().toString().trim();
                if (isPasswordValid(newPW)) {
                    final ProgressDialog loading = ProgressDialog.show(EditProfile.this, "يتم تحديث البيانات", "الرجاء الانتظار", false, false);
                    // request create queue
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    // response using URL
                    StringRequest request = new StringRequest(Request.Method.POST, CHANGE_PASSWORD_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("success")) {
                                loading.dismiss();
                                alertDialog.dismiss();
                                Toast.makeText(EditProfile.this, "تم تحديث كلمة المرور يجب عليك تسجيل الدخول", Toast.LENGTH_SHORT).show();
                                SessionManager sessionManager = new SessionManager(EditProfile.this);
                                sessionManager.logout();
                                //startActivity(new Intent(EditProfile.this, EditProfile.class));
                            } else {
                                loading.dismiss();
                                oldPasswordEditText.setError("كلمة المرور القديمة غير صحيحة");
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
                            params.put("user_ID", username);
                            params.put("new_password", newPW);
                            params.put("old_password", oldPW);

                            return params;
                        }
                    };
                    // adding request to queue
                    requestQueue.add(request);
                } //else {oldPasswordEditText.setError("Enter the right password");}
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                // startActivity(new Intent (EditProfile.this , EditProfile.class));
            }
        });


    }//Done

    @SuppressLint("WrongViewCast")
    public void updatePhone() {
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
                newphone = phoneEditText.getText().toString().trim();
                if (validPhone(phoneEditText.getText().toString().trim())) {
                    alertDialog.dismiss();
                    final ProgressDialog loading = ProgressDialog.show(EditProfile.this, "يتم تحديث رقم الجوال", "الرجاء الانتظار", false, false);
                    // request create queue
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    // response using URL
                    StringRequest request = new StringRequest(Request.Method.POST, CHANGE_PHONE_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            try {
                                if (response.equals("success")) {
                                    confirmOtp();
                                } else {
                                    Toast.makeText(EditProfile.this, response, Toast.LENGTH_SHORT).show();
                                    updatePhone();
                                }
                            } catch (JSONException e) {
                                loading.dismiss();
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
                            params.put("user_ID", username);
                            params.put("phone_number", newphone);
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
                final ProgressDialog loading = ProgressDialog.show(EditProfile.this, "يتم التحقق من الكود المدخل", "الرجاء الانتظار ...", false,false);
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
                                Toast.makeText(EditProfile.this, "تم تحديث رقم الجوال بنجاح", Toast.LENGTH_LONG).show();
                            }
                            else{
                                loading.dismiss();
                                //Displaying a toast if the otp entered is wrong
                                Toast.makeText(EditProfile.this,"رمز التحقق خاطئ" ,Toast.LENGTH_LONG).show();
                                //Asking user to enter otp again
                                confirmOtp();
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



    public boolean isPasswordValid(String newPassword) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        //String  newPassword=newPasswordEditText.getText().toString().trim();
        matcher = pattern.matcher(newPassword);

        if (!((confirmPasswordEditText.getText().toString().trim()).equals(newPassword))) {
            confirmPasswordEditText.setError("كلمة المرور المدخلة غير متطابقة");
            return false;
        } else if (!matcher.matches()) {
            newPasswordEditText.setError("كلمة مرور ضعيفة");
            return false;
        }
        return true;
    }

}