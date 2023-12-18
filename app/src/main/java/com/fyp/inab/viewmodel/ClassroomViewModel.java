package com.fyp.inab.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.fyp.inab.object.Classroom;
import com.fyp.inab.object.User;
import com.fyp.inab.repository.ClassroomRepo;

import java.util.ArrayList;
import java.util.Map;

public class ClassroomViewModel extends AndroidViewModel {

    private ClassroomRepo repository;

    public ClassroomViewModel(@NonNull Application application) {
        super(application);

        repository = new ClassroomRepo(application);
    }

    /*
    Student
     */
    public MutableLiveData<Integer> inputClassroomCode (String code) {
        return repository.checkExistenceClassCodeInClassroomPath(code);
    }

    public MutableLiveData<User> getTeacherData (String id) {
        return repository.getTeacherProfile(id);
    }

    public MutableLiveData<Integer> totalCountClassroom () {
        return repository.getCountClassroomJoined();
    }

    public void leaveClassroom (String classroomId, String teacherId) {
        repository.leaveClassroom(classroomId, teacherId);
    }


    /*
    Teacher
     */
    public void createClassroom(Classroom classroom) {
        repository.createClassroom(classroom);
    }

    public void generateClassroomCode (Map<String, Object> classroomData) {
        repository.generateClassroomCode(classroomData);
    }

    public MutableLiveData<Map<String, Object>> classCodeIsExists (String classroomId) {
        return repository.checkExistenceClassCode(classroomId);
    }

    public void resetClassroomCode(String id, String code) {
        repository.resetClassroomCode(id, code);
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getStudentListClassroom (String id) {
        return repository.getStudentListOfClassroom(id);
    }

    public void deleteClassroom (String classroomId){
        repository.deleteClassroom(classroomId);
    }
}
