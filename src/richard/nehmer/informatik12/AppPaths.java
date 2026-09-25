package richard.nehmer.informatik12;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Resolves paths relative to wherever the launcher itself is running from
 * (its jar/class location) — not the current working directory, which
 * changes depending on how the app was started.
 */
public class AppPaths {

    public static File getBaseDir() {
        try {
            return new File(AppPaths.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            return new File("."); // fallback: current working directory
        }
    }

    /**
     * Resolves a path that may be:
     *  - absolute (a file the user picked from anywhere on disk), or
     *  - relative (a build bundled inside the launcher's own project folder)
     */
    public static File resolve(String path) {
        File f = new File(path);
        return f.isAbsolute() ? f : new File(getBaseDir(), path);
    }
}