
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;



public class groupPanel extends JPanel implements ActionListener{

    int count;
    public JLabel [] jlabel;
    public JRadioButton [] jradio;
    public JCheckBox    [] jcheckbox;
    JComponent   [] jcomp;
    ButtonGroup	    group;
    int		    type;
    int 	    radioValue;
    boolean	 [] checkboxValue;

    public final static int LABEL=0;
    public final static int RADIO=1;
    public final static int CHECKBOX=2;
    public final static int ANY=3;
    public final static int ANYFLOW=4;

    public groupPanel (Object myTitle,  JComponent obj) {
            if (myTitle != null) 
               setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder((String)myTitle),
                        BorderFactory.createEmptyBorder(0,2,0,2)));
	    add(obj);
    }
    public Object [] getEle() {
            switch (type) {
	    case RADIO:
                 return jradio;
	    case CHECKBOX:
                 return jcheckbox;
            default:
                 return jcomp;
            }
    }
      
    // type 1=radio button, 2=checkbox, 3=jcomponets
    public groupPanel (Object myTitle, int numCol, Object [] obj, int type) {
	String str;
        this.type = type;
	try {
            if (myTitle != null) 
               setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder((String)myTitle),
                        BorderFactory.createEmptyBorder(0,2,0,2)));

            count = obj.length;
            int numRow = (int)Math.ceil(count*1.0/numCol); 
            
            if (type != ANYFLOW) setLayout(new GridLayout(numRow, numCol));
            else setLayout(new FlowLayout());

            switch (type) {
	    case LABEL:
		jlabel = new JLabel[count];
		break;
	    case RADIO:
		jradio = new JRadioButton[count];
	        group = new ButtonGroup();	
		break;
	    case CHECKBOX:
		jcheckbox = new JCheckBox[count];
                checkboxValue = new boolean[count];
		break;
            default:
		jcomp = new JComponent[count];
            }
            for (int i=0; i<count; i++) {
               if (type == LABEL) {
		  jlabel[i] = new JLabel((String)obj[i]);
		  add(jlabel[i]);
               }
               else if (type == RADIO) {
                  if (obj[i] instanceof String)
		     jradio[i] = new JRadioButton((String)obj[i]);
                  else {
		     jradio[i] = (JRadioButton)obj[i];
                  }
		  jradio[i].addActionListener(this);
		  group.add(jradio[i]);
		  add(jradio[i]);
               }
               else if (type == CHECKBOX) {
		  jcheckbox[i] = new JCheckBox((String)obj[i]);
		  jcheckbox[i].addActionListener(this);
		  add(jcheckbox[i]);
               }
               else if (type == 3 || type == 4) {
                  jcomp[i] = (JComponent)obj[i];
		  add(jcomp[i]);
               }
            }

    	} catch (Exception ex) {
	    String msg = ex.getMessage();
	    System.out.println("scalePanel0...err = "+msg);
    	}
    }

    public groupPanel ( JComponent c1, JComponent c2) {
       this(null, c1,c2);
    }
    public groupPanel (Object myTitle, JComponent c1, JComponent c2) {
            if (myTitle != null) 
               setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder((String)myTitle),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
            setLayout(new GridLayout(1, 2));
            add(c1);
	    add(c2);
    }

    public boolean [] getCheckboxValue() { 
       return checkboxValue;
    }

    public int getRadioValue() { return radioValue;}

    public void setValue(int idx) { 
      try {
	switch (type) {
	  case RADIO:
            for (int i=0; i<count; i++) 
             if (i == idx) jradio[i].setSelected(true);
             else jradio[i].setSelected(false);
	    radioValue = idx;
	    break;
	  case CHECKBOX:
	    checkboxValue[idx] = true;
	    break;
	}
      } catch (Exception e) {
	e.printStackTrace();
      }
    }

    public void setValue(boolean [] value) { 
       for (int i=0; i<count; i++)  {
          jcheckbox[i].setSelected(value[i]);
	  checkboxValue[i] = value[i];
       }
    }

    public void selectCallback() {

    }

    public void actionPerformed(ActionEvent evt) {
       try {
          switch (type) {
          case RADIO:
            for (int i=0; i<count; i++) 
	       if (jradio[i].isSelected()) {
		 radioValue = i;
		 break;
	       };
            selectCallback();
            break;
          case CHECKBOX:
            for (int i=0; i<count; i++) 
		 checkboxValue[i] = jcheckbox[i].isSelected();
            selectCallback();
            break;
          }
       }catch(Exception e) {
	  e.printStackTrace();
       }
   }


}
