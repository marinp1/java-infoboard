package fi.patrikmarin.infoboard.calendar;

import java.util.TreeMap;
import java.util.TreeSet;

public class MeisterTaskHelper {
    
    /**
     * Get a list of MeisterTask projects.
     * @return a map of projects with IDs
     */
    protected static TreeMap<String, String> getMeisterTaskProjects() {
    	TreeMap<String, String> meisterProjects = new TreeMap<String, String>();
    	
    	meisterProjects.put("12423", "School");
    	
    	return meisterProjects;
    }
    
    protected static TreeSet<MeisterTaskEvent> getMeisterTasksByProject(CommonEventContainer ce) {
    	TreeSet<MeisterTaskEvent> meisterTasks = new TreeSet<MeisterTaskEvent>();
    	
    	MeisterTaskEvent mEvent = new MeisterTaskEvent("7363937", ce, "CS-E4330 Projekti 1", "", "1483014938000.0");
 
    	meisterTasks.add(mEvent);
    	
    	return meisterTasks;
    }
}
