package com.wf.wisdom_safety;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.wf.util.RxSubscriptionCollection;
import com.wf.util.Validator;
import com.wf.wisdom_safety.bean.user.User;
import com.wf.wisdom_safety.model.user.UserManager;
import com.wf.wisdom_safety.ui.mainmenu.MainActivity;
import com.wf.wisdom_safety.ui.user.DaggerUserComponent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;

public class LoginActivity extends AppCompatActivity {

    @Inject
    UserManager mUserManager;
    //@Inject
    //PushUtil mPushUtil;

    RxSubscriptionCollection mRxSubscriptionCollection = new RxSubscriptionCollection();
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.user_name_edt)
    EditText mUserNameEdt;
    @Bind(R.id.password_edt)
    EditText mPasswordEdt;
    @Bind(R.id.login_btn)
    Button mLoginButton;
    @Bind(R.id.login_progress)
    ProgressBar mLoginProgress;
    @Bind(R.id.forgot_password_tv)
    TextView mForgotPasswordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        DaggerUserComponent.create().inject(this);
        setToolbar();
        // enable status bar tint
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(false);
        // Set up the login form.
        mUserNameEdt.setText(mUserManager.getDefaultName());
        mPasswordEdt.setText(mUserManager.getDefaultPassword());
        mPasswordEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        mToolbar.setTitle("");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mUserNameEdt.setError(null);
        mPasswordEdt.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserNameEdt.getText().toString();
        String password = mPasswordEdt.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!Validator.isPassword(password)) {
            mPasswordEdt.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEdt;
            cancel = true;
        }

        if(email == null || email.equals("")) {
            mUserNameEdt.setError("请输入登录账号");
            focusView = mUserNameEdt;
            cancel = true;
        }

        // Check for a valid email address.
       /* if (!Validator.isMobile(email)) {
            mUserNameEdt.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameEdt;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //User.sharedInstance().login(email, password);
            login(email, password);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginButton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            mForgotPasswordTv.setEnabled(!show);
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mForgotPasswordTv.setEnabled(!show);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
    }

    @OnClick({R.id.login_btn, R.id.forgot_password_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                attemptLogin();
                break;
            case R.id.forgot_password_tv:
               // startActivity(new Intent(this, ResetPasswordActivity.class));
                Toast.makeText(LoginActivity.this, "请联系单位管理员重置密码", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 登录
     */
    public void login(final String name, final String password) {
        Subscription subscription = mUserManager.login(name, password).subscribe(new Observer<User>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                String description = e.getMessage();
                Toast.makeText(LoginActivity.this, description, Toast.LENGTH_SHORT).show();
                showProgress(false);
            }

            @Override
            public void onNext(User user) {
                //mPushUtil.init();
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
//                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }

}

