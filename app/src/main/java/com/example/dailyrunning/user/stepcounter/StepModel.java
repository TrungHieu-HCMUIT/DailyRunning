package com.example.dailyrunning.user.stepcounter;

public class StepModel {
    private int id;
    private int task;

    public StepModel(int id, int task) {
        this.id = id;
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }
}
