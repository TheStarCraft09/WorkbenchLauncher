package richard.nehmer.informatik12;

import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;

/**
 * Saves the projects added at runtime through the file picker, so they're
 * still registered next time the launcher starts. Stored as a plain
 * properties file next to the launcher itself — no external libraries.
 */
public class ProjectRegistry {

    private static final File FILE = new File(AppPaths.getBaseDir(), "external_projects.properties");

    public static List<ManagedExecutable> loadAll() {
        List<ManagedExecutable> result = new ArrayList<>();
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
            String path = props.getProperty(i + ".path");
            String typeStr = props.getProperty(i + ".type");
            String prefix = props.getProperty(i + ".prefix", "");
            if (name == null || path == null || typeStr == null) continue;

            ManagedExecutable.Type type = ManagedExecutable.Type.valueOf(typeStr);
            result.add(new ManagedExecutable(name, path, type, prefix.isEmpty() ? null : prefix));
        }
        return result;
    }

    /** Appends one new entry to the saved list and rewrites the file. */
    public static void save(String name, String storedExecutableRelPath,
                            ManagedExecutable.Type type, String winePrefixRelPath) {
        Properties props = new Properties();
        if (FILE.exists()) {
            try (FileInputStream in = new FileInputStream(FILE)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }

        int count = Integer.parseInt(props.getProperty("count", "0"));
        props.setProperty(count + ".name", name);
        props.setProperty(count + ".path", storedExecutableRelPath);
        props.setProperty(count + ".type", type.name());
        props.setProperty(count + ".prefix", winePrefixRelPath == null ? "" : winePrefixRelPath);
        props.setProperty("count", String.valueOf(count + 1));

        try (FileOutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "Projects added at runtime via the launcher's file picker");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
