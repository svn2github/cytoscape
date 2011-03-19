import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class BasePAP extends java.applet.Applet implements ActionListener {

   TextField fld1 = new TextField(40);
   TextField fld2 = new TextField(40);
   TextArea tarea = new TextArea(8,50);
   Button abt = new Button("ANNEAL");
 
   public void init(){
     
      fld1.setEditable(true);
      fld1.setFont(new Font("Courier", Font.PLAIN, 14));
      fld2.setEditable(true);
      fld2.setFont(new Font("Courier", Font.PLAIN, 14));
      tarea.setEditable(false);
      tarea.setFont(new Font("Courier", Font.PLAIN, 14));

      Panel p1 = new Panel();
      Panel p2 = new Panel();

      p1.setLayout(new BorderLayout());
      p2.setLayout(new BorderLayout());

      p1.add("North", new Label("PUT TWO SEQUENCES IN THE FIELDS BELOW."));
      p1.add("Center",fld1);
      p1.add("South",fld2);
      p2.add("South",tarea);
      p2.add("North",abt);

      setLayout(new BorderLayout());
      add("North", p1);
      add("South", p2);

      abt.addActionListener(this);
     
   }
 
   public void start(){
      addItem("Starting this Applet...");
      repaint();
    }

   public void stop(){
      addItem("Stopping this Applet...");
    }

   public void destroy(){
      addItem("Preparing for unloading this applet...");
    }

   public void actionPerformed(ActionEvent e) {
     anneal();
   }

   public void anneal(){

     String seq1 = fld1.getText();
     String seq2 = fld2.getText();
     if(seq1.length() == 0 || seq2.length() == 0){
       tarea.setText("Please enter two sequences.");
       return;
     }

      seq1 = seq1.toLowerCase();
      seq1 = seq1.replace('u', 't');
      seq2 = seq2.toLowerCase();
      seq2 = seq2.replace('u', 't');

      SeqGraph sg = new SeqGraph(seq1, seq2);

      double min_fe = sg.find_min(0, 0);
      DecimalFormat min_fe_frmt = new DecimalFormat("0.0");

      sg.getres_SeqGraph();
      tarea.setText("Result:\n\n" + 
		    sg.match_gr1() + '\n' + sg.r1() + '\n' +
                    sg.r2() + '\n' + sg.match_gr2() + '\n' +
                    "\nFree energy = " + min_fe_frmt.format(min_fe));
                    // String.format("%3.1f", 1.0*min_fe));
      }

   public void addItem(String newWord){
      System.out.println(newWord);
      repaint();
    }


}

