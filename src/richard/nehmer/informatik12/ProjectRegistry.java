package richard.nehmer.informatik12;

import java.io.*;
import java.util.*;

/**
 * Saves the projects added at runtime through the file picker, so they're
 * still registered next time the launcher starts. Stored as a plain
 * properties file next to the launcher itself — no external libraries.
 */
public class ProjectRegistry {

    private static final File FILE = new File(AppPaths.getBaseDir(), "external_projects.properties");

    /** Loads every previously-added project as a ready-to-register ExternalAppProject. */
    public static List<ExternalAppProject> loadAll() {
        List<ExternalAppProject> result = new ArrayList<>();
        if (!FILE.exists()) return result;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(FILE)) {
            props.load(in);
        } catch (IOException e) {
            return result; // corrupt or unreadable file: just start with none saved
        }

        int count = Integer.parseInt(props.getProperty("count", "0"));
        for (int i = 0; i < count; i++) {
            String name = props.getProperty(i + ".name");
            if (name == null) continue;
            result.add(new ExternalAppProject(
                    name,
                    emptyToNull(props.getProperty(i + ".windows", "")),
                    emptyToNull(props.getProperty(i + ".linux", "")),
                    emptyToNull(props.getProperty(i + ".mac", ""))
            ));
        }
        return result;
    }

    /** Appends one new entry to the saved list and rewrites the file. */
    public static void save(String name, String windowsPath, String linuxPath, String macPath) {
        Properties props = new Properties();
        if (FILE.exists()) {
            try (FileInputStream in = new FileInputStream(FILE)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }

        int count = Integer.parseInt(props.getProperty("count", "0"));
        props.setProperty(count + ".name", name);
        props.setProperty(count + ".windows", nullToEmpty(windowsPath));
        props.setProperty(count + ".linux", nullToEmpty(linuxPath));
        props.setProperty(count + ".mac", nullToEmpty(macPath));
        props.setProperty("count", String.valueOf(count + 1));

        try (FileOutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "Projects added at runtime via the launcher's file picker");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static String emptyToNull(String s) { return (s == null || s.isEmpty()) ? null : s; }
}
