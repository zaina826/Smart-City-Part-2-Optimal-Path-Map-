import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HyperloopTrainNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageTrainSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;;
    public int numTrainLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<TrainLine> lines;

    /**
     * Method with a Regular Expression to extract integer numbers from the fileContent
     * @return the result as int
     */
    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract string constants from the fileContent
     * @return the result as String
     */
    public String getStringVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("\\s*" + varName + "\\s*=\\s*(?:\"([^\"]*)\"|([^\\s\"]+))");
        Matcher m = p.matcher(fileContent);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the fileContent
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */

    public Double getDoubleVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("\\s*" + varName + "\\s*=\\s*([-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Double.parseDouble(m.group(1));

        // TODO: Your code goes here
    }


    /**
     * Write the necessary Regular Expression to extract a Point object from the fileContent
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * @return the result as a Point object
     */
    public Point getPointVar(String varName, String fileContent) {
        Point p = new Point(0, 0);
        Pattern pattern = Pattern.compile("\\b" + varName + "\\s*=\\s*\\(\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\)");
        Matcher m = pattern.matcher(fileContent);

        m.find();
        p.x = Integer.parseInt(m.group(1));
        p.y = Integer.parseInt(m.group(2));
        return p;
        // TODO: Your code goes here

    } 

    /**
     * Function to extract the train lines from the fileContent by reading train line names and their 
     * respective stations.
     * @return List of TrainLine instances
     */
    public List<TrainLine> getTrainLines(String fileContent) {
        List<TrainLine> trainLines = new ArrayList<>();
        Pattern linePattern = Pattern.compile(
                "train_line_name\\s*=\\s*\"?(.*?)\"?\\s*+train_line_stations\\s*=\\s*(\\([^)]+\\)(?:\\s*\\([^)]+\\))*)",
                Pattern.DOTALL
        );
        Matcher lineMatcher = linePattern.matcher(fileContent);


        while (lineMatcher.find()) {
            String trainLineNameBlock = lineMatcher.group(0);
            String trainLineName = getStringVar("train_line_name", trainLineNameBlock);
            String stationsData = lineMatcher.group(2);

            List<Station> stations = extractStations(stationsData, trainLineName);
            trainLines.add(new TrainLine(trainLineName, stations));
        }
        return trainLines;
    }



    private static List<Station> extractStations(String stationsData, String lineName) {
    List<Station> stations = new ArrayList<>();

    Pattern stationPattern = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    Matcher stationMatcher = stationPattern.matcher(stationsData);
    int lineNum = 1;

    while (stationMatcher.find()) {
        int x = Integer.parseInt(stationMatcher.group(1));
        int y = Integer.parseInt(stationMatcher.group(2));
        stations.add(new Station(new Point(x, y), lineName + " Line Station " + lineNum));
        lineNum++;
    }

    return stations;
}


    /**
     * Function to populate the given instance variables of this class by calling the functions above.
     */
    public void readInput(String filename) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(filename)));

            this.averageTrainSpeed = getDoubleVar("average_train_speed", fileContent)/0.06;
            this.numTrainLines = getIntVar("num_train_lines", fileContent);
            this.lines = getTrainLines(fileContent);
            this.startPoint = new Station(getPointVar("starting_point", fileContent), "Starting Point");
            this.destinationPoint = new Station(getPointVar("destination_point", fileContent), "Final Destination");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Your code goes here

    }

}