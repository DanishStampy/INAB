package com.fyp.inab.viewmodel;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.User;
import com.fyp.inab.repository.ProfileRepo;

import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {

    private ProfileRepo repository;
    private MutableLiveData<Map<String, Object>> userData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        repository = new ProfileRepo(application);
    }

    public void updateUserProfile (Map<String, Object> userUpdate) {
        Log.d(TAG, "updateUserProfile: user = " + userUpdate.get("email"));
        repository.updateProfile(userUpdate);
    }

    public MutableLiveData<User> getUserProfile () {
        return repository.getProfile();
    }
}
