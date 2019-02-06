package de.timecrunch.timecrunch.model;

public class TimeBlockTaskModel {
    private TaskModel task;
    private boolean isFinished;

    public TimeBlockTaskModel(){
    }

    public TimeBlockTaskModel(TaskModel task, boolean isFinished){
        this.task = task;
        this.isFinished = isFinished;
    }


    public TaskModel getTask() {
        return task;
    }

    public boolean getIsFinished() {
        return isFinished;
    }


    public void setIsFinished(boolean finished) {
        isFinished = finished;
    }
}
