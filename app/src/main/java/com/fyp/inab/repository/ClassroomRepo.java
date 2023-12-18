package com.fyp.inab.repository;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Classroom;
import com.fyp.inab.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassroomRepo {

    private Application application;

    // firebase
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference teacherClassroomRef, TeacherPathClassroomRef, studentClassroomRef, classroomStudentListRef, userRef;

    // mutable live data
    private MutableLiveData<Map<String, Object>> classroomCodeExistence;
    private MutableLiveData<Integer> classroomCodeIsCorrect;
    private MutableLiveData<ArrayList<Map<String, Object>>> classroomStudentList;
    private MutableLiveData<User> teacherData;
    private MutableLiveData<Integer> totalCountClassroom;

    private ArrayList<Map<String, Object>> studentList;
    private String path;
    private User user;

    public ClassroomRepo(Application application) {
        this.application = application;

        // init mutable live data
        classroomCodeExistence = new MutableLiveData<>();
        classroomCodeIsCorrect = new MutableLiveData<>();
        classroomStudentList = new MutableLiveData<>();
        teacherData = new MutableLiveData<>();
        totalCountClassroom = new MutableLiveData<>();

        // init arraylist
        studentList = new ArrayList<>();

        // init firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userRef = firestore.collection("users");
        teacherClassroomRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("classroom");
        TeacherPathClassroomRef = firestore.collection("pathClassroom");
        studentClassroomRef = firestore.collection("users")
                .document(auth.getUid())
                .collection("classroom");
    }

    public void createClassroom(Classroom classroom) {
        teacherClassroomRef.document()
                .set(classroom)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: classroom created");
                        }
                    }
                });
    }

    public void generateClassroomCode(Map<String, Object> classroomData) {

        TeacherPathClassroomRef.document()
                .set(classroomData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: class code inserted");
                    }
                });
    }

    public MutableLiveData<Map<String, Object>> checkExistenceClassCode(String classroomId) {

        TeacherPathClassroomRef
                .whereEqualTo("path", "users/" + auth.getUid() + "/classroom/" + classroomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Map<String, Object> classroomPath = new HashMap<>();
                        classroomPath.put("isExist", false);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                classroomPath.put("isExist", true);
                                classroomPath.put("code", documentSnapshot.getString("code"));
                                break;
                            }
                        } else {
                            classroomPath.put("isExist", false);
                        }
                        classroomCodeExistence.postValue(classroomPath);
                    }
                });
        return classroomCodeExistence;
    }

    public void resetClassroomCode(String classroomId, String classCode) {
        TeacherPathClassroomRef
                .whereEqualTo("path", "users/" + auth.getUid() + "/classroom/" + classroomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Map<String, Object> code = new HashMap<>();
                                code.put("code", classCode);
                                TeacherPathClassroomRef
                                        .document(documentSnapshot.getId())
                                        .set(code, SetOptions.merge());
                            }
                        }
                    }
                });
    }

    public MutableLiveData<Integer> checkExistenceClassCodeInClassroomPath(String code) {

        Log.d(TAG, "checkExistenceClassCodeInClassroomPath: entered");
        classroomCodeIsCorrect = new MutableLiveData<>();

        TeacherPathClassroomRef.whereEqualTo("code", code)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // exist
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    path = documentSnapshot.getString("path");
                                    break;
                                }
                                /*
                                before add student in classroom,
                                check whether the class has same student
                                 */
                                firestore.collection(path + "/student_list")
                                        .whereEqualTo("student_id", auth.getUid())
                                        .limit(1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    // exist already in classroom
                                                    if (task.getResult().size() > 0) {
                                                        classroomCodeIsCorrect.postValue(0);
                                                    }
                                                    // not exist
                                                    else {
                                                        Map<String, Object> student = new HashMap<>();
                                                        student.put("student_id", auth.getUid());

                                                        // add student in classroom
                                                        firestore.collection(path + "/student_list")
                                                                .document()
                                                                .set(student)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "onComplete: insert the student list");
                                                                        } else {
                                                                            Log.d(TAG, "onComplete: failed insert in classroom");
                                                                        }
                                                                    }
                                                                });

                                                        // add classroom info in student info
                                                        firestore.document(path)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Classroom classroom = task.getResult().toObject(Classroom.class);
                                                                            String classroomId = path.split("/")[3];

                                                                            studentClassroomRef.document(classroomId)
                                                                                    .set(classroom)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            Log.d(TAG, "onComplete: class has inserted");
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                        classroomCodeIsCorrect.postValue(1);
                                                    }
                                                }
                                            }
                                        });
                            }
                            // not exist
                            else {
                                classroomCodeIsCorrect.postValue(2);
                            }
                        } else {
                            Log.d(TAG, "onComplete: failed?");
                        }
                    }
                });
        return classroomCodeIsCorrect;
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getStudentListOfClassroom (String classId) {

        // init arraylist
//        if (!studentList.isEmpty()) {
//            studentList.clear();
//        }

        teacherClassroomRef.document(classId)
                .collection("student_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            studentList = new ArrayList<>();

                            if (task.getResult().size() == 0) {
                                classroomStudentList.postValue(studentList);
                            } else {
                                for (QueryDocumentSnapshot docs : task.getResult()) {
                                    String studentId = docs.getString("student_id");

                                    // retrieve student data from user collection
                                    userRef.document(studentId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    user = task.getResult().toObject(User.class);
                                                    String id = task.getResult().getId();

                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("user", user);
                                                    data.put("id", id);

                                                    studentList.add(data);

                                                    // postValue method will override the value.
                                                    classroomStudentList.postValue(studentList);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
        return classroomStudentList;
    }

    public MutableLiveData<User> getTeacherProfile(String teacherId) {
        firestore.collection("users")
                .document(teacherId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            teacherData.postValue(task.getResult().toObject(User.class));
                        }
                    }
                });
        return teacherData;
    }

    public MutableLiveData<Integer> getCountClassroomJoined() {

        studentClassroomRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            totalCountClassroom.postValue(task.getResult().size());
                        }
                    }
                });

        return totalCountClassroom;
    }

    public void leaveClassroom (String classroomId, String teacherId) {

        CollectionReference studentList = userRef
                                .document(teacherId)
                                .collection("classroom")
                                .document(classroomId)
                                .collection("student_list");

        // delete student from student list in classroom
        studentList
                .whereEqualTo("student_id", auth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                studentList
                                        .document(docs.getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: leave the student list");
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    }
                });

        // delete the classroom
        studentClassroomRef
                .document(classroomId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: delete classroom student");
                        }
                    }
                });
    }

    public void deleteClassroom(String classroomId) {

        String path = "users/"+auth.getUid()+"/classroom/"+classroomId;

        // delete path classroom
        TeacherPathClassroomRef
                .whereEqualTo("path", path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                TeacherPathClassroomRef
                                        .document(docs.getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: delete path classroom");
                                                }
                                            }
                                        });
                                break;
                            }
                        }
                    }
                });

        /*
        go through each student in student list
        then delete the classroom
         */
        teacherClassroomRef
                .document(classroomId)
                .collection("student_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot docs : task.getResult()) {
                                String studentId = docs.getString("student_id");
                                userRef
                                        .document(studentId)
                                        .collection("classroom")
                                        .document(classroomId)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "onComplete: classroom for " + studentId + "deleted");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        // delete the classroom
        teacherClassroomRef.document(classroomId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: delete classroom");
                        }
                    }
                });
    }
}
