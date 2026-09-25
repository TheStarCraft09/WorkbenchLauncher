package richard.nehmer.informatik12;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A project added at runtime via the file picker. Unlike ExternalAppProject
 * (which expects separate pre-built binaries per platform), this wraps a
 * single executable that the launcher has copied into its own managed
 * storage:
 *   - a Linux AppImage: run directly
 *   - a Windows .exe: run through Wine, inside its own dedicated prefix
 *     under projects/prefixes/<name>, so games don't share Windows
 *     environments and one broken prefix can't affect another game.
 */
public class ManagedExecutable implements ProjectModule {

    public enum Type { APPIMAGE, WINDOWS_EXE }

    private final String name;
    private final String storedExecutableRelPath; // e.g. "projects/executeables/unix/game.AppImage"
    private final Type type;
    private final String winePrefixRelPath;        // only set for WINDOWS_EXE, e.g. "projects/prefixes/game"

    public ManagedExecutable(String name, String storedExecutableRelPath, Type type, String winePrefixRelPath) {
        this.name = name;
        this.storedExecutableRelPath = storedExecutableRelPath;
        this.type = type;
        this.winePrefixRelPath = winePrefixRelPath;
    }

    public String getStoredExecutableRelPath() { return storedExecutableRelPath; }
    public Type getType() { return type; }
    public String getWinePrefixRelPath() { return winePrefixRelPath; }

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
        JLabel status = new JLabel(type == Type.WINDOWS_EXE ? "Ready (via Wine)" : "Ready");
        status.setForeground(Color.GRAY);
        panel.add(status, gbc);

        gbc.gridy++;
        JButton launchButton = new JButton("Launch");
        launchButton.addActionListener(e -> {
            launchButton.setEnabled(false);
            status.setForeground(Color.GRAY);
            status.setText("Launching...");
            // Whole launch runs off the EDT: Wine prefix setup and waiting
            // for the game to exit can both take a while.
            new Thread(() -> launch(status, launchButton)).start();
        });
        panel.add(launchButton, gbc);

        return panel;
    }

    private void launch(JLabel status, JButton launchButton) {
        File exeFile = AppPaths.resolve(storedExecutableRelPath);
        if (!exeFile.exists()) {
            fail(status, launchButton, "File missing: " + exeFile.getAbsolutePath());
            return;
        }

        try {
            Process process;

            if (type == Type.APPIMAGE) {
                exeFile.setExecutable(true); // AppImages often lose +x after being copied
                process = new ProcessBuilder(exeFile.getAbsolutePath())
                        .directory(exeFile.getParentFile())
                        .start();

            } else {
                File prefixDir = AppPaths.resolve(winePrefixRelPath);
                if (!prefixDir.exists()) {
                    SwingUtilities.invokeLater(() -> status.setText("Setting up Wine prefix..."));
                    prefixDir.mkdirs();
                    ProcessBuilder init = new ProcessBuilder("wine", "wineboot", "--init");
                    init.environment().put("WINEPREFIX", prefixDir.getAbsolutePath());
                    init.start().waitFor();
                }

                SwingUtilities.invokeLater(() -> status.setText("Running via Wine..."));
                ProcessBuilder pb = new ProcessBuilder("wine", exeFile.getAbsolutePath());
                pb.environment().put("WINEPREFIX", prefixDir.getAbsolutePath());
                pb.directory(exeFile.getParentFile());
                process = pb.start();
            }

            SwingUtilities.invokeLater(() -> {
                status.setText(type == Type.WINDOWS_EXE ? "Running via Wine..." : "Running...");
                status.setForeground(new Color(0, 130, 0));
            });

            process.waitFor();

            SwingUtilities.invokeLater(() -> {
                status.setText(type == Type.WINDOWS_EXE ? "Ready (via Wine)" : "Ready");
                status.setForeground(Color.GRAY);
                launchButton.setEnabled(true);
            });

        } catch (IOException ex) {
            String msg = type == Type.WINDOWS_EXE
                    ? "Failed to launch \u2014 is Wine installed? (" + ex.getMessage() + ")"
                    : "Failed to launch: " + ex.getMessage();
            fail(status, launchButton, msg);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void fail(JLabel status, JButton launchButton, String message) {
        SwingUtilities.invokeLater(() -> {
            status.setText(message);
            status.setForeground(Color.RED);
            launchButton.setEnabled(true);
        });
    }
}
