package com.fyp.inab.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Nilam;
import com.fyp.inab.object.NilamLog;
import com.fyp.inab.repository.NilamRepo;

import java.util.ArrayList;
import java.util.Map;

public class NilamViewModel extends AndroidViewModel {

    private NilamRepo repository;

    public NilamViewModel(@NonNull Application application) {
        super(application);

        repository = new NilamRepo(application);
    }

    public void createNilam(Nilam nilam, String teacherId, String classId) {
        repository.createNilamProgress(nilam, teacherId, classId);
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getListNotification () {
        return repository.getNotificationApproval();
    }

    public MutableLiveData<Nilam> getNilamData (String path) {
        return repository.getNilamDetails(path);
    }

    public void updateNilamApproval (String path, String response, String id, String comment) {
        repository.updateApprovalNilam(path, response, id, comment);
    }

    public MutableLiveData<Integer> getNilamProgress(String uid) {
        return repository.countNilamProgress(uid);
    }

    public void createLogNilam (String path, String classId, String sender, String response, String comment) {
        repository.insertNilamUpdateLog(path, classId, sender, response, comment);
    }

    public MutableLiveData<ArrayList<NilamLog>> getLogList () {
        return repository.getLogNilam();
    }
}
