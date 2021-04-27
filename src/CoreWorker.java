import Models.XRaySample;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CoreWorker {

    //static String pythonCommand = "python3 /Users/Marco/IdeaProjects/XRayData/src/Python/PlotData.py";
    static final String pythonVersion = "python3";
    //static String scriptPath = "/Users/Marco/IdeaProjects/XRayData/src/Python/PlotData.py";

    public static void main(String[] args) throws IOException, InterruptedException {
        String scriptPath = System.getProperty("user.dir");
        scriptPath += "/Python/PlotData.py";
        //scriptPath += "/src/Python/PlotData.py";

        FileInput files = new FileInput(); //Read and process data from files

        HashMap<File, LinkedList> map;
        if (args.length == 0) {
            map = files.getSampleMap();
        } else {
            map = processArgs(args, files);
        }

        ArrayList<LinkedList> allFiles = new ArrayList<>();
        LinkedList<XRaySample> samples = new LinkedList<>();
        for (Map.Entry<File, LinkedList> entry : map.entrySet()) {
            samples = entry.getValue();
            allFiles.add(samples);
        }

        ArrayList<String> command = new ArrayList<>(); //arguments to enter in command line to run plotting script
        command.add(pythonVersion);
        command.add(scriptPath);

        String pythonArgs = new String();
        for (LinkedList<XRaySample> file: allFiles) {
            pythonArgs = "";
            for (XRaySample sample: file) {
                pythonArgs += sample.getData();
            }
            command.add(pythonArgs);
        }

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        int result = -1;
        result = commandExecutor.executeCommand();

        //Get output from the script
        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        System.out.println("\nPYTHON SCRIPT OUTPUT:");
        System.out.println(stdout);
        System.out.println("--------------------");
        //Error traces
        System.out.println("STDERR");
        System.out.println(stderr);
        printMemoryUsage();
    }

    private static HashMap<File, LinkedList> processArgs(String[] args, FileInput files) {
        try {
            int dataType = Integer.parseInt(args[0]);
            switch (dataType) {
                case 1: return files.getSampleMap();
                case 2: return files.getNoSampleMap();
                case 3: return files.getSampleBgMap();
                case 4: return files.getNoSampleBgMap();
                default: break;
            }
        } catch (NumberFormatException nfe) {
        }
        System.out.println("Error: Invalid argument");
        System.out.print("Enter 1 for data with sample, 2 for data with no sample, 3 for ");
        System.out.println("background data with sample, 4 for background data without sample");
        return files.getSampleMap();//Default to this data set if args are unintelligible
    }

    //Prints the amount of memory used by JVM
    private static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();// Run garbage collection
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Used memory: %d bytes%n ", memory);
        System.out.printf("Memory in use: %d megabytes%n", (memory/(1024L * 1024L)));
    }
}


