package fi.patrikmarin.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import fi.patrikmarin.google.GoogleService;

public class TasksService {
	
	public ArrayList<GTaskList> tasklists = new ArrayList<GTaskList>();
	public ArrayList<GTaskEvent> tasks = new ArrayList<GTaskEvent>();
	
    private Tasks service;
	
	public TasksService() throws IOException {
		service = getTasksService();
		getTaskLists();
		getTasks();
	}
	
    /**
     * Build and return an authorized Tasks client service.
     * @return an authorized Tasks client service
     * @throws IOException
     */
    private Tasks getTasksService() throws IOException {
        Credential credential = GoogleService.authorize();
        return new Tasks.Builder(
                GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    private void getTaskLists() throws IOException {
    	tasklists = new ArrayList<GTaskList>();

        TaskLists result = service.tasklists().list()
             .setMaxResults(Long.valueOf(10))
             .execute();
        
        List<TaskList> gtl = result.getItems();
        
        if (gtl == null || gtl.size() == 0) {
        	System.out.println("No task lists found.");
        } else {
            for (TaskList tasklist : gtl) {
            	
            	GTaskList tl = new GTaskList();
            	
            	tl.setID(tasklist.getId());
            	tl.setLink(tasklist.getSelfLink());
            	tl.setTitle(tasklist.getTitle());
            	
            	tasklists.add(tl);
            }
        }
    }
    
    private void getTasks() throws IOException {
    	
    	for (GTaskList tl : tasklists) {
    		com.google.api.services.tasks.model.Tasks gtasks = service.tasks().list(tl.getID()).execute();

        	for (Task task : gtasks.getItems()) {
        	  GTaskEvent gtask = new GTaskEvent();
        	  
        	  gtask.setParent(tl);
        	  gtask.setID(task.getId());
        	  gtask.setNotes((task.getNotes() == null) ? "" : task.getNotes());
        	  gtask.setTitle(task.getTitle());
        	  gtask.setDue((task.getDue() == null) ? null : task.getDue());
        	  gtask.setCompleted((task.getCompleted() == null) ? false : true);
        	  tasks.add(gtask);
        	}
        	
    	}
    }
}
