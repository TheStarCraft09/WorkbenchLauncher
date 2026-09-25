package richard.nehmer.informatik12;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private final List<ProjectModule> projects = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> sidebarList = new JList<>(listModel);

    public Launcher() {
        super("WorkBench Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // ---- Hardcoded / bundled projects ----
        // registerProject(new NextYearsProject());

        // ---- Projects added at runtime in a previous session ----
        for (ManagedExecutable saved : ProjectRegistry.loadAll()) {
            registerProject(saved);
        }

        sidebarList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sidebarList.getSelectedIndex() >= 0) {
                cardLayout.show(contentArea, projects.get(sidebarList.getSelectedIndex()).getName());
            }
        });

        JScrollPane sidebarScroll = new JScrollPane(sidebarList);

        JButton addButton = new JButton("+ Add project");
        addButton.addActionListener(e -> addExternalProjectViaFileChooser());

        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.add(sidebarScroll, BorderLayout.CENTER);
        sidebarPanel.add(addButton, BorderLayout.SOUTH);

        JPanel welcome = new JPanel(new BorderLayout());
        welcome.add(new JLabel("Select a project on the left", SwingConstants.CENTER), BorderLayout.CENTER);
        contentArea.add(welcome, "welcome");
        cardLayout.show(contentArea, "welcome");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private void registerProject(ProjectModule project) {
        projects.add(project);
        listModel.addElement(project.getName());
        contentArea.add(project.getPanel(), project.getName());
    }

    /**
     * Opens a file picker, copies the chosen executable into the launcher's
     * own managed storage (projects/executeables/unix or /windows), sets up
     * a dedicated Wine prefix for .exe files, registers the project, and
     * saves it so it's still there next time the launcher starts.
     */
    private void addExternalProjectViaFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select an executable (.AppImage or .exe)");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        String fileName = selected.getName();
        String lower = fileName.toLowerCase();

        ManagedExecutable.Type type;
        String subfolder;
        if (lower.endsWith(".appimage") || lower.endsWith(".x86_64")) {
            type = ManagedExecutable.Type.APPIMAGE;
            subfolder = "projects/executeables/unix";
        } else if (lower.endsWith(".exe")) {
            type = ManagedExecutable.Type.WINDOWS_EXE;
            subfolder = "projects/executeables/windows";
        } else {
            JOptionPane.showMessageDialog(this, "Only .AppImage and .exe files are supported.",
                    "Unsupported file", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String suggestedName = fileName.replaceAll("(?i)\\.(appimage|exe)$", "");
        String name = JOptionPane.showInputDialog(this, "Name for this project:", suggestedName);
        if (name == null || name.isBlank()) return; // cancelled

        try {
            File destDir = AppPaths.resolve(subfolder);
            destDir.mkdirs();
            File destFile = new File(destDir, fileName);
            Files.copy(selected.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (type == ManagedExecutable.Type.APPIMAGE) destFile.setExecutable(true);

            String storedRelPath = subfolder + "/" + fileName;
            String winePrefixRelPath = null;
            if (type == ManagedExecutable.Type.WINDOWS_EXE) {
                String baseName = fileName.replaceAll("(?i)\\.exe$", "");
                winePrefixRelPath = "projects/prefixes/" + baseName;
            }

            ManagedExecutable newProject = new ManagedExecutable(name, storedRelPath, type, winePrefixRelPath);
            registerProject(newProject);
            ProjectRegistry.save(name, storedRelPath, type, winePrefixRelPath);
            sidebarList.setSelectedIndex(projects.size() - 1);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to copy executable: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Launcher().setVisible(true));
    }
}