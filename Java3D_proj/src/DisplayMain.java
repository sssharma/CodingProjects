
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.awt.image.*;

import javax.swing.*;
import javax.imageio.*;

import javax.media.j3d.*;
import javax.vecmath.*;




import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.geometry.Box;

public class DisplayMain extends JApplet implements   MouseListener, KeyListener {
	private boolean		DEBUG = false;
	
	Canvas3D		canvas;
	Scene		        scene;
	SimpleUniverse	        universe;
	BranchGroup		root ;
	BranchGroup		sceneRoot;
	TransformGroup		sceneGroup;
	Transform3D		origXform;
	
	DirectionalLight	headLight;
	SpotLight	        spotLight;
	AmbientLight	        ambLight;
        boolean                 spotLightOn, headLightOn, ambLightOn,sliderOn;
	int			width=1000, height=600;
	double			radius;
	javax.swing.Timer	timer;
	Vector3f                currPos = new Vector3f();
	float			rotAngle;
        Appearance              dotApp;
	Point3d                 center;
	TransformGroup	        vpTransGroup;
	Switch			backgroundSW;
        //BranchGroup             background;
	TransformGroup          dotGroup;
	float                   objDistance=-150;
	float			viewDistance = 2.8f;

        DisplayDialog           displayDialog;
	boolean			wrapTexture;
        int	                animationType;
	int                     animationSpeed=50;
	String                  objFile, dataDir="../data/";
        ObjectDisplay           objectDisplay;

        public DisplayMain () {
           super();
        }


    	public static void main(String[] args) {
    		
    		DisplayMain applet = new DisplayMain ();
    		String objfile =applet.dataDir +"head.obj";
    		System.out.println(" rags="+ args);
    		objfile=applet.dataDir+"Raptor.obj" ;
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		//applet.width = dim.width;
    		//applet.height = dim.height;
    		int x = (dim.width - applet.width)/2;
    		int y = (dim.height - applet.height)/2;
    		applet.init(objfile);
    		JFrame f = new JFrame();
                    f.setBounds(x,y,applet.width, applet.height);
                    f.getContentPane().add(applet);
                    f.setVisible(true);
    		applet.start();
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    	}
    	
	//default launch point for applets
	public void init(String filename) {
	        try {
	                objFile="pyramid.obj";
			GraphicsConfiguration config=
				SimpleUniverse.getPreferredConfiguration();
			canvas = new Canvas3D(config);
			
			Container contentPane = this.getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(canvas, BorderLayout.CENTER);
			canvas.addMouseListener(this);
                        canvas.addKeyListener ( this ) ; 

			ActionListener taskPerformer = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					animateHandle();
				}
			};
			timer = new javax.swing.Timer(5, taskPerformer);
			timer.setCoalesce(true);
                        setScene();
			updateDisplay(animationType, animationSpeed, wrapTexture, objFile);
			BoundingSphere behaviorBounds = new BoundingSphere(new Point3d(),
			Double.MAX_VALUE);
/*
			MouseRotate mr = new MouseRotate();
		        mr.setTransformGroup(objectDisplay.objGroup);
		        mr.setSchedulingBounds(behaviorBounds);
		        sceneRoot.addChild(mr);
		
		        MouseTranslate mt = new MouseTranslate();
		        mt.setTransformGroup(objectDisplay.objGroup);
		        mt.setSchedulingBounds(behaviorBounds);
		        //objectDisplay.topGroup.addChild(mt);
		
		        MouseZoom mz = new MouseZoom();
		        mz.setTransformGroup(objectDisplay.objGroup);
		        mz.setSchedulingBounds(behaviorBounds);
	       	        //objectDisplay.topGroup.addChild(mz);
*/

