package richard.nehmer.informatik12;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private final List<ProjectModule> projects = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> sidebarList = new JList<>(listModel);

    public Launcher() {
        super("School Projects");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // ---- Hardcoded / bundled projects ----
        // registerProject(new NextYearsProject());

        // ---- Projects added at runtime in a previous session ----
        for (ExternalAppProject saved : ProjectRegistry.loadAll()) {
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

        // Placeholder shown before anything is selected
        JPanel welcome = new JPanel(new BorderLayout());
        welcome.add(new JLabel("Select a project on the left", SwingConstants.CENTER), BorderLayout.CENTER);
        contentArea.add(welcome, "welcome");
        cardLayout.show(contentArea, "welcome");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    /** Registers a project both in-memory and in the visible sidebar/content area. */
    private void registerProject(ProjectModule project) {
        projects.add(project);
        listModel.addElement(project.getName());
        contentArea.add(project.getPanel(), project.getName());
    }

    /** Opens a file picker, wraps the chosen executable as a project, registers it, and saves it. */
    private void addExternalProjectViaFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select an executable (.AppImage, .exe, .app)");
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();

        String name = JOptionPane.showInputDialog(this, "Name for this project:", selected.getName());
        if (name == null || name.isBlank()) return; // cancelled

        String os = System.getProperty("os.name").toLowerCase();
        String windowsPath = null, linuxPath = null, macPath = null;
        String absolutePath = selected.getAbsolutePath();

        if (os.contains("win")) windowsPath = absolutePath;
        else if (os.contains("mac")) macPath = absolutePath;
        else linuxPath = absolutePath;

        ExternalAppProject newProject = new ExternalAppProject(name, windowsPath, linuxPath, macPath);
        registerProject(newProject);
        ProjectRegistry.save(name, windowsPath, linuxPath, macPath);

        sidebarList.setSelectedIndex(projects.size() - 1); // jump straight to the new project
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Launcher().setVisible(true));
    }
}