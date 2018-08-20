package com.wf.wisdom_safety.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wf.util.RxSubscriptionCollection;
import com.wf.wisdom_safety.LoginActivity;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.user.User;
import com.wf.wisdom_safety.model.user.UserManager;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;

/**
 * Created by Lionel on 2017/9/15.
 */

public class UserFragment extends Fragment {

    @Inject
    UserManager mUserManager;
    @Inject
    Picasso mPicasso;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;

    @Bind(R.id.button)
    Button mButton;
    @Bind(R.id.name_tv)
    TextView mNameTv;
    @Bind(R.id.phone_tx)
    TextView mPhoneTx;

    public UserFragment() {
        DaggerUserComponent.create().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        User user = mUserManager.getUser();
        if(user != null && mNameTv != null && mPhoneTx != null) {
            mNameTv.setText(user.getName());
            mPhoneTx.setText(user.getUsername());
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mRxSubscriptionCollection.cancelAll();
    }

    @OnClick(R.id.button)
    public void onLogoutClick() {
        mRxSubscriptionCollection.add(mUserManager.logout().subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Context context = UserFragment.this.getActivity();
                Toast.makeText(context, "退出登录失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean) {
                    getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        }));

    }

    @OnClick(R.id.change_password_view)
    public void onChangePasswordClick() {
        startActivity(new Intent(this.getContext(), ChangePasswordActivity.class));
    }

    @OnClick(R.id.about_view)
    public void onAboutClick() {
        startActivity(new Intent(this.getContext(), AboutActivity.class));
    }

}
