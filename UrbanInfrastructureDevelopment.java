import java.io.Serializable;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class UrbanInfrastructureDevelopment implements Serializable {
    static final long serialVersionUID = 88L;

    /**
     * Given a list of Project objects, prints the schedule of each of them.
     * Uses getEarliestSchedule() and printSchedule() methods of the current project to print its schedule.
     * @param projectList a list of Project objects
     */
    public void printSchedule(List<Project> projectList) {
        for (Project project: projectList){
            project.printSchedule(project.getEarliestSchedule());
            project.getProjectDuration();
        }
        // TODO: YOUR CODE HERE
    }

    /**
     * TODO: Parse the input XML file and return a list of Project objects
     *
     * @param filename the input XML file
     * @return a list of Project objects
     */
    public List<Project> readXML(String filename) {
        List<Project> projectList = new ArrayList<>();
        try {

            //Same as last assignment
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList projectNodeList = doc.getElementsByTagName("Project");

            //Iterate over objects with project headers
            for (int i = 0; i < projectNodeList.getLength(); i++) {
                Node projectNode = projectNodeList.item(i);
                if (projectNode.getNodeType() == Node.ELEMENT_NODE) {
                    //For each project we've found the Name.
                    Element projectElement = (Element) projectNode;
                    String projectName = projectElement.getElementsByTagName("Name").item(0).getTextContent();
                    List<Task> tasks = new ArrayList<>();

                    //Now we need to find the tasks by iterating over objects with header types "Task"
                    NodeList taskList = projectElement.getElementsByTagName("Task");
                    for (int j = 0; j < taskList.getLength(); j++) {
                        Node taskNode = taskList.item(j);
                        if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element taskElement = (Element) taskNode;

                            //Find ID, Desription, Duration
                            int taskID = Integer.parseInt(taskElement.getElementsByTagName("TaskID").item(0).getTextContent());
                            String description = taskElement.getElementsByTagName("Description").item(0).getTextContent();
                            int duration = Integer.parseInt(taskElement.getElementsByTagName("Duration").item(0).getTextContent());
                            List<Integer> dependencies = new ArrayList<>();
                            NodeList dependsOn = taskElement.getElementsByTagName("DependsOnTaskID");

                            //Iterate over dependencies and add those:
                            for (int k = 0; k < dependsOn.getLength(); k++) {
                                dependencies.add(Integer.parseInt(dependsOn.item(k).getTextContent()));
                            }

                            //Finally create this task with all the info we've collected
                            Task task = new Task(taskID, description, duration, dependencies);
                            tasks.add(task);
                        }
                    }

                    //Then with all the tasks we've collected, create a project.
                    Project project = new Project(projectName, tasks);
                    projectList.add(project);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO: YOUR CODE HERE
        return projectList;
    }
}
