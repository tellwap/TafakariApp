package com.blogspot.waptell.tafakari;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class UserAuthenticationActivity extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "UserAuthenticationActiv";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    private static String phonenumber="";

    //widget
    private EditText code, mPhonenumber;
    private TextView verfInfo, codeVerfInfo, phoneVerfTime, phoneCodeResend,wrongNumber;

    //groupView/layout
    private LinearLayout startVerfLayout;
    private LinearLayout codeVerfLayout;
    private LinearLayout parentLayout;

    //country code picker
    private CountryCodePicker ccp;

    //progress dialog
    private ProgressDialog mProgressDialog;

    //sharedpref
    private  SharedPreferenceConfig sharedPreferenceConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sharedpref
        sharedPreferenceConfig = new SharedPreferenceConfig(UserAuthenticationActivity.this);

        //check if user alread Authenticated
        if (!sharedPreferenceConfig.verfiedUserPhonenumber().equals("")){
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }

        setContentView(R.layout.activity_user_authentication);





        //country code picker
        ccp = findViewById(R.id.ccp);

        //widget
        mPhonenumber = findViewById(R.id.phonenumber);
        verfInfo = findViewById(R.id.verfInfo);
        codeVerfInfo = findViewById(R.id.phone_verif_code_info);
        phoneVerfTime = findViewById(R.id.phone_verif_time);
        phoneCodeResend = findViewById(R.id.phone_code_resend);
        wrongNumber = findViewById(R.id.wrong_number);

        //join cpp and mPhonenumber to get full number


        //progressDialog
        mProgressDialog = new ProgressDialog(this);

        //layout
        startVerfLayout = findViewById(R.id.startVerfLayout);
        codeVerfLayout = findViewById(R.id.code_verf_layout);
        parentLayout = findViewById(R.id.parent_layout);

        //mAuth
        mAuth = FirebaseAuth.getInstance();

        code = findViewById(R.id.code);

        callback_verificvation();

        phoneCodeResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sharedPreferenceConfig.readPhoneNumber().equals("")){
                    startPhoneNumberVerification("+"+sharedPreferenceConfig.readPhoneNumber());
                    startVerfLayout.setVisibility(View.GONE);
                }
            }
        });

        if (!sharedPreferenceConfig.readPhoneNumber().equals("")){

            startVerfLayout.setVisibility(View.GONE);
            codeVerfLayout.setVisibility(View.VISIBLE);
            codeVerfInfo.setText("Tumetuma ujumbe mfupi kwa " + sharedPreferenceConfig.readPhoneNumber() + ". Jaza kodi maalumu kuingia kwenye akaunti yako.");
            phoneCodeResend.setVisibility(View.VISIBLE);
            phoneVerfTime.setVisibility(View.GONE);


        }

        wrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear shared preference
                sharedPreferenceConfig.addVerfInf("","");
                codeVerfLayout.setVisibility(View.GONE);
                startVerfLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        //show progressDialog
        mProgressDialog.setMessage("Tafadhali subiri..");
        mProgressDialog.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        mProgressDialog.setMessage("Tafadhali subiri...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        Log.e(TAG, "verifyPhoneNumberWithCode: " + credential.getSmsCode());
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void callback_verificvation() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e(TAG, "onVerificationCompleted:" + credential);
                mProgressDialog.dismiss();

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e(TAG, "onVerificationFailed", e);
                verfInfo.setTextColor(getResources().getColor(R.color.colorDanger));

                //dismiss progresDialog
                mProgressDialog.dismiss();

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    verfInfo.setText(R.string.phone_verif_error_message);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    verfInfo.setText(R.string.phone_verf_exceed_quota);

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                mProgressDialog.dismiss();
                Log.e(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                //save VerfId and phonenumber to the sharedPref

                sharedPreferenceConfig.addVerfInf(phonenumber,mVerificationId);

                //hide verficationform and show code verfication
                startVerfLayout.setVisibility(View.GONE);
                codeVerfLayout.setVisibility(View.VISIBLE);
                codeVerfInfo.setText("Tumetuma ujumbe mfupi kwa " + sharedPreferenceConfig.readPhoneNumber() + ". Jaza kodi maalumu kuingia kwenye akaunti yako.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 60; i >= 0; --i) {
                            try {
                                Thread.sleep(1000);

                                final int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        phoneVerfTime.setText("SUBIRI " + finalI + " SEC KUTUMA TENA.");
                                        if (finalI == 0) {
                                            //show resend view and hide code verf time
                                            phoneVerfTime.setVisibility(View.GONE);
                                            phoneCodeResend.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //  Log.e(TAG, "run: "+i );
                        }


                    }
                }).start();


            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Log.e(TAG, "onComplete: " + user.getPhoneNumber());
                            sharedPreferenceConfig.insertVerfiedUserPhonenumber(user.getPhoneNumber());
                            startActivity(new Intent(UserAuthenticationActivity.this, ProfileActivity.class));
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Snackbar.make(parentLayout,"Umeingiza kodi ambazo si sahihi au tayari zimeshatumika tafadhali tuma tena.",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void startVerification(View view) {

        if (sharedPreferenceConfig.readPhoneNumber().equals("")) {
            phonenumber = ccp.getSelectedCountryCode().concat(mPhonenumber.getText().toString().trim());
            startPhoneNumberVerification("+" + phonenumber);
        }else{
           // startPhoneNumberVerification("+" + sharedPreferenceConfig.readPhoneNumber());
            Log.e(TAG, "startVerification: "+sharedPreferenceConfig.readPhoneNumber());
        }

    }

    public void VerfyNumber(View view) {
        if (!TextUtils.isEmpty(code.getText().toString())) {

            verifyPhoneNumberWithCode(sharedPreferenceConfig.readTokenId(), code.getText().toString());
        }else{
            Toast.makeText(this, "Tafadhali jaza code zilizotumwa.", Toast.LENGTH_LONG).show();
        }

    }
}
