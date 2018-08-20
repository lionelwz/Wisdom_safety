package com.wf.wisdom_safety.ui.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wf.util.RxSubscriptionCollection;
import com.wf.util.Validator;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.model.user.UserManager;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;

public class ChangePasswordActivity extends AppCompatActivity {

    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;
    @Inject
    UserManager mUserManager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.password_edit)
    EditText mPasswordEdit;
    @Bind(R.id.repeat_password_edit)
    EditText mRepeatPasswordEdit;
    @Bind(R.id.old_password_edit)
    EditText mOldPasswordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        DaggerUserComponent.create().inject(this);

        mToolbar.setTitle(getString(R.string.change_password));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxSubscriptionCollection.cancelAll();
    }

    @OnClick(R.id.confirm_btn)
    public void onConfirmClick() {
        String oldPassword = mOldPasswordEdit.getText().toString();
        if (oldPassword.isEmpty() || !Validator.isPassword(oldPassword)) {
            mOldPasswordEdit.setError(getText(R.string.password_hint));
            mOldPasswordEdit.requestFocus();
            return;
        }

        String password = mPasswordEdit.getText().toString();
        if (password.isEmpty() || !Validator.isPassword(password)) {
            mPasswordEdit.setError(getText(R.string.password_hint));
            mPasswordEdit.requestFocus();
            return;
        }

        String repeat = mRepeatPasswordEdit.getText().toString();
        if (!repeat.equals(password)) {
            mRepeatPasswordEdit.setError("密码两次输入不符！");
            mRepeatPasswordEdit.requestFocus();
            return;
        }

        Subscription subscription = mUserManager.changePassword(oldPassword, password, repeat).
        subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Boolean success) {
                if (success) {
                    Toast.makeText(ChangePasswordActivity.this, "密码修改成功！", Toast.LENGTH_SHORT).show();
                    ChangePasswordActivity.this.finish();
                } else
                    onError(new RuntimeException("密码修改失败！"));
            }
        });
        mRxSubscriptionCollection.add(subscription);
    }
}
