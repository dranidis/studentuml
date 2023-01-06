package edu.city.studentuml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * A utility class that stores a list of recently accessed files in the user
 * preferences.
 */
public class RecentFiles {
    private static final int MAX_RECENT_FILES = 10;
    private static final String PREF_KEY_PREFIX = "recent_file_";

    private static RecentFiles instance;
    private Preferences prefs;

    private RecentFiles() {
        prefs = Preferences.userNodeForPackage(RecentFiles.class);
    }

    public static RecentFiles getInstance() {
        if (instance == null) {
            instance = new RecentFiles();
        }
        return instance;
    }

    /**
     * Adds a file to the list of recent files. If the file is already in the list,
     * it is moved to the front. If the list exceeds the maximum number of recent
     * files, the least recently accessed file is removed from the list.
     *
     * @param file the file to add
     */
    public void addRecentFile(String file) {
        // Load the list of recent files.
        List<String> recentFiles = getRecentFiles();

        // Add the new file to the list, removing any duplicates.
        recentFiles = addToList(recentFiles, file);

        // Store the list in the preferences.
        for (int i = 0; i < recentFiles.size(); i++) {
            prefs.put(PREF_KEY_PREFIX + i, recentFiles.get(i));
        }
    }

    /**
     * Returns the list of recent files.
     *
     * @return the list of recent files
     */
    public List<String> getRecentFiles() {
        List<String> recentFiles = new ArrayList<>();
        int i = 0;
        while (true) {
            String file = prefs.get(PREF_KEY_PREFIX + i, null);
            if (file == null) {
                break;
            }
            recentFiles.add(file);
            i++;
        }
        return recentFiles;
    }

    /**
     * Adds an item to a list, removing any duplicates.
     *
     * @param list the list
     * @param item the item to add
     * @return the modified list
     */
    private List<String> addToList(List<String> list, String item) {
        // Remove any duplicates.
        removeFromList(list, item);

        // Add the item to the beginning of the list.
        List<String> newList = new ArrayList<>();
        newList.add(item);
        newList.addAll(list);

        // If the list is too long, remove the last item.
        while (newList.size() > MAX_RECENT_FILES) {
            newList.remove(newList.size() - 1);
        }

        return newList;
    }

    private void removeFromList(List<String> list, String item) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (item.equals(list.get(i))) {
                list.remove(i);
                return;
            }
        }
    }
}