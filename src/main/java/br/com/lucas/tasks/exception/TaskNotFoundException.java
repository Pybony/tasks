package br.com.lucas.tasks.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException() {
        super("Task not found");
    }

}
