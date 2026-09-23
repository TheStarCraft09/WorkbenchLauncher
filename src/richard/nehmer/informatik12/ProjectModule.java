package richard.nehmer.informatik12;

import javax.swing.JPanel;

/**
 * Every school project you build implements this.
 * The launcher only ever talks to projects through this interface,
 * so it never needs to change when you add a new one.
 */
public interface ProjectModule {

    /** Name shown in the sidebar list. */
    String getName();

    /** The panel shown when this project is selected. Build it fresh each call. */
    JPanel getPanel();
}
