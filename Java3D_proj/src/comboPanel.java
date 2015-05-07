
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


// It can be constructed as month panel or general combo pull down 
// The examples are in simpleDialog main: month and cb object.

// for month panel: 
// Creates a panel to allow selection of month value (1-12)
//  inputs are panel title string and month value
//  title string can be "" and month value is expected to be int (1-12)
//  callable rtns are:
//    getValue() = returns month as an int value (1-12)
//    setValue(month value) = month value is expected to be int (1-12)

public class comboPanel extends JPanel implements ActionListener{

    private JComboBox comboPulldown;
    private int idx;
    final static boolean COLORS = false;

    private final int MONTH=0;
    private final int OTHERS=1;
    private int type=MONTH;
    private JLabel  title;

    public static String [] monthLabel = {
	    "January",
	    "February",
	    "March",
	    "April",
	    "May",
	    "June",
	    "July",
	    "August",
	    "September",
	    "October",
	    "November",
	    "December"
    };

    String [] labels;

    public static String  getMonth(int idx) {	return monthLabel[idx-1];}
    public JComboBox getCombo() {	return comboPulldown;}

    public int getValue() {	return idx;	}

    public void setValue(int valin) {
	idx = valin;
        if (type == MONTH) {
	  if (idx < 1) idx= 1;
	  if (idx> 12) idx= 12;
	  comboPulldown.setSelectedIndex(idx-1);
        }
        else 
	  comboPulldown.setSelectedIndex(idx);
    }

    public void setTitle(String str) {
       title.setText(str);
    }
    public void setValue(String str) {
      String [] ary;
      if (type == MONTH) ary = monthLabel;
      else ary = labels; 
      if (ary == null) return;
      //System.out.println("combopanel setvalue str=" + str + " eles=" + ary.length);
      for (int i=0; i<ary.length; i++) {
        //if (i<12 || ary[i].equalsIgnoreCase("Sansserif")) 
	   //System.out.println("i=" + i + " aryv=" + ary[i]);
	if (str.equalsIgnoreCase(ary[i]))  {
	   //System.out.println("found match idx=" + i);
           comboPulldown.setSelectedIndex(i);
           break;
        }
      }
      
    }

    public comboPanel (Vector v) {
	    comboPulldown = new JComboBox(v);
	    comboPulldown.addActionListener(this);
	    add(comboPulldown);
    }


    // general combo pulldown: labels contain the items in the pulldown
    public comboPanel (String myTitle, int idx, String [] labels) {
	String str;
	int i;

        this.labels = labels;
	try {
	    if (COLORS) setBackground(Color.cyan);

/*
            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(myTitle),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
*/
            setBorder( BorderFactory.createEmptyBorder(2,5,2,5));



            if (labels != null && labels.length >0 && labels[0].equals(monthLabel[0]) ) {
		type = MONTH; 
	        if (idx < 0) idx = 0;
	        if (idx > 11) idx = 11;
            }
	    else type = OTHERS;
            this.idx= idx;

	    comboPulldown = new JComboBox();
            if (labels != null && labels.length>0) {
               for (i=0; i<labels.length; i++) 
	          comboPulldown.addItem(labels[i]);
	       comboPulldown.setSelectedIndex(idx);
            }

	    comboPulldown.addActionListener(this);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            if (myTitle != null) {
               title = new JLabel(myTitle + "  ");
	       add(title);
            }
	    add(comboPulldown);

    	} catch (Exception ex) {
	    ex.printStackTrace();
    	}
    }

    public void updateContent(Object  [] obj) {
       int n = comboPulldown.getItemCount();
       for (int i=n-1; i>=0; i--) 
       comboPulldown.removeItemAt(i);
       for (int i=0; i<obj.length; i++) 
	       comboPulldown.addItem(obj[i]);
       comboPulldown.setSelectedIndex(0);
    }

    public void updateStringContent(String  [] obj) {
       comboPulldown.removeAllItems();
       for (int i=0; i<obj.length; i++) 
	       comboPulldown.addItem(obj[i]);
       comboPulldown.setSelectedIndex(0);
    }

    public void actionPerformed(ActionEvent e) {
       changeSelection();
    }

    public Dimension getMaximumSize() {
                    return getPreferredSize();
    }

    public Dimension getMinimumSize() {
                    return getPreferredSize();
    }

    public comboPanel (String myTitle, int noMonthIn)
    {
      this(myTitle, Math.max(0, noMonthIn-1), monthLabel);
    }

    public void changeSelection() {
	try {
	    if (type == MONTH) {
	       int mn;
               mn = comboPulldown.getSelectedIndex() + 1;
	       if (mn > 0) idx = mn;
            }
	    else  idx = comboPulldown.getSelectedIndex() ;
	} catch (Exception ex) {
	   ex.printStackTrace();
	}
    }

    public JComboBox getComboBox() { return comboPulldown;}

}
