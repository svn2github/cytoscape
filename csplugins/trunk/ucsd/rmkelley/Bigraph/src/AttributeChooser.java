package ucsd.rmkelley.Bigraph;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
public class AttributeChooser{
	public static String getAttribute(String [] attrs){
		AttributeChooserDialog acDialog = new AttributeChooserDialog(attrs);	
		try{
			synchronized (acDialog){
				acDialog.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
		return acDialog.getResult();

	}
}
class AttributeChooserDialog extends JFrame implements ActionListener{
	String result;
	JList dataList;
	public AttributeChooserDialog(String [] attributes){
 		dataList = new JList(attributes);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(dataList,BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(this);
		getContentPane().add(ok,BorderLayout.SOUTH);
		this.setVisible(true);
		this.pack();
	}

	public String getResult(){
		return result;
	}

	public void actionPerformed(ActionEvent e){
		result = (String)dataList.getSelectedValue();
	        synchronized (this){
			notify();
		}
		this.dispose();	
	}
}
