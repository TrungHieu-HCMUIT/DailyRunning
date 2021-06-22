package com.example.dailyrunning.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.record.RecordFragment;
import com.example.dailyrunning.record.RecordViewModel;
import com.example.dailyrunning.utils.ConfirmDialog;
import com.example.dailyrunning.utils.RunningLoadingDialog;

/*
* Q7UH R5LF EJ6M NRXE YMG4 Y3Y3 ILF6 QDAN
*   100053689872926
Nxhung2
 * */

public class LoginActivity extends AppCompatActivity implements LoginViewModel.LoadingDialog , RecordViewModel.ShowConfirmDialog {

    private LoginViewModel mLoginViewModel;
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/dailyrunning-6e8e9.appspot.com/o/avatar_photos%2Fsbcf-default-avatar%5B1%5D.png?alt=media&token=ec7c1fcd-9fc8-415f-b2ec-51ffb03867a3";
    private RunningLoadingDialog loadingDialog;
    private ConfirmDialog confirmDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginViewModel=new ViewModelProvider(this).get(LoginViewModel.class);
        loadingDialog=new RunningLoadingDialog();
        mLoginViewModel.setLoadingDialog(this);
        mLoginViewModel.confirmDialog=this;
        confirmDialog=new ConfirmDialog();
        //endregion
    }

    public Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.login_fragment_container);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }
    @Override
    public void onBackPressed() {
        Fragment currentFrag=getForegroundFragment();
        if(!(currentFrag instanceof RegisterAddInfoFragment)) {
            super.onBackPressed();
        }
        else
        {
            confirmDialog.show(getSupportFragmentManager()
                    ,"Thoát ứng dụng?",
                    "Bạn có muốn thoát ứng dụng",v -> {},
                    V->{
                        setResult(RESULT_CANCELED);
                        finish();
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //FB login
        mLoginViewModel.mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void showDialog() {
        loadingDialog.show(getSupportFragmentManager(),"tag");
    }

    @Override
    public void dismissDialog() {
        loadingDialog.dismiss();

    }

    @Override
    public void show(String title, String description, View.OnClickListener onCancel, View.OnClickListener onConfirm) {
        confirmDialog.show(getSupportFragmentManager(),title,description,onCancel,onConfirm);
    }
}
