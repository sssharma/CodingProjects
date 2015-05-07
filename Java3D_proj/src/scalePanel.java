import java.io.*;
import java.lang.*;
import java.beans.*;
import java.text.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.Cursor.*;
import java.awt.Toolkit.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.sound.sampled.*;

// Creates a panel to allow a numeric value to be edited via typed text or
// slider bar.
//  inputs are:
//    myTitle = panel title string (can be null or "")
//    noFfracts = (int) number of digits to the right of decimal pt. (0-??)
//    xval = (float) current data value
//    xmn = (float) minimum allowable value for xval
//    xmx = (float) maximum allowable value for xval
//  callable rtns are:
//    getValue() = returns current xval value
//    setValue() = sets current xval value (forcing within legal ranges)

public class scalePanel extends JPanel {
    JSlider slider;
    JTextField textField;
    JLabel    mxLabel;
    final static boolean DEBUG = false;
    final static boolean COLORS = false;
    private NumberFormat nf;
    private float mult;
    private int mn, mx, val;
    private float fval, minVal, maxVal;

    public JSlider getSlider() {return slider;	}
    public float getValue() {	return fval;	}
    public void setValue(float valin) {
	fval = valin;
	if (fval < minVal) fval = minVal;
	if (fval > maxVal) fval = maxVal;
	textField.setText(nf.format(fval));
	int ival = (int) (fval * mult);
	slider.setValue(ival);
    }
    public void setMaxValue(float valin) {
       maxVal = valin;
       mx = (int)(maxVal*mult);
       slider.setMaximum(mx);
       mxLabel.setText(nf.format(maxVal));
    }
    public void setMinValue(float valin) {
       minVal = valin;
       mn = (int)(minVal*mult);
       slider.setMinimum(mn);
       //mnLabel.setText(nf.format(minVal));
    }
    public JTextField getTextField() { return textField;}
    public scalePanel () {
       super();
    }
    public scalePanel (String myTitle, int nofracts,
	float xval, float xmn, float xmx)
    {
	String str;
	int i;

	try {

	    super.setFont(new Font("helvetica", Font.PLAIN, 24));

	    if (COLORS) setBackground(Color.cyan);

            setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(myTitle),
                        BorderFactory.createEmptyBorder(0,0,0,0)));

	    if (nofracts < 0) nofracts = 0;
	    nf = NumberFormat.getInstance();
	    nf.setMaximumFractionDigits(nofracts);

	    mult = 1;
	    if (nofracts > 0) {
		for (i=0 ; i<nofracts ; i++) mult = mult * 10.0f;
	    }

	    minVal = xmn;
	    maxVal = xmx;
	    fval = xval;
	    mn = (int) (xmn*mult);
	    mx = (int) (xmx*mult);
	    val = (int) (xval*mult);
	    str = nf.format(xval);
	    textField = new JTextField(str);
	    slider = new JSlider(mn, mx, val);

	    textField.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    changeTextField();
		}
	    });

	    slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    changeSlider();
		}
	    });

            textField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) { }
                public void focusLost(FocusEvent e) { lFocusText(e); }
            });

            //Make the textfield/slider group a fixed size.
            JPanel unitGroup = new JPanel() {
            	public Dimension getMinimumSize() {
                    return getPreferredSize();
             	}
            	public Dimension getPreferredSize() {
                    return new Dimension(150,
                                     super.getPreferredSize().height);
                }
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }
            };
            if (COLORS) unitGroup.setBackground(Color.blue);

	    str = nf.format(xmn);
	    JLabel mnLabel = new JLabel(str);
            mnLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	    str = nf.format(xmx);
	    mxLabel = new JLabel(str);
            mxLabel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

            unitGroup.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
            unitGroup.setLayout(new BoxLayout(unitGroup, BoxLayout.X_AXIS));
	    unitGroup.add(mnLabel);
            unitGroup.add(textField);
	    unitGroup.add(mxLabel);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(unitGroup);
	    add(slider);
            unitGroup.setAlignmentY(TOP_ALIGNMENT);

    	} catch (Exception ex) {
	    String msg = ex.getMessage();
	    System.out.println("scalePanel0...err = "+msg);
	    System.out.println("myTitle=" + myTitle + " min=" + xmn + " max=" + xmx + " value=" + xval);
    	}
    }
    public void setTitle(String myTitle) {
         setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(myTitle),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
    }

    private void changeTextField() {
	String str = textField.getText();
	try {
	    fval = (nf.parse(str)).floatValue();
	    if (fval < minVal) fval = minVal;
	    if (fval > maxVal) fval = maxVal;
 //System.out.println("----changeTextField");
	    //set slider value
	    int ival = (int) (fval * mult);
	    slider.setValue(ival);
	} catch (Exception ex) {
	    String msg = ex.getMessage();
	    System.out.println("scalePanel1...err = "+msg);
	}
    }

    private void changeSlider() {
	int ival = slider.getValue();
	try {
	    fval = ((float) ival) / mult;
	    if (fval < minVal) fval = minVal;
	    if (fval > maxVal) fval = maxVal;
 //System.out.println("----changeSlider");
	    //set textField value
	    String str = nf.format(fval);
	    textField.setText(str);
	} catch (Exception ex) {
	    String msg = ex.getMessage();
	    System.out.println("scalePanel2...err = "+msg);
	}
    }

    private void lFocusText(FocusEvent e) {
	if (!e.isTemporary()) changeTextField();
    }
}
