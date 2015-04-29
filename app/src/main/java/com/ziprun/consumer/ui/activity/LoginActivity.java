package com.ziprun.consumer.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.AuthOTP;
import com.ziprun.consumer.data.model.ZipConsumer;
import com.ziprun.consumer.network.ZipRestApi;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class LoginActivity extends ZipBaseActivity {
    private static final String TAG = LoginActivity.class.getCanonicalName();

    private static final int MOBILE_VERIFICATION_STATE = 1;
    private static final int VERIFICATION_CODE_STATE = 2;
    private static final String KEY_CURRENT_STATE = "current_state";
    private static final int MOBILE_NUM_LENGTH = 10;
    private static final int VERIFICATION_CODE_LENGTH = 6;


    @InjectView(R.id.login_container)
    RelativeLayout loginContainer;

    @InjectView(R.id.txt_verification_msg)
    TextView verificationMsg;

    @InjectView(R.id.txt_verification_helptext)
    TextView verificationHelpText;

    @InjectView(R.id.txt_countrycode)
    TextView countryCodeText;

    @InjectView(R.id.mobile_number)
    EditText editMobileNumber;

    @InjectView(R.id.verification_code)
    EditText editVerificationCode;

    @InjectView(R.id.nextBtn)
    Button nextBtn;

    @Inject
    ZipRestApi zipRestApi;

    Observable<Response> mobileVerificationObs;

    Observable<ZipConsumer> otpVerificationObs;

    Subscription verificationSub;

    ProgressDialog progressDialog;

    int currentState;

    String mobileNumber;

    String verificationCode;

    boolean isVerifying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        if(savedInstanceState != null){
            currentState = savedInstanceState.getInt(KEY_CURRENT_STATE);
        }else{
            currentState = MOBILE_VERIFICATION_STATE;
        }

        if(currentState == MOBILE_VERIFICATION_STATE)
            setupMobileVerificationState();
        else
            setupVerificationCodeState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isVerifying){
            if(currentState == MOBILE_VERIFICATION_STATE &&
                    mobileNumber != null)
                sendVerificationRequest();
            else if(verificationCode != null)
                verifyCode();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isVerifying && verificationSub != null) {
            verificationSub.unsubscribe();
            progressDialog.dismiss();
        }
    }

    public void setupMobileVerificationState(){
        currentState = MOBILE_VERIFICATION_STATE;
        countryCodeText.setVisibility(View.VISIBLE);
        editMobileNumber.setVisibility(View.VISIBLE);
        editVerificationCode.setVisibility(View.GONE);
        nextBtn.setText(R.string.btn_send_verification_code);
        verificationMsg.setText(R.string.msg_mobile_verification);
        verificationHelpText.setText(R.string.helptext_mobile_verification);

        addHorizontalLine(R.id.mobile_number);

        mobileNumber = editMobileNumber.getText().toString();

        nextBtn.setVisibility(mobileNumber.length() == 10 ? View.VISIBLE
                : View.GONE);

        editMobileNumber.requestFocus();

        editMobileNumber.addTextChangedListener(reqLengthValidator);
    }



    public void setupVerificationCodeState(){
        currentState = VERIFICATION_CODE_STATE;
        countryCodeText.setVisibility(View.GONE);
        editMobileNumber.setVisibility(View.GONE);
        editVerificationCode.setVisibility(View.VISIBLE);
        nextBtn.setText(R.string.btn_verify_code);
        addHorizontalLine(R.id.verification_code);

        verificationMsg.setText(R.string.msg_otp_verification);
        verificationHelpText.setText(R.string.helptext_otp_verification);

        verificationCode = editVerificationCode.getText().toString();

        nextBtn.setVisibility(verificationCode.length() == 6 ? View.VISIBLE
                : View.GONE);

        editVerificationCode.requestFocus();
        editVerificationCode.addTextChangedListener(reqLengthValidator);
    }



    public void validate(int length, int reqLength){
        nextBtn.setVisibility(length == reqLength? View.VISIBLE
                : View.GONE);
    }

    public void addHorizontalLine(int anchor){
        LayoutInflater li = LayoutInflater.from(this);
        View line = li.inflate(R.layout.view_horizontal_line, loginContainer,
                false);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, 2);

        lp.addRule(RelativeLayout.ALIGN_BOTTOM, anchor);

        loginContainer.addView(line, lp);
    }

    @OnClick(R.id.nextBtn)
    public void onNextBtnClicked(View view){
        if(currentState == MOBILE_VERIFICATION_STATE){
            sendVerificationRequest();
        }else{
            verifyCode();
        }

    }

    private void verifyCode() {
        isVerifying = true;
        showProgressDialog();

        if(otpVerificationObs == null){
            otpVerificationObs =
                    zipRestApi.verifyOTP(new AuthOTP(mobileNumber, verificationCode)).observeOn
                            (AndroidSchedulers.mainThread()).cache();
        }

        verificationSub = otpVerificationObs.subscribe(new Action1<ZipConsumer>() {
            @Override
            public void call(ZipConsumer zipConsumer) {
                isVerifying = false;
                progressDialog.dismiss();
                otpVerificationObs = null;
                Log.i(TAG, "Consumer: " + zipConsumer.toJson());
                zipRunSession.setConsumer(zipConsumer);
                startDeliveryActivity();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error while verifying otp", throwable);
                isVerifying = false;
                progressDialog.dismiss();
                otpVerificationObs = null;
                Toast.makeText(LoginActivity.this, "Invalid OTP/ OTP has " +
                        "expired", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void sendVerificationRequest() {
        isVerifying = true;
        showProgressDialog();

        if(mobileVerificationObs == null){
            mobileVerificationObs =
                    zipRestApi.verifyMobileNumber(
                            new AuthOTP(mobileNumber, null)
                    ).observeOn(AndroidSchedulers.mainThread()).cache();

        }

        verificationSub = mobileVerificationObs.subscribe(new Action1<Response>() {
            @Override
            public void call(Response response) {
                isVerifying = false;
                mobileVerificationObs = null;
                progressDialog.dismiss();
                currentState = VERIFICATION_CODE_STATE;
                setupVerificationCodeState();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                isVerifying = false;
                progressDialog.dismiss();
                mobileVerificationObs = null;
                Log.e(TAG, "Unable to send mobile verification request");
                Toast.makeText(LoginActivity.this, "Unable to verify mobile " +
                        "number at this time. Please try after sometime",
                        Toast.LENGTH_LONG).show();
            }
        });
   }

    private void showProgressDialog() {
        int title;
        int msg;
        if(currentState == MOBILE_VERIFICATION_STATE){
            title = R.string.title_dialog_wait;
            msg = R.string.msg_dialog_send_verification_request;
        }else{
            title = R.string.title_dialog_wait;
            msg = R.string.msg_dialog_verifying_otp;
        }
        progressDialog = ProgressDialog.show(this,
                getString(title),
                getString(msg), true);

    }

    private void startDeliveryActivity() {
        startActivity(new Intent(this, DeliveryActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(currentState == VERIFICATION_CODE_STATE)
            setupMobileVerificationState();
        else
            super.onBackPressed();
    }

    private TextWatcher reqLengthValidator = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int reqLength;
            if(currentState == MOBILE_VERIFICATION_STATE){
                mobileNumber = s.toString();
                reqLength = MOBILE_NUM_LENGTH;
            }else{
                verificationCode = s.toString();
                reqLength = VERIFICATION_CODE_LENGTH;
            }

            validate(s.length(), reqLength);
        }
    };
}
