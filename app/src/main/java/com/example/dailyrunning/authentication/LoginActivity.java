package com.example.dailyrunning.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.utils.LoginViewModel;

/*
* Q7UH R5LF EJ6M NRXE YMG4 Y3Y3 ILF6 QDAN
*   100053689872926
Nxhung2
 * */

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/dailyrunning-6e8e9.appspot.com/o/avatar_photos%2Fsbcf-default-avatar%5B1%5D.png?alt=media&token=ec7c1fcd-9fc8-415f-b2ec-51ffb03867a3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginViewModel=new ViewModelProvider(this).get(LoginViewModel.class);


        //endregion
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //FB login
        mLoginViewModel.mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //
        super.onActivityResult(requestCode, resultCode, data);
    }
    



}

/*
                                                    //region up avt len firebase
                                                    StorageReference avtRef = FirebaseStorage.getInstance().getReference().child("avatar_photos").child(fbUID);
                                                    avtRef.putFile(Uri.parse(avatarUrl)).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            avtRef.getDownloadUrl()
                                                                    .addOnSuccessListener(uri -> {
                                                                        Uri userAvatarUri = uri;
                                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(user.getUid());
                                                                        userRef.child("avatarURI").setValue(userAvatarUri.toString());
                                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                                                                        user.updateProfile(profileUpdates);
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            task1.getException().printStackTrace();
                                                        }
                                                    });

                                                    //endregion
*/
