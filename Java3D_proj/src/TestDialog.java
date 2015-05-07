import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

public class TestDialog extends simpleDialog {
   Mytest app;

   groupPanel animationTypeGP;
   groupPanel shapeGP;
   scalePanel animationSP;
   groupPanel objectTextureGP;
   comboPanel objectCB, shapeCB;

   static String [] labelObjects= {
	      "Raptor.obj",
	      //"Taki.obj",
	      "AlienClassic.obj",
	      "needle01.obj",
	      "box.obj",
	      "multi objs",
	      "ben.obj",
	      "tour.obj",
	      "TryTry.obj"
       };
   public TestDialog(Mytest app) {
      super("Display Selection");
      this.app = app;

        String [] labelAnimationType= {
	"Object Rotate",
	"Camera Moving",
	"Camera Moving and ObjectRotation"
      };
      animationTypeGP = new groupPanel("Animation Type", 1, labelAnimationType, groupPanel.RADIO);
      animationTypeGP.setValue(0);
      addObj(animationTypeGP);

      animationSP = new scalePanel("Animation Speed", 0, 50, 5, 200);
      addObj(animationSP);

      String [] labelTexture= {
	"None",
	"Random Dot"
      };
      objectTextureGP = new groupPanel("Texture", 1, labelTexture, groupPanel.RADIO);
      objectTextureGP.setValue(0);
      addObj(objectTextureGP);

      objectCB = new comboPanel("Select Display Object", 1, labelObjects);
      objectCB.getCombo().setSelectedIndex(0);
      addObj(objectCB);

      /*
      String [] labelShape= {
	"Geometry Shape",
	"Objects"
      };
      shapeGP = new groupPanel("Shape type", 1, labelShape, groupPanel.RADIO);
      shapeGP.setValue(app.shapeType);
      addObj(shapeGP);
      JRadioButton [] jary = (JRadioButton [])shapeGP.getEle();
        for (int i=0; i<jary.length; i++) {
           jary[i].addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent ev) {
                  setObjectList();
               }
           });
        } 

     

       String [] labelShapes= {
	      "Box",
	      "Cylinder",
	      "Cone",
	      "Sphere"
       };
       shapeCB = new comboPanel("Select Shapes", 1, labelShapes);
       shapeCB.getCombo().setSelectedItem(app.shapeName);
       addObj(shapeCB);

       if (app.shapeType == 0) objectCB.setVisible(false);
       else shapeCB.setVisible(false);
       */

       addControl();
       setVisible(true);
       show();
   }
   public void setObjectList() {
       int opt = shapeGP.getRadioValue();

       if (opt == 1) {
	       objectCB.setVisible(false);
	       shapeCB.setVisible(true);
       }
       else {
	       shapeCB.setVisible(false);
	       objectCB.setVisible(true);
       }
   }

   public void okCallback() {
	setVisible(false);

	boolean wrapTexture = false;
	if ( objectTextureGP.getRadioValue() == 1) wrapTexture = true;
	//app.updateDisplay(animationTypeGP.getRadioValue(), (int)(animationSP.getValue()), wrapTexture,(String) (objectCB.getCombo().getSelectedItem()),shapeGP.getRadioValue(), (String)shapeCB.getCombo().getSelectedItem() );
	app.updateDisplay(animationTypeGP.getRadioValue(), (int)(animationSP.getValue()), wrapTexture,(String) (objectCB.getCombo().getSelectedItem()), 1, null );

   }
    public void cancelCallback() {
	setVisible(false);
   }
}

