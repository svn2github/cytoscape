package cytoscape.demo;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class DemoUtil {
    private static int counter = 0;
    private static Icon pyramidIcon1;
    private static Icon pyramidIcon2;

    public static JScrollPane createSamplePanel(String title) {
        JEditorPane editorPane = new JEditorPane ();
        editorPane.setContentType("text/html");
        editorPane.setText(getSampleToolTip(title));
        editorPane.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane (editorPane);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setMinimumSize(new Dimension (100, 100));
        return scrollPane;
    }

    public static String getSampleToolTip(String title) {
        return new String("<HTML><CENTER><TABLE WIDTH=300>" +
                "   <TR><TD><h4>" + title +
                "   </h4><hr></TD></TR>" +
                "   <TR><TD>" +
                "   Lorem ipsum dolor sit amet, consectetuer adipiscing elit. " +
                "   Fusce sed magna. Vestibulum scelerisque. Maecenas luctus. " +
                "   </TD></TR></TABLE></html>");
    }

    public static Icon getPyramidIcon1() {
        if (pyramidIcon1 == null) {
            URL iconUrl1 = CytoPanelDemo.class.getResource
                    ("resources/pyramid.png");
            pyramidIcon1 = new ImageIcon(iconUrl1);
        }
        return pyramidIcon1;
    }

    public static Icon getPyramidIcon2() {
        if (pyramidIcon1 == null) {
            URL iconUrl1 = CytoPanelDemo.class.getResource
                    ("resources/pyramid2.png");
            pyramidIcon1 = new ImageIcon(iconUrl1);
        }
        return pyramidIcon1;
    }
}
