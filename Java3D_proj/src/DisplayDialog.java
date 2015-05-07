import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

public class DisplayDialog extends simpleDialog {
   DisplayMain app;

   groupPanel animationTypeGP;
   scalePanel animationSP;
   groupPanel objectTextureGP;
   comboPanel objectCB;

   public DisplayDialog(DisplayMain app) {
      super("Display Selection");
      this.app = app;

        String [] labelAnimationType= {
	"Object Rotate",
	"Camera Moving"
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

      String [] labelObjects= {
	      "Raptor.obj",
	      "boat.obj",
	      //"Taki.obj",
	      "AlienClassic.obj",
	      "needle01.obj",
	      "pyramid.obj",
	      "head.obj",
	      "multi objs",
	      "ben.obj",
	      "tour.obj"
       };
       objectCB = new comboPanel("Select Display Object", 1, labelObjects);
       objectCB.getCombo().setSelectedIndex(0);
       addObj(objectCB);

       addControl();
       setVisible(true);
       show();
   }
   public void okCallback() {
	setVisible(false);

	boolean wrapTexture = false;
	if ( objectTextureGP.getRadioValue() == 1) wrapTexture = true;
	app.updateDisplay(animationTypeGP.getRadioValue(), (int)(animationSP.getValue()), wrapTexture,(String) (objectCB.getCombo().getSelectedItem()));

   }
    public void cancelCallback() {
	setVisible(false);
   }
}

