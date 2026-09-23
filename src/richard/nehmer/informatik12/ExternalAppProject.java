package richard.nehmer.informatik12;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A ProjectModule that doesn't run inside the launcher's own window —
 * it launches an external native build (Godot AppImage / .exe / .app)
 * as a separate process, on whichever OS the launcher is running on.
 *
 * Reusable: build this once, then register a new instance per game
 * with just its name + the three build paths.
 */
public class ExternalAppProject implements ProjectModule {

    private final String name;
    private final String windowsExeRelPath;   // e.g. "games/mygame/mygame.exe"
    private final String linuxAppImageRelPath; // e.g. "games/mygame/mygame.AppImage"
    private final String macAppRelPath;        // e.g. "games/mygame/mygame.app"

    public ExternalAppProject(String name, String windowsExeRelPath,
                              String linuxAppImageRelPath, String macAppRelPath) {
        this.name = name;
        this.windowsExeRelPath = windowsExeRelPath;
        this.linuxAppImageRelPath = linuxAppImageRelPath;
        this.macAppRelPath = macAppRelPath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel(name);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title, gbc);

        gbc.gridy++;
        JLabel status = new JLabel("Ready");
        status.setForeground(Color.GRAY);
        panel.add(status, gbc);

        gbc.gridy++;
        JButton launchButton = new JButton("Launch");
        launchButton.addActionListener(e -> {
            launchButton.setEnabled(false);
            status.setText("Launching...");
            launch(status, launchButton);
        });
        panel.add(launchButton, gbc);

        return panel;
    }

    /** Resolves, starts, and tracks the correct native build for this OS. */
    private void launch(JLabel status, JButton launchButton) {
        String os = System.getProperty("os.name").toLowerCase();
        String relPath = os.contains("win") ? windowsExeRelPath
                : os.contains("mac") ? macAppRelPath
                : linuxAppImageRelPath;

        if (relPath == null) {
            status.setText("No build available for this OS");
            status.setForeground(Color.RED);
            launchButton.setEnabled(true);
            return;
        }

        File exeFile = resolve(relPath);
        List<String> command = new ArrayList<>();

        if (os.contains("win")) {
            command.add(exeFile.getAbsolutePath());
        } else if (os.contains("mac")) {
            command.add("open");
            command.add(exeFile.getAbsolutePath()); // .app bundles are launched via `open`, not run directly
        } else {
            exeFile.setExecutable(true); // AppImages usually need +x set after download/export
            command.add(exeFile.getAbsolutePath());
        }

        if (!exeFile.exists()) {
            status.setText("Build not found: " + exeFile.getAbsolutePath());
            status.setForeground(Color.RED);
            launchButton.setEnabled(true);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(exeFile.getParentFile()); // run with its own folder as working dir (assets, saves, etc.)
            Process process = pb.start();

            status.setText("Running...");
            status.setForeground(new Color(0, 130, 0));

            // Watch for exit off the EDT, then flip the UI back on the EDT
            new Thread(() -> {
                try {
                    process.waitFor();
                } catch (InterruptedException ignored) {
                }
                SwingUtilities.invokeLater(() -> {
                    status.setText("Ready");
                    status.setForeground(Color.GRAY);
                    launchButton.setEnabled(true);
                });
            }).start();

        } catch (IOException ex) {
            status.setText("Failed to launch: " + ex.getMessage());
            status.setForeground(Color.RED);
            launchButton.setEnabled(true);
        }
    }

    private File resolve(String relativePath) {
        return AppPaths.resolve(relativePath);
    }
}