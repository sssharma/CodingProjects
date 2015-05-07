
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/* This dialog can be constructed in several ways:
  1. Message dialog: new simpleDialog(title, msg): see example in main
  2. A simple text and slide bar dialog: see example in main
  3. Arbitray componets: see example in main
  4. Vary control panel: choice of OK, CANCEL, RESET buttons in the panel
  5. extends the simpleDialog example: see main myDialog and okCallback routine
*/

public class simpleDialog extends JDialog implements ActionListener{
   public JButton buttOK;

   JPanel topPanel = new JPanel(); 
   public JPanel buttPanel = new JPanel();
   GridBagConstraints gbc = new GridBagConstraints();
   GridBagLayout gb = new GridBagLayout(); 

   public JButton getMiddleButton() {
      Object [] bary = (Object[])buttPanel.getComponents();
      if (bary.length == 2) return null;
      else return (JButton)bary[1];
   }
   
   public simpleDialog() {
     init();
   }
   public simpleDialog(String ok, String cancel, String reset) {
     init();
     createControl(ok, cancel, reset);
   }
   public simpleDialog(String title, String ok, String cancel, String reset) {
     setTitle(title);
     init();
     createControl(ok, cancel, reset);
   }

   public simpleDialog(String title) {
     setTitle(title);
     init();
   }

   public simpleDialog(String title, scalePanel sp) {
     setTitle(title);
     init();
     topPanel.add(sp);
     createControl("OK", "CANCEL", null);
     addControl();
     show();
   }

   public simpleDialog(String title, String msg) {
     setTitle(title);
     init();

     // separate the lines
     JPanel labelPanel = new JPanel();
     String linefeed = (String)System.getProperty("line.separator"); 
     String newline = "\n";
     String sep=newline;
     if (msg.indexOf(linefeed) >=0) sep = linefeed;
   
     StringTokenizer token = new StringTokenizer(msg,sep); 
     JLabel [] jlabel = new JLabel[token.countTokens()];
     for (int i=0; i<jlabel.length; i++)
        labelPanel.add(new JLabel(token.nextToken()));
	
     labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
     topPanel.add(labelPanel);
     createControl("OK", null, null);
     addControl();
     show();
   }
     

   public void init() {
     Container contentPane = this.getContentPane();
     topPanel.setLayout(gb);
     topPanel.setBorder(BorderFactory.createEmptyBorder(40,20,20,20));
     JScrollPane scrollPane = new JScrollPane(topPanel);
     contentPane.add(scrollPane);
   }
  
   public void clear() {
     topPanel.removeAll();
     buttPanel.removeAll();
     pack();
   }
   public void addObj(Object obj) {
     gbc.anchor = GridBagConstraints.WEST;  
     //gbc.fill = GridBagConstraints.NONE;  
     gbc.gridx= 0;
     gbc.gridwidth = GridBagConstraints.REMAINDER; 
     gb.setConstraints( (JComponent)obj, gbc );
     topPanel.add((JComponent)obj);
   } 
   public void addControl() {
     if (buttPanel.getComponentCount() == 0) createControl("OK","RESET","CANCEL");
     gbc.anchor = GridBagConstraints.CENTER;  
     gb.setConstraints( buttPanel, gbc );
     topPanel.add(buttPanel);
     pack();
     Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
     Dimension d2 = getSize();
     setLocation((d.width-d2.width)/2, (d.height-d2.height)/2);
   }
   public void setBorder(int top, int left, int bottom, int right) {
      topPanel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
   }
   public void addControl(Object obj) {
     pack();
     Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
     Dimension d2 = getSize();
     setLocation((d.width-d2.width)/2, (d.height-d2.height)/2);
   }

   public void createControl(String ok, String reset, String cancel) { 

     ok = padding(ok);
     buttPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
     buttPanel.removeAll();
     buttOK = new JButton   (ok);
     buttOK.setActionCommand("OK");
     buttPanel.add(buttOK);
     buttOK.addActionListener(this);

     if (reset!= null) {
        reset = padding(reset);
        JButton buttRESET = new JButton(reset);
        buttRESET.setActionCommand("RESET");
        buttRESET.addActionListener(this);
        buttPanel.add(buttRESET);
     }
     if (cancel != null) {
       cancel = padding(cancel);
       JButton buttCANCEL = new JButton(cancel);
       buttPanel.add(buttCANCEL);
        buttCANCEL.setActionCommand("CANCEL");
       buttCANCEL.addActionListener(this);
     }
  }
  public static JPanel createTextField(String label1, JTextField tf, String label2) {
     JPanel panel = new JPanel();
     panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS)); 
     String val = tf.getText();
/*
     if (val == null || val.length() < 4) {
		val = "     "+val; 
		tf.setText(val);
     }
*/
     //tf.setPreferredSize(new Dimension(60, 25));
     if (label1 != null) panel.add(new JLabel(label1));
     panel.add(tf);
     if (label2 != null) panel.add(new JLabel(label2));
     return panel;
  }
  public JPanel combine(JComponent c1, JComponent c2, JComponent c3) {
     JPanel panel = new JPanel();
     GridBagConstraints gbc = new GridBagConstraints();    
     GridBagLayout gb = new GridBagLayout();
     panel.setLayout(gb);
     //gbc.anchor = GridBagConstraints.WEST;  
     gbc.fill = GridBagConstraints.NONE;
     gbc.weightx= 0.4;
     //gbc.gridx= 0;
     //gbc.gridy= 0;
     gb.setConstraints( (JComponent)c1, gbc );
     panel.add(c1);

     //gbc.gridx= 1;
     gbc.weightx= 0.4;
     gb.setConstraints( (JComponent)c2, gbc );
     panel.add(c2);

     gbc.weightx= 0.2;
     //gbc.gridx= 2;
     gbc.gridwidth = GridBagConstraints.REMAINDER; 
     gb.setConstraints( (JComponent)c3, gbc );
     panel.add(c3);
     gbc.weightx= 1;
     gbc.gridx= 0;
     return panel;
  }
  
  public JPanel combine(JComponent c1, JComponent c2) {
     JPanel panel = new JPanel();
     GridBagConstraints gbc = new GridBagConstraints();    
     GridBagLayout gb = new GridBagLayout();
     panel.setLayout(gb);
     gbc.anchor = GridBagConstraints.WEST;  
     gbc.fill = GridBagConstraints.NONE;
     //gbc.gridx= 0;
     gb.setConstraints( (JComponent)c1, gbc );
     panel.add(c1);
     gbc.gridwidth = GridBagConstraints.REMAINDER; 
     gb.setConstraints( (JComponent)c2, gbc );
     panel.add(c2);
     return panel;
  }
  public String padding(String str) {
    int len = str.length();
    if (len >= 10) return str;

    int n = 10;
    int m = (n-len)/2;
    String blkstr="";
    for (int i=0; i<m; i++) 
      blkstr = blkstr +" ";
    if (len %2 == 1) 
       return (blkstr + str + blkstr + " ");
    else 
       return (blkstr + str + blkstr);
  }	

  public void addVspace(int npix) {
     JPanel jc = new JPanel();
     jc.setBorder(BorderFactory.createEmptyBorder(npix,0,0,0));
     gb.setConstraints( jc, gbc );
     topPanel.add(jc);
  }

  public void okCallback() {
     System.out.println("   in simple dialog okcallback");
     hide();
  }

  public void cancelCallback() {
     hide();
  }

  public void resetCallback() {
     hide();
  }
  
  public void setValue() {}

  public void actionPerformed(ActionEvent evt) {
      String action = evt.getActionCommand();
      if (action.equals("OK") ) okCallback(); 
      else if (action.equals("CANCEL") ) cancelCallback(); 
      else if (action.equals("RESET") ) resetCallback(); 
  }
	    
  public JPanel getTopPanel() { return topPanel;}


  public static void main(String [] argv) {


     final scalePanel sp = new scalePanel("Depth", 0, 100, 0, 16000);

     //following line is an example of simple text and slide bar dialog
     //simpleDialog dialog = new simpleDialog("Input Depth",sp);

     String msg="Do you want close the file\nPlease log off\nRun imat again";
     // following line is an example of simple msg dialog
     //simpleDialog dialog = new simpleDialog("Input Depth",msg);


/*
     // following is an example of several componets dialog
     simpleDialog dialog = new simpleDialog("db extraction");
     monthPanel month = new monthPanel("Select Month", 1);
     dialog.addObj(month);
     dialog.addObj(sp);
     dialog.addControl();
     dialog.show();
*/

     String [] labels2= {
	"X axis in nm Y in ft",
	"X axis in nm Y in kyd",
	"X axis in nm Y in yd",
	"X axis in m Y in m",
     };
     final groupPanel gp1 = new groupPanel("Axis", 1, labels2, groupPanel.RADIO);
        gp1.setValue(2);

     String [] labels= {"TL", "SE", "ECHO"};
     boolean [] v = { true, false, true};
     final groupPanel gp2 = new groupPanel("FileType", 2, labels, groupPanel.CHECKBOX);
       gp2.setValue(v);

     final comboPanel month = new comboPanel("db Month", 5);
     Object [] obj = {month, sp};
     //final groupPanel gp3 = new groupPanel(null, 2, obj, groupPanel.ANY);
     final groupPanel gp3 = new groupPanel(null,month, sp);

     String [] label3= {"srcon", "ecvon","buoy"};
     final JList gp4 = new JList(label3);

     String [] label4= {"GDEM", "ETOPO5", "BEST"};
     final comboPanel cb = new comboPanel("db base", 1, label4);

     class myDialog extends simpleDialog {

        public myDialog(String str) {
          super(str);
        }

        public void okCallback() {
	  System.out.println();
          int v= gp1.getRadioValue();
	  System.out.println("  radio selected is v=" + v);

          boolean [] b = gp2.getCheckboxValue();
	  System.out.println();
          for (int i=0; i<b.length; i++)
	    System.out.println(" i=" + i + "  checkbox value=" + b[i]);

	  System.out.println();
	  System.out.println(" month value=" + month.getValue());
	  System.out.println();
	  System.out.println(" spvalue=" + sp.getValue());

	  System.out.println();

	  System.out.println();
	  System.out.println(" database value=" + cb.getValue());

	  hide();
        }
     }

     myDialog dialog = new myDialog("Toggle Option");
     dialog.addObj(gp1);
     dialog.addVspace(30);
     dialog.addObj(gp2);
     dialog.addObj(gp3);
     dialog.addObj(gp4);
     dialog.addObj(cb);
     //dialog.createControl("Yes", (String)null, "No");
     dialog.addControl();
     dialog.show();
  }


/************************ mDialog *******************************
 
 The following is a modal dialog. 
 Argument:
   title:  a string or null
   obj:	   an Object. Eg: a scale panel, a combox, 
	an array of checkbox, or any Object
 Return:
   same as JOptionPane.showOptionDialog return
 
 Example:   
   scalePanel sp = new scalePanel("Enter Range", 0, 20, 0, 40);
   if (simpleDialog.mDialog("Zoom Range", sp) != JOptionPane.YES_OPTION) return;
   else rangeMax = sp.getValue();
*/

   public static int mDialog(String title, Object obj) {
      return JOptionPane.showOptionDialog( null,
		    obj,   
		    title, 
		    JOptionPane.OK_CANCEL_OPTION, 
		    JOptionPane.INFORMATION_MESSAGE,
		    null,                          
		    null,
		    null
		); 
   }

   public class basicDialog extends simpleDialog implements ActionListener{
      int idx=0;

      public basicDialog(boolean mode, Object [] obj) {
         setModal(mode);
         setUndecorated(true);
         for (int i=0; i<obj.length; i++) {
            addObj(obj[i]);	 
            ((JButton)obj[i]).addActionListener(this);
	    ((JButton)obj[i]).setActionCommand("OBJECT"+i);
         }
         show();
      }

      public void actionPerformed(ActionEvent evt) {
         String action = evt.getActionCommand();
         if (action.indexOf("OBJECT") >=0) { 
            idx = Integer.parseInt(action.substring(6));
         }
         hide();
      }

      public int getIdx() { return idx;}
   }
}
