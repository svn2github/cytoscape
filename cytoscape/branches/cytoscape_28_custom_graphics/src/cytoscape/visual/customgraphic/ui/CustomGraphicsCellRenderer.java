package cytoscape.visual.customgraphic.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXPanel;

import cytoscape.visual.customgraphic.CustomGraphicsUtil;
import cytoscape.visual.customgraphic.CyCustomGraphics;

public class CustomGraphicsCellRenderer extends JPanel implements
		ListCellRenderer {

	private static final long serialVersionUID = 8040076496780883222L;

	private static final int ICON_SIZE = 130;

	private final Map<CyCustomGraphics<?>, Component> panelMap;

	public CustomGraphicsCellRenderer() {
		panelMap = new HashMap<CyCustomGraphics<?>, Component>();
	}

	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JPanel target = null;
		if (value != null && value instanceof CyCustomGraphics<?>) {
			CyCustomGraphics<?> cg = (CyCustomGraphics<?>) value;
			target = (JPanel) panelMap.get(cg);
			if (target == null) {
				target = createImagePanel(cg);
				panelMap.put(cg, target);
			}

		}

		if (isSelected) {
			target.setBorder(BorderFactory.createEtchedBorder());
		} else
			target.setBorder(null);

		return target;
	}

	private JPanel createImagePanel(final CyCustomGraphics<?> cg) {
		final Image image = cg.getImage();
		if (image == null)
			return this;

		final JXImagePanel imagePanel = new JXImagePanel();
		imagePanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		imagePanel.setStyle(JXImagePanel.Style.CENTERED);

		if (image.getHeight(null)<ICON_SIZE && image.getWidth(null) < 200)
			imagePanel.setImage(image);
		else
			imagePanel.setImage(CustomGraphicsUtil.getResizedImage(image, null, ICON_SIZE, true));
		
		imagePanel.setBackground(Color.white);
		final JXPanel buttonPanel = new JXPanel();
		buttonPanel.setLayout(new BorderLayout());
		final JLabel label = new JLabel(cg.getDisplayName());
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(100, 20));
		buttonPanel.add(label, BorderLayout.SOUTH);
		buttonPanel.add(imagePanel, BorderLayout.CENTER);
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setPreferredSize(new Dimension(200, 150));
		return buttonPanel;
	}
}
