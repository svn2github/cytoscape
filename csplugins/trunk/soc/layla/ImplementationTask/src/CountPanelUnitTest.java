import static org.junit.Assert.*;
import junit.framework.TestCase;
import javax.swing.JTextArea;

import org.junit.Test;


public class CountPanelUnitTest extends TestCase {
	
	CountPanel myPanel = new CountPanel();

	@Test
	public void testCountPanel() {
		assertEquals("Word Counts", myPanel.getTitle().getText());		
	}

	@Test
	public void testAddText() {
		myPanel.addText("Test String");
		JTextArea myText = myPanel.getText();
		assertEquals("Test String", myText.getText());
		
	}

	@Test
	public void testClearText() {
		myPanel.addText("Another line of text");
		myPanel.clearText();
		JTextArea myText = myPanel.getText();
		assertEquals("", myText.getText());
		
	}

}
