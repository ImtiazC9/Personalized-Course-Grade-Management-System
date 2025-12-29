package application;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all file system interactions, including saving and loading User and Course objects.
 * Uses Java Serialization to write/read objects to/from the local disk.
 */
public class DataManager {

    private static final String DATA_DIR = "data";
    private static final String USERS_DIR = DATA_DIR + File.separator + "users";
    private static final String COURSES_DIR = DATA_DIR + File.separator + "courses";

    // Static block runs once when the class is loaded to ensure data directories exist
    static {
        try {
            // Creates the 'data/users' and 'data/courses' directories
            Files.createDirectories(Paths.get(USERS_DIR));
            Files.createDirectories(Paths.get(COURSES_DIR));
        } catch (IOException e) {
            System.err.println("Error initializing data directories: " + e.getMessage());
        }
    }

    // --- User Management ---

    /**
     * Saves a User object to the file system (Serialization).
     * The file name is based on the username (e.g., data/users/john.dat).
     */
    public static void saveUser(User user) throws IOException {
        String filepath = USERS_DIR + File.separator + user.getUsername() + ".dat";
        // ObjectOutputStream is used for serialization
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(user);
        }
    }

    /**
     * Loads a User object from the file system (Deserialization).
     * Returns null if the user file does not exist.
     */
    public static User loadUser(String username) {
        String filepath = USERS_DIR + File.separator + username + ".dat";
        File file = new File(filepath);
        if (!file.exists()) return null;

        // ObjectInputStream is used for deserialization
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user " + username + ": " + e.getMessage());
            return null;
        }
    }

    // --- Course Management ---

    /**
     * Saves a Course object to the file system.
     * The file name is formatted as [username]_[courseID].dat (e.g., data/courses/john_CS101.dat).
     */
    public static void saveCourse(Course course) throws IOException {
        // Filename format: username_courseID.dat to ensure uniqueness per user
        String filename = course.getOwnerUsername() + "_" + course.getId() + ".dat";
        String filepath = COURSES_DIR + File.separator + filename;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(course);
        }
    }

    /**
     * Loads all Course objects associated with a specific username from the file system.
     */
    public static List<Course> loadCoursesForUser(String username) {
        List<Course> userCourses = new ArrayList<>();
        File folder = new File(COURSES_DIR);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                // Filters files by username prefix and .dat extension
                if (file.isFile() && file.getName().startsWith(username + "_") && file.getName().endsWith(".dat")) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Course c = (Course) ois.readObject();
                        userCourses.add(c);
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Error loading course file " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        return userCourses;
    }
}