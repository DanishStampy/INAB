package com.fyp.inab.repository;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Nilam;
import com.fyp.inab.object.NilamLog;
import com.fyp.inab.object.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NilamRepo {

    private Application application;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CollectionReference nilamRef, nilamApprovalRef, bookRef, logRef, classRef;

    private MutableLiveData<ArrayList<Map<String, Object>>> listApprovalNotification;
    private MutableLiveData<ArrayList<NilamLog>> listLogApprovalNilam;
    private MutableLiveData<Nilam> nilamDataObj;
    private MutableLiveData<Integer> nilamProgress;

    private String id;

    private ArrayList<Map<String, Object>> arrayListApprovalNotification;

    public NilamRepo(Application application) {
        this.application = application;

        // init mutable live data
        listApprovalNotification = new MutableLiveData<>();
        nilamDataObj = new MutableLiveData<>();
        nilamProgress = new MutableLiveData<>();

        // init firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        nilamRef = firestore.collection("users")
            .document(auth.getUid())
            .collection("nilam");
        nilamApprovalRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("approval_nilam");
        bookRef = firestore.collection("books");
        logRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("teacher_log");
        classRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("classroom");
    }

    public void createNilamProgress(Nilam nilam, String teacherId, String classId) {

        String nilamUid = System.currentTimeMillis() + "." + nilam.getTitle();
        String path = "users/"+auth.getUid()+"/nilam/"+nilamUid;

        // insert nilam inside student
        nilamRef.document(nilamUid)
                .set(nilam)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: nilam updated progress");
                        }
                    }
                });

        // send approval request to teacher
        Map<String, Object> approvalRequest = new HashMap<>();
        approvalRequest.put("sender", user.getDisplayName());
        approvalRequest.put("class_id", classId);
        approvalRequest.put("path", path);
        approvalRequest.put("title", nilam.getTitle());
        approvalRequest.put("author", nilam.getAuthor());

        firestore.collection("users")
                .document(teacherId)
                .collection("approval_nilam")
                .document(System.currentTimeMillis() + "")
                .set(approvalRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: sent approval to teacher");
                        }
                    }
                });

    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getNotificationApproval () {

        nilamApprovalRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayListApprovalNotification = new ArrayList<>();

                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                Map<String, Object> approval = new HashMap<>();

                                String notificationDate = Util.convertIdToDate(docs.getId());
                                approval.put("id", docs.getId());
                                approval.put("sender", docs.getString("sender"));
                                approval.put("notification_date", notificationDate);
                                approval.put("title", docs.getString("title"));
                                approval.put("author", docs.getString("author"));
                                approval.put("class_id", docs.getString("class_id"));
                                approval.put("path", docs.getString("path"));

                                arrayListApprovalNotification.add(approval);
                            }
                            listApprovalNotification.postValue(arrayListApprovalNotification);
                        }
                    }
                    @SuppressLint("SimpleDateFormat")
                    private String getDate(String id) {
                        long timestamp = Long.parseLong(id);
                        Date date = new Date(timestamp);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        return format.format(date);
                    }
                });

        return listApprovalNotification;
    }

    public MutableLiveData<Nilam> getNilamDetails (String pathNilam) {

        firestore.document(pathNilam)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            nilamDataObj.postValue(task.getResult().toObject(Nilam.class));
                        }
                    }
                });
        return nilamDataObj;
    }

    public void updateApprovalNilam (String pathNilam, String response, String notificationId, String comment) {

        String dateTime = Util.getDateCurrent();

        Map<String, Object> updateNilam = new HashMap<>();
        updateNilam.put("approval", response);
        updateNilam.put("comment", comment);
        updateNilam.put("date_approved", dateTime);

        // update approval nilam
        firestore.document(pathNilam)
                .update(updateNilam)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: updated nilam approval");
                        }
                    }
                });

        // delete the notification approval
        nilamApprovalRef.document(notificationId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: delete approval nilam");
                        }
                    }
                });
    }

    public void insertNilamUpdateLog (String path, String class_id, String sender, String response, String comment) {

        String[] arrPath = path.split("/");
        String nilamId = arrPath[arrPath.length-1];

        // get the class name first
        classRef.document(class_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String classroomName = task.getResult().getString("classTitle");

                            Map<String, Object> logData = new HashMap<>();
                            logData.put("nilam_id", nilamId);
                            logData.put("classroom_name", classroomName);
                            logData.put("student_name", sender);
                            logData.put("status_nilam", response);
                            logData.put("comment", comment);

                            // then insert in log
                            logRef.document(String.valueOf(System.currentTimeMillis()))
                                    .set(logData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: log inserted");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public MutableLiveData<Integer> countNilamProgress(String studentId) {

        firestore.collection("users")
                .document(studentId)
                .collection("nilam")
                .whereEqualTo("approval", "approved")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            nilamProgress.postValue(task.getResult().size());
                        }
                    }
                });

        return nilamProgress;
    }

    public MutableLiveData<ArrayList<NilamLog>> getLogNilam () {
        listLogApprovalNilam = new MutableLiveData<>();
        logRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList dataList = new ArrayList();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot docs : task.getResult()) {
                               NilamLog log = docs.toObject(NilamLog.class);
                               String dateMillis = Util.convertIdToDate(docs.getId());

                               log.setDate(dateMillis);
                               dataList.add(log);
                            }
                            listLogApprovalNilam.postValue(dataList);
                        } else {
                            listLogApprovalNilam.postValue(dataList);
                        }
                    }
                });
        return listLogApprovalNilam;
    }
}