		        setViewpoint();
		        setupLighting(); 
			
			
		} catch (Exception e) {
			System.out.println(" viewer startup exception ");
			e.printStackTrace();
		}
	}

	private void setScene() {
		universe = new SimpleUniverse(canvas);
		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		
		root = new BranchGroup();
		root.setCapability(Group.ALLOW_CHILDREN_READ);
		root.setCapability(Group.ALLOW_CHILDREN_WRITE);
		root.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_DETACH);
		root.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
		
		sceneRoot = new BranchGroup();
		sceneRoot.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		sceneRoot.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		sceneRoot.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		sceneRoot.setCapability(BranchGroup.ALLOW_DETACH);
		sceneRoot.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		sceneRoot.setCapability(BranchGroup.ALLOW_BOUNDS_READ);

		sceneGroup =  new TransformGroup();
		sceneGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
                sceneGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                sceneGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
                sceneGroup.setCapability(Group.ALLOW_CHILDREN_READ);
                sceneGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
                sceneGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		sceneGroup.addChild(sceneRoot);		


	
	
        /*
		TextureLoader loader = new TextureLoader(dataDir+"dot2.gif",
                   // "LUMINANCE", new Container());
		   "INTENSITY", new Container());
                Texture texture = loader.getTexture();
                texture.setBoundaryModeS(Texture.WRAP      );
                texture.setBoundaryModeT(Texture.WRAP);
   
                TextureAttributes texAttr = new TextureAttributes();
                texAttr.setTextureMode(TextureAttributes.MODULATE);
		//texAttr.setBoundaryModeS(Texture.WRAP);
		//texAttr.setBoundaryModeT(Texture.WRAP);
                dotApp = new Appearance();
		 TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
                                                        TexCoordGeneration.TEXTURE_COORDINATE_2);
                dotApp.setTexCoordGeneration(tcg);
                dotApp.setTexture(texture);
                dotApp.setTextureAttributes(texAttr);
               // dotApp.setMaterial(new Material());

                PolygonAttributes pa = new PolygonAttributes();
                //pa.setPolygonMode(pa.POLYGON_LINE);
                pa.setCullFace(pa.CULL_BACK);
                dotApp.setPolygonAttributes(pa);

                int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
                Sphere sphere = new Sphere(0.2f, primflags, dotApp);  
                //sceneGroup.addChild(sphere);	

	        root.addChild(sceneGroup);
		universe.addBranchGraph(root);
                createDots();
                */


	}
	
	//used by setscene
	private void setupLighting() {
		BranchGroup lightBG = new BranchGroup();
		BoundingSphere lightBounds =
			new BoundingSphere(new Point3d(), Double.MAX_VALUE);
		headLight = new DirectionalLight(new Color3f(0.8f,0.8f,0.8f),
				new Vector3f(0,0,-1));
		headLight.setCapability(Light.ALLOW_STATE_READ);
		headLight.setCapability(Light.ALLOW_STATE_WRITE);
		headLight.setInfluencingBounds(lightBounds);
		spotLight = new SpotLight();
		//spotLight.setCapability(SpotLight.ALLOW_SPREAD_ANGLE_WRITE);
		spotLight.setCapability(Light.ALLOW_STATE_READ);
		spotLight.setCapability(Light.ALLOW_STATE_WRITE);
		ambLight = new AmbientLight(true, new Color3f(1.0f,1.0f,1.0f));
		ambLight.setCapability(Light.ALLOW_STATE_READ);
		ambLight.setCapability(Light.ALLOW_STATE_WRITE);
		ambLight.setInfluencingBounds(lightBounds);
		spotLight.setEnable(false);
		headLight.setEnable(true);
		ambLight.setEnable(false);
		
		lightBG.addChild(headLight);
		lightBG.addChild(spotLight);
		lightBG.addChild(ambLight);
		root.addChild(lightBG);
	}


	//used by setscene
	public void setViewpoint() {
		
		Transform3D viewTrans = new Transform3D();
		Transform3D eyeTrans = new Transform3D();
		
//		put the View at the standard VRML default position 0,0,10
		BoundingSphere sceneBounds = (BoundingSphere) sceneRoot.getBounds();
		//BoundingSphere sceneBounds = (BoundingSphere) root.getBounds();
		radius = sceneBounds.getRadius();
		center = new Point3d();
		sceneBounds.getCenter(center);
		Vector3d temp = new Vector3d(center);
	        System.out.println(" center x=" + temp.x + " y=" + temp.y +
				" z=" + temp.z + " r=" + radius);
		
		Viewer viewer = universe.getViewer();
		View view = viewer.getView();
		view.setDepthBufferFreezeTransparent(false);
		view.setFrontClipDistance(0.1);
		view.setBackClipDistance(1000);
		//float z = (float) (temp.z + 2.8*radius);
        	float z = (float) (temp.z + viewDistance*radius);
		Vector3f pos = new Vector3f((float)temp.x,(float)temp.y,z);
		eyeTrans.set(pos);
		viewTrans.mul(eyeTrans);
		
//		set the view transform
		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		vpTransGroup = viewingPlatform.getViewPlatformTransform();
		vpTransGroup.setTransform(viewTrans);
	}




	public void mouseEntered( MouseEvent e ) { }
	public void mouseExited(  
			MouseEvent e ) { }
	public void mouseClicked( MouseEvent e ) { }
	public void mouseReleased( MouseEvent e ) {//timer.start(); 
	}
	public void mousePressed( MouseEvent e ) {//timer.stop(); 
	}
	
	public void start() {
		width = getSize().width;
		height = getSize().height;
	}
	
	//used to signal applet ending
	public void stop() {
		super.stop();
	}
	
	//used to destroy the applet
	public void destroy() {
		universe.cleanup();
	}

	public void animateHandle() {
		/* translate
		currPos.x = (float) (currPos.x + 15.0/width);
		if (currPos.x > 1) currPos.x = (float)(-1.0);
		Transform3D tf = new Transform3D();
		tf.rotX(-80*Math.PI/180);
		tf.setTranslation(new Vector3f(currPos.x, 0,0));
		examineGroup.setTransform(tf);
		*/


		// rotation
            if (animationType == 0) {
		rotAngle = 2;
		if (rotAngle > 360) rotAngle = rotAngle-360;

	        Transform3D tmp = new Transform3D();
		objectDisplay.objGroup.getTransform(tmp);
		if (objFile.endsWith("head.obj"))
		   tmp.rotZ(rotAngle*Math.PI/180);
		else
		   tmp.rotY(rotAngle*Math.PI/180);
		objectDisplay.objXform.mul(tmp);
		objectDisplay.objGroup.setTransform(objectDisplay.objXform);
            }
	    else {
		//camera moving
		currPos.x = (float)(currPos.x + radius/width);
		//currPos.x = (float)(currPos.x + 5);
		if (currPos.x > radius*1.5) currPos.x =(float) -radius*1.5f;
		Transform3D viewTrans = new Transform3D();
		viewTrans.set(new Vector3f(currPos.x, (float)center.y, (float)(center.z+viewDistance*radius) ));
	
		 Transform3D tmp = new Transform3D();
		 tmp.set(new Vector3f(currPos.x, 0, 0)); // keep background still
		 //dotGroup.setTransform(tmp);
		 vpTransGroup.setTransform(viewTrans);
           }
	}

	public void createDots() {
	    float xv = 350, yv=300, zv=objDistance;
            double[] ptArray={xv, yv, zv,
		    -xv, yv, zv,
		    -xv, -yv, zv,
		    xv, -yv, zv};
            float s=32;
            TexCoord2f[] texpt=new TexCoord2f[4];
	    texpt[0]=new TexCoord2f(0.0f,0.0f);
	    texpt[1]=new TexCoord2f(s,0.0f);
    	    texpt[2]=new TexCoord2f(s,s);
    	    texpt[3]=new TexCoord2f(0.0f,s);
		
            GeometryInfo gi= new GeometryInfo(GeometryInfo.QUAD_ARRAY);
            gi.setCoordinates(ptArray);
           // gi.setTextureCoordinateParams(1, 2);
           // gi.setTextureCoordinates(0, texpt);
 

           NormalGenerator normalGenerator = new NormalGenerator();
           normalGenerator.generateNormals(gi);
 
           GeometryArray myQuadArray = gi.getGeometryArray();
           Shape3D shape = new Shape3D(myQuadArray, dotApp);
       
          
	   BranchGroup background = new BranchGroup();
	   background.setCapability(Group.ALLOW_CHILDREN_READ);
	   background.setCapability(Group.ALLOW_CHILDREN_WRITE);
	   background.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	   background.setCapability(BranchGroup.ALLOW_DETACH);
	   background.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
		
           dotGroup = new TransformGroup();
	   dotGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	   dotGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	   dotGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	   dotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	   dotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	   dotGroup.addChild(shape);

	   backgroundSW= new Switch();
	   backgroundSW.setCapability(Switch.ALLOW_SWITCH_READ);
           backgroundSW.setCapability(Switch.ALLOW_SWITCH_WRITE);
           backgroundSW.setCapability(Group.ALLOW_CHILDREN_READ);
           backgroundSW.setCapability(Group.ALLOW_CHILDREN_WRITE);
           backgroundSW.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	   backgroundSW.addChild(dotGroup);
           background.addChild(backgroundSW);
	   root.addChild(background);
           if (wrapTexture) backgroundSW.setWhichChild(0);
	   else backgroundSW.setWhichChild(Switch.CHILD_NONE);

	}
 
  public void keyTyped ( KeyEvent e ){ 
   }  

  public void keyPressed ( KeyEvent e){ } 
  public void keyReleased ( KeyEvent e ){    

      char c = e.getKeyChar();
      if( c=='a' || c == 'A') timer.start(); 
      else if( c == 's' || c == 'S') timer.stop(); 
      else if(c == 'm' || c == 'm') {
	   if (displayDialog == null) displayDialog = new DisplayDialog(this);
	   displayDialog.setVisible(true);
      }
  }  


  public void  updateDisplay(int animationType, int animationSpeed, boolean wrapTexture, String filename) {
      canvas.stopRenderer();
      if (!filename.startsWith(dataDir)) filename = dataDir+ filename;
      this.animationType = animationType;
      this.wrapTexture = wrapTexture;
      this.objFile = filename;
      timer.setDelay(animationSpeed);
      if (wrapTexture) backgroundSW.setWhichChild(0);
      else backgroundSW.setWhichChild(Switch.CHILD_NONE);

      sceneRoot.detach();
      int cnt = sceneRoot.numChildren();
      System.out.println(" scenrot num="+ cnt);
      if (cnt > 0) sceneRoot.removeChild(0); 

      objectDisplay = new ObjectDisplay();
      objectDisplay.loadData(filename);
      sceneRoot.addChild(objectDisplay.sw); 

      sceneGroup.addChild(sceneRoot);
      setViewpoint();
      canvas.startRenderer();

      BoundingSphere sceneBounds = (BoundingSphere) sceneRoot.getBounds();
      radius = sceneBounds.getRadius();
      //System.out.println(" update obj="+ filename + " radius="+ radius);
  }

  class ObjectDisplay {
      Switch			sw;
      BranchGroup		topGroup;
      TransformGroup		objGroup;
      Transform3D		origXform;
      Transform3D		objXform;
      Scene		        objScene;			//loaded obj 

      String [] labelObjects= {
	      "Raptor.obj",
	      "Taki.obj",
	      "AlienClassic.obj",
	      "needle01.obj",
	      "head.obj",
	      "multi objs"
       };

      public ObjectDisplay() {
	 sw = new Switch();
	 sw.setCapability(Switch.ALLOW_SWITCH_READ);
         sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
         sw.setCapability(Group.ALLOW_CHILDREN_READ);
         sw.setCapability(Group.ALLOW_CHILDREN_WRITE);
         sw.setCapability(Group.ALLOW_CHILDREN_EXTEND);

         topGroup = new BranchGroup();
	 topGroup.setCapability(BranchGroup.ALLOW_DETACH);
	 topGroup.setCapability(Group.ALLOW_COLLISION_BOUNDS_READ);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_READ);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

         objGroup = new TransformGroup();
	 objGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	 objGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	 objGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	 objGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
	 objGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
      }
     

      public void loadData(String filename) {
	if (!filename.endsWith(".obj")) {
		createMultiObjects();
		return;
	}
	/*
	try {
	  if (!wrapTexture) {
	     int flags = MyObjectFile.RESIZE;
	     MyObjectFile f = new MyObjectFile(flags, (float)0.0);
	     objScene = f.load(filename);
	  }
	  else {   //with dot
	     int flags = ObjectFile.RESIZE;
	     ObjectFile f = new ObjectFile(flags, (float)0.0);
	     objScene = f.load(filename);
	  }
	} catch(Exception e) {
          e.printStackTrace();
	}
	  */
	  objScene = loadObject(filename);
	  objGroup.addChild(objScene.getSceneGroup());
	  //if (filename.endsWith("pyramid.obj")) setPos(0,0,objDistance*1.5f);
	  setPos(0,0,objDistance*0.95f);
	  if (filename.endsWith("head.obj")) setRotX(-80);
	  topGroup.addChild(objGroup);
	  sw.addChild(topGroup);
	  sw.setWhichChild(0);
	 
      }
      public void setScale(float x, float y, float z) {
	  objXform = new Transform3D();
	  objGroup.getTransform(objXform);
          Transform3D  tmp= new Transform3D();
	  tmp.setScale(new Vector3d(0.1f, 0.1f, 0.1f));
	  objXform.mul(tmp);
	  objGroup.setTransform(objXform);
	  System.out.println("   setscale---------");
      }
      public void setPos(float x, float y, float z) {
	  objXform = new Transform3D();
	  objGroup.getTransform(objXform);
          Transform3D  tmp= new Transform3D();
	  tmp.set(new Vector3f(x, y, z));
	  objXform.mul(tmp);
	  objGroup.setTransform(objXform);
      }

      public Scene loadObject(String filename) {
	      System.out.println("=====loadobject filename="+ filename);
	try {
	  if (!wrapTexture) {
	     int flags = MyObjectFile.RESIZE;
	     MyObjectFile f = new MyObjectFile(flags, (float)0.0 );
	     f.setAppearance(dotApp);
	     objScene = f.load(filename);
	  }
	  else {   //with dot
	     int flags = ObjectFile.RESIZE;
	     ObjectFile f = new ObjectFile(flags, (float)0.0 );
	     f.setAppearance(dotApp);
	     objScene = f.load(filename);
	  }
	  return objScene;
	} catch(Exception e) {
          e.printStackTrace();
	  return null;
	}
      }

      public void setRotX(float degree) {
	  objXform = new Transform3D();
	  objGroup.getTransform(objXform);
          Transform3D  tmp= new Transform3D();
	  tmp.rotX(degree*Math.PI/180);
	  objXform.mul(tmp);
	  objGroup.setTransform(objXform);
      }

      public void createMultiObjects() {
	    int n=2;
	    Scene [] scenes = new Scene[n];
	    TransformGroup [] tgs = new TransformGroup[n];
	    Transform3D [] tfs = new Transform3D[n];
	    String [] filenames = {"Raptor.obj", "pyramid.obj"};
              
	    for (int i=0; i<n; i++) {
	        tgs[i] = new TransformGroup();
	        tfs[i] = new Transform3D();
	        tgs[i].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	        tgs[i].setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	        tgs[i].setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	        tgs[i].setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
	        tgs[i].setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		   scenes[i] = loadObject("data/"+filenames[i]);
		   tgs[i].addChild(scenes[i].getSceneGroup());
	        objGroup.addChild(tgs[i]);
	    }
	    tfs[0].set(new Vector3f(-1.0f, 0, objDistance*0.95f));
	    tfs[1].set(new Vector3f(0.2f, 0, objDistance*0.95f));
	    tfs[1].setScale(0.5f);
	    for (int i=0; i<n; i++) 
		    tgs[i].setTransform(tfs[i]);
	  topGroup.addChild(objGroup);
	  sw.addChild(topGroup);
	  sw.setWhichChild(0);
      }

  }

}
