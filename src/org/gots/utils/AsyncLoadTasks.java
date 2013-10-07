package org.gots.utils;

import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import org.gots.ui.LoginActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsyncLoadTasks extends AsyncTask<Void, Void, List<String>> {

    private final LoginActivity loginActivity;

    public com.google.api.services.tasks.Tasks service;

    public AsyncLoadTasks(LoginActivity loginActivity1) {
        this.loginActivity = loginActivity1;
        service = loginActivity1.service;
    }


    @Override
    protected List<String> doInBackground(Void... arg0) {
        try {
            List<String> result = new ArrayList<String>();
            com.google.api.services.tasks.Tasks.TasksOperations.List listRequest = service.tasks().list(
                    "@default");
            listRequest.setFields("items/title");
            List<Task> tasks = listRequest.execute().getItems();
            if (tasks != null) {
                for (Task task : tasks) {
                    result.add(task.getTitle());
                }
            } else {
                result.add("No tasks.");
            }
            return result;
        } catch (IOException e) {
            loginActivity.handleGoogleException(e);
            return Collections.singletonList(e.getMessage());
        } finally {
            loginActivity.onRequestCompleted();
        }
    }
}
