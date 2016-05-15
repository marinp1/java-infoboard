package fi.patrikmarin.google;

import java.io.IOException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import fi.patrikmarin.google.GoogleService;

public class TasksService {
    /**
     * Build and return an authorized Tasks client service.
     * @return an authorized Tasks client service
     * @throws IOException
     */
    public static Tasks getTasksService() throws IOException {
        Credential credential = GoogleService.authorize();
        return new Tasks.Builder(
                GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    public static void getTasks() throws IOException {
        Tasks service = getTasksService();

        // Print the first 10 task lists.
        TaskLists result = service.tasklists().list()
             .setMaxResults(Long.valueOf(10))
             .execute();
        List<TaskList> tasklists = result.getItems();
        if (tasklists == null || tasklists.size() == 0) {
            System.out.println("No task lists found.");
        } else {
            System.out.println("Task lists:");
            for (TaskList tasklist : tasklists) {
            	String tID = tasklist.getId();
            	
            	com.google.api.services.tasks.model.Tasks tasks = service.tasks().list(tID).execute();

            	for (Task task : tasks.getItems()) {
            	  System.out.println(task.getTitle());
            	}
            }
        }
    }
}
