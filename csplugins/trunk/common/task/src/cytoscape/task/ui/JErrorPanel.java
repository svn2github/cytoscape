package cytoscape.task.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Common UI element for displaying errors and stack traces.
 */
public class JErrorPanel extends JPanel {
    /**
     * The Error Object
     */
    private Throwable t;

    /**
     * A Human Readable Error Message
     */
    private String userErrorMessage;

    /**
     * Flag to Show/Hide Error Details.
     */
    private boolean showDetails = false;

    /**
     * Show/Hide Details Button.
     */
    private JButton detailsButton;

    /**
     * Scroll Pane used to display Stack Trace Elements.
     */
    private JScrollPane detailsPane;

    /**
     * Window Owner.
     */
    private Window owner;

    private static final String SHOW_TEXT = "Show Error Details";
    private static final String HIDE_TEXT = "Hide Error Details";

    /**
     * Private Constructor.
     *
     * @param owner            Window owner.
     * @param t                Throwable Object. May be null.
     * @param userErrorMessage User Readable Error Message. May be null.
     */
    JErrorPanel(Window owner, Throwable t, String userErrorMessage) {
        if (owner == null) {
            throw new IllegalArgumentException("owner parameter is null.");
        }
        this.owner = owner;
        this.t = t;
        this.userErrorMessage = userErrorMessage;
        initUI();
    }

    /**
     * Initializes UI.
     */
    private void initUI() {
        //  Use  Border Layout
        setLayout(new BorderLayout());

        //  Create North Panel with Error Message and Button.
        JPanel northPanel = createNorthPanel();
        add(northPanel, BorderLayout.NORTH);

        //  Create Center Panel with Error Details.
        JScrollPane centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        //  Repack and validate the owner
        owner.pack();
        owner.validate();
    }

    /**
     * Creates North Panel with Error Message and Details Button.
     *
     * @return JPanel Object.
     */
    private JPanel createNorthPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        if (userErrorMessage == null) {
            userErrorMessage = new String("An Error Has Occurred.  "
                    + "Please try again.");
        }

        //  Create Left Margin
        panel.add(Box.createHorizontalStrut(10));

        //  Add Error Message with Custom Font Properties
        JLabel errorLabel = new JLabel(StringUtils.truncateOrPadString
                ("Error:  " + userErrorMessage));
        errorLabel.setForeground(Color.BLUE);
        Font font = errorLabel.getFont();
        errorLabel.setFont(new Font(font.getFamily(), Font.BOLD,
                font.getSize()));
        panel.add(errorLabel);

        //  Conditionally Add Details Button
        conditionallyAddDetailsButton(panel);

        return panel;
    }

    /**
     * Creates Center Panel with Error Details.
     *
     * @return JScrollPane Object.
     */
    private JScrollPane createCenterPanel() {
        detailsPane = new JScrollPane();

        if (t != null && t.getStackTrace() != null) {

            //  Get Stack Trace
            StackTraceElement ste[] = t.getStackTrace();

            //  Create a Tree of Stack Trace Elements
            DefaultMutableTreeNode top =
                    new DefaultMutableTreeNode(t.getMessage());

            //  Create Individual Nodes in JTree
            DefaultMutableTreeNode current = top;
            for (int i = 0; i < ste.length; i++) {
                DefaultMutableTreeNode node =
                        new DefaultMutableTreeNode(ste[i]);
                current.add(node);
                current = node;
            }

            //  Create a JTree Object
            JTree tree = new JTree(top);

            //  Open all Nodes
            tree.scrollPathToVisible(new TreePath(current.getPath()));
            tree.setBorder(new EmptyBorder(4, 10, 10, 10));
            detailsPane.setViewportView(tree);
            detailsPane.setPreferredSize(new Dimension(10, 150));

        }
        //  By default, do not show
        detailsPane.setVisible(false);

        return detailsPane;
    }

    /**
     * Adds a Show/Hide Details Button.
     *
     * @param panel JPanel Object.
     */
    private void conditionallyAddDetailsButton(JPanel panel) {
        if (t != null && t.getStackTrace() != null) {
            detailsButton = new JButton(SHOW_TEXT);
            detailsButton.addActionListener(new ActionListener() {

                /**
                 * Toggle Show/Hide Error Details.
                 *
                 * @param e ActionEvent.
                 */
                public void actionPerformed(ActionEvent e) {
                    showDetails = !showDetails;
                    detailsPane.setVisible(showDetails);

                    if (showDetails) {
                        detailsButton.setText(HIDE_TEXT);
                    } else {
                        detailsButton.setText(SHOW_TEXT);
                    }
                    owner.pack();
                    owner.validate();
                }
            });
            panel.add(Box.createHorizontalGlue());
            detailsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            panel.add(detailsButton);
        }
    }

    /**
     * Main Method.
     * Used for testing purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        try {
            frame.add(new JPanel(), null);
        } catch (Error e) {
            JErrorPanel errorPanel = new JErrorPanel(frame, e,
                    "User Interface Error");
            frame.getContentPane().add(errorPanel, BorderLayout.CENTER);
            frame.pack();
            frame.show();
        }
    }
}