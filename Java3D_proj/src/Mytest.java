
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
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

public class Mytest extends JApplet implements   MouseListener, KeyListener {
	private boolean		DEBUG = false;
        private Robot           robot;	
	Canvas3D		canvas;
	Scene		        scene;
	SimpleUniverse	        universe;
	BranchGroup		root, sceneRoot, objRoot;
	TransformGroup		sceneGroup, examineGroup;
	Transform3D		origXform;
	
	DirectionalLight	headLight;
	SpotLight	        spotLight;
	AmbientLight	        ambLight;
        boolean                 spotLightOn, headLightOn, ambLightOn,sliderOn;
	double			radius;
	javax.swing.Timer	timer;
	Vector3f                currPos = new Vector3f();
	float			rotAngle;
        Appearance              dotApp, normApp;
	Point3d                 center;
	TransformGroup	        vpTransGroup;
	Switch			backgroundSW;
	TransformGroup          dotGroup;
	int			shapeType = 1;
	String			shapeName="cylinder";
        TestDialog              testDialog;
	boolean			wrapTexture=false;
        int	                animationType;
        ObjectDisplay           objectDisplay;
        boolean                 captureOn;
	BufferedImage []        imgs;
        Rectangle		rect;
	int                     imageCnt;
        float 			totalX;
	int                     animationSpeed=50;
	String                  objFile, dataDir="data/";
	float                   objDistance=-150f;
	float			viewDistance = 3.2f;
	int			width=1000, height=600;
	int                     totalCnt = 18;  // number of images to capture
	float                   rad = (float)Math.PI/180;

        public Mytest () {
           super();
        }

	public void init(String filename) {
	        try {
                        File f= new File ("./data");
		        if (f.exists()) dataDir = "data/";	
			else dataDir = "../data/";

                        f= new File (dataDir + "../images");
		        if (!f.exists()) f.mkdir();
			System.out.println("   -----datadir="+ dataDir);

			robot = new Robot();
                        imgs = new BufferedImage[totalCnt];
		
	                //objFile="multi objs";
	                objFile="Raptor.obj";
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

		examineGroup=  new TransformGroup();
		examineGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
                examineGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
                examineGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
                examineGroup.setCapability(Group.ALLOW_CHILDREN_READ);
                examineGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
                examineGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);

		objRoot = new BranchGroup();
		objRoot .setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		objRoot .setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		objRoot .setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objRoot .setCapability(BranchGroup.ALLOW_DETACH);
		objRoot .setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		objRoot .setCapability(BranchGroup.ALLOW_BOUNDS_READ);

	        examineGroup.addChild(objRoot);
	        sceneRoot.addChild(sceneGroup);
	        sceneGroup.addChild(examineGroup);


		BoundingSphere behaviorBounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
		MouseRotate mr = new MouseRotate();
	        mr.setTransformGroup(examineGroup);
	        mr.setSchedulingBounds(behaviorBounds);
	        sceneRoot.addChild(mr);
		
	        MouseTranslate mt = new MouseTranslate();
	        mt.setTransformGroup(examineGroup);
	        mt.setSchedulingBounds(behaviorBounds);
	        sceneRoot.addChild(mt);
		
	        MouseZoom mz = new MouseZoom();
	        mz.setTransformGroup(examineGroup);
	        mz.setSchedulingBounds(behaviorBounds);
       	        sceneRoot.addChild(mz);

		TextureLoader loader = new TextureLoader(dataDir+"dot2.gif",
		    new Container());
                   // "LUMINANCE", new Container());
		   //"INTENSITY", new Container());
		System.out.println("loader="+ loader);
                Texture texture = loader.getTexture();
		System.out.println(" texture="+ texture);
                texture.setBoundaryModeS(Texture.WRAP      );
                texture.setBoundaryModeT(Texture.WRAP);
   
                TextureAttributes texAttr = new TextureAttributes();
                texAttr.setTextureMode(TextureAttributes.MODULATE);
		//texAttr.setBoundaryModeS(Texture.WRAP);
		//texAttr.setBoundaryModeT(Texture.WRAP);
                dotApp = new Appearance();
		
                dotApp.setTexture(texture);
                dotApp.setTextureAttributes(texAttr);

                PolygonAttributes pa = new PolygonAttributes();
                //pa.setPolygonMode(pa.POLYGON_LINE);
                pa.setCullFace(pa.CULL_BACK);
                dotApp.setPolygonAttributes(pa);

	
		normApp = new Appearance();
		TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.EYE_LINEAR,
                                                        TexCoordGeneration.TEXTURE_COORDINATE_2);
                normApp.setTexCoordGeneration(tcg);
                normApp.setTexture(texture);
                normApp.setTextureAttributes(texAttr);
                normApp.setPolygonAttributes(pa);

                int primflags = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;
                Sphere sphere = new Sphere(1f, primflags, dotApp);  
                //Box  shape = new Box(1.0f, 1.0f, 4, Box.GENERATE_TEXTURE_COORDS, dotApp);
		//examineGroup.addChild(shape);
                

		root.addChild(sceneRoot);
		universe.addBranchGraph(root);

                createDots();
	        updateDisplay(animationType, animationSpeed, wrapTexture, objFile, shapeType,shapeName);
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
		headLight.setEnable(true);
		lightBG.addChild(headLight);
		/*
		spotLight = new SpotLight();
		spotLight.setCapability(Light.ALLOW_STATE_READ);
		spotLight.setCapability(Light.ALLOW_STATE_WRITE);
		ambLight = new AmbientLight(true, new Color3f(1.0f,1.0f,1.0f));
		ambLight.setCapability(Light.ALLOW_STATE_READ);
		ambLight.setCapability(Light.ALLOW_STATE_WRITE);
		ambLight.setInfluencingBounds(lightBounds);
		spotLight.setEnable(false);
		ambLight.setEnable(false);
		
		lightBG.addChild(spotLight);
		lightBG.addChild(ambLight);
		*/
		root.addChild(lightBG);
	}


	//used by setscene
	public void setViewpoint() {
	   String 	[][] adj = { // radius is related to viewing distance. 
		        {"AlienClassic.obj", "1.8f"},   //1.54f,  //AllenClassic.obj
			{"needle01.obj", "1.6f"},       //1.1767f, //needle01.obj
			{"box.obj","2.8f"},             //1.732f,  //box
			{"multi objs","2.8f"},          //multiobj: box and raptor
			{"ben.obj","1.6f"},          //multiobj: box and raptor
		};
		
		Transform3D viewTrans = new Transform3D();
		Transform3D eyeTrans = new Transform3D();
		
//		put the View at the standard VRML default position 0,0,10
		BoundingSphere sceneBounds = (BoundingSphere) sceneRoot.getBounds();
		//BoundingSphere sceneBounds = (BoundingSphere) root.getBounds();
		radius = sceneBounds.getRadius();
	        int idx=0;
	        for (idx=0; idx < adj.length; idx++) {
		   if (objFile.indexOf(adj[idx][0]) >=0) radius = Float.parseFloat(adj[idx][1]);
		}
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

	public static void main(String[] args) {
		
		Mytest applet = new Mytest ();
		String objfile =applet.dataDir +"head.obj";
		System.out.println(" rags="+ args);
		objfile=applet.dataDir+"multi obj" ;
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
		Point p = getLocation();
		SwingUtilities.convertPointToScreen(p,this);
		rect = new Rectangle(p.x, p.y, width, height);
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
		tf.rotX(-80*rad);
		tf.setTranslation(new Vector3f(currPos.x, 0,0));
		examineGroup.setTransform(tf);
		*/


		// rotation
            if (animationType == 0) {
		    /*
		rotAngle =  rotAnagle+2;
		if (rotAngle > 360) rotAngle = rotAngle-360;
	        Transform3D tf = new Transform3D();
		examineGroup.getTransform(tf);
	        Transform3D tf2 = new Transform3D();
		tf2.rotY(2.0*rad); //each time rotate 2 degree. 
		tf.mul(tf2);
		examineGroup.setTransform(tf);
		*/
		rotAngle =  (rotAngle+2)%360;
		objsRotation();
		if (captureOn && imageCnt < totalCnt && rotAngle%(360/totalCnt) == 0 ) {
	            grabCurrentImage(imageCnt);
		    imageCnt++;
		}
		else if (imageCnt >= totalCnt) {
			captureOn = false;
			imageCnt=0;
			System.out.println("------- capture image done-------");
		}
            }
	    else if (animationType == 1 || animationType == 2) {
		if (animationType == 2) objsRotation();  //individual object rotate around its own axis

		//camera moving
		currPos.x = (float)(currPos.x + radius*3.0/width);
		//currPos.x = (float)(currPos.x + 1);
		if (currPos.x > radius*1.5) currPos.x =(float) -radius*1.5f;
		Transform3D viewTrans = new Transform3D();
		//viewTrans.set(new Vector3f(currPos.x, (float)center.y, (float)(center.z+viewDistance*radius) ));
		viewTrans.setTranslation(new Vector3f((float)(radius*3.0f/width), 0,0));
	
		 Transform3D tf= new Transform3D();
		 vpTransGroup.getTransform(tf);
		 tf.mul(viewTrans);

		 vpTransGroup.setTransform(tf);
		 
		 totalX = totalX + (float)(radius *  3.0f/width);
		 //System.out.println("imgecnt="+ imageCnt + " curpos="+ currPos.x + " totalx="+ totalX + " dist="+ (totalX -3*radius/totalCnt));
		if (captureOn && imageCnt < totalCnt && Math.abs(totalX - 3*radius/totalCnt) < 0.01  ) {
	            grabCurrentImage(imageCnt);
		    imageCnt++;
		    totalX = 0;
		}
		else if (imageCnt >= totalCnt) {
			captureOn = false;
			imageCnt=0;
			System.out.println("------- capture image done-------");
		}
		
           }
	}

	public void createDots() {
	    float xv = 300, yv=300, zv=objDistance;
            double[] ptArray={xv, yv, zv,
		    -xv, yv, zv,
		    -xv, -yv, zv,
		    xv, -yv, zv};
            float sx = xv, sy=yv ;
            TexCoord2f[] texpt=new TexCoord2f[4];
	    texpt[0]=new TexCoord2f(0.0f,0.0f);
	    texpt[1]=new TexCoord2f(sx,0.0f);
    	    texpt[2]=new TexCoord2f(sx,sy);
    	    texpt[3]=new TexCoord2f(0.0f,sy);
		
            GeometryInfo gi= new GeometryInfo(GeometryInfo.QUAD_ARRAY);
            gi.setCoordinates(ptArray);
            gi.setTextureCoordinateParams(1, 2);
            gi.setTextureCoordinates(0, texpt);
 

           NormalGenerator normalGenerator = new NormalGenerator();
           normalGenerator.generateNormals(gi);
 
           GeometryArray myQuadArray = gi.getGeometryArray();
           Shape3D shape = new Shape3D(myQuadArray, normApp);
          
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
      else if( c == 'd' || c == 'D') captureOn = true;
      else if(c == 'm' || c == 'm') {
	   if (testDialog == null) testDialog = new TestDialog(this);
	   testDialog.setVisible(true);
      }
  }  


  public void  updateDisplay(int animationType, int animationSpeed, boolean wrapTexture, String filename, int shapeType, String shapeName) {
      canvas.stopRenderer();
      if (!filename.startsWith(dataDir)) filename = dataDir+ filename;
      this.animationType = animationType;
      this.wrapTexture = wrapTexture;
      this.objFile = filename;
      timer.setDelay(animationSpeed);
      if (wrapTexture) backgroundSW.setWhichChild(0);
      else backgroundSW.setWhichChild(Switch.CHILD_NONE);

      objRoot.detach();
      int cnt = objRoot.numChildren();
      System.out.println(" scenrot num="+ cnt);
      if (cnt > 0) objRoot.removeChild(0); 

      objectDisplay = new ObjectDisplay();
       if (shapeType == 0) objectDisplay.createPrimShape(shapeName);
      else objectDisplay.loadData(filename);
      objRoot.addChild(objectDisplay.topGroup); 

      examineGroup.addChild(objRoot);
      setViewpoint();
      canvas.startRenderer();

      BoundingSphere sceneBounds = (BoundingSphere) sceneRoot.getBounds();
      radius = sceneBounds.getRadius();
      //System.out.println(" update obj="+ filename + " radius="+ radius);
  }

  public void grabCurrentImage(int idx) {
     try {
          imgs[idx] = robot.createScreenCapture(rect);
	  String fname = dataDir +  "../images/data" + idx + ".gif";
	  File file = new File(fname);
	  ImageIO.write(imgs[idx], "gif", file);
     } catch(Exception e) {
	  e.printStackTrace();
     }
  }

  class ObjectDisplay {
      Switch			sw;
      BranchGroup		topGroup;
      TransformGroup		objTG;
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
	      /*
	 sw = new Switch();
	 sw.setCapability(Switch.ALLOW_SWITCH_READ);
         sw.setCapability(Switch.ALLOW_SWITCH_WRITE);
         sw.setCapability(Group.ALLOW_CHILDREN_READ);
         sw.setCapability(Group.ALLOW_CHILDREN_WRITE);
         sw.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	 */

         topGroup = new BranchGroup();
	 topGroup.setCapability(BranchGroup.ALLOW_DETACH);
	 topGroup.setCapability(Group.ALLOW_COLLISION_BOUNDS_READ);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_READ);
	 topGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

         objTG= new TransformGroup();
	 objTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	 objTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	 objTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	 objTG.setCapability(Group.ALLOW_CHILDREN_READ);
	 objTG.setCapability(Group.ALLOW_CHILDREN_WRITE);
         objTG.setCapability(Group.ALLOW_CHILDREN_EXTEND);
      }
     

      public void loadData(String filename) {
	if (!filename.endsWith(".obj")) {
		createMultiObjects();
		return;
	}

	  objScene = loadObject(filename);
	  addObj(objScene);
	  setTransform(0,0,0, 0,0,objDistance*0.95f);
	  if (filename.endsWith("head.obj")) setTransform(-80,0,0,0,0,0);
	  topGroup.addChild(objTG);
	 
      }
      public void addObj(Scene scene) {
                TransformGroup tgs = new TransformGroup();
	        Transform3D tfs = new Transform3D();
	        tgs.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	        tgs.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	        tgs.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	        tgs.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
	        tgs.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		tgs.addChild(scene.getSceneGroup());
	        objTG.addChild(tgs);
      }
      private void setTransform(float rotx, float roty, float rotz, float x, float y, float z) {
	   Transform3D tf = new Transform3D();
	   objTG.getTransform(tf);
	   if (Math.abs(rotx) >0) {
	     Transform3D tf2 = new Transform3D();
	     tf2.rotX(rotx*rad);
	     tf.mul(tf2);
	   }
	   if (Math.abs(roty) >0) {
	     Transform3D tf2 = new Transform3D();
	     tf2.rotY(roty*rad);
	     tf.mul(tf2);
	   }
	   if (Math.abs(rotz) >0) {
	     Transform3D tf2 = new Transform3D();
	     tf2.rotZ(rotz*rad);
	     tf.mul(tf2);
	   }
	   if (Math.abs(x) >0 || Math.abs(y) > 0 || Math.abs(z) > 0) {
	     Transform3D tf2 = new Transform3D();
	     tf2.setTranslation(new Vector3f(x,y,x));
	     tf.mul(tf2);
	   }
	   objTG.setTransform(tf);

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


      public void createMultiObjects() {
	    String [] filenames = {"Raptor.obj", "box.obj"};
	    int n=filenames.length;
	    System.out.println("----- in createMultobjects------");
	    Scene [] scenes = new Scene[n];
	    TransformGroup [] tgs = new TransformGroup[n];
	    Transform3D [] tfs = new Transform3D[n];
            objXform = new Transform3D(); 
	    for (int i=0; i<n; i++) {
		scenes[i] = loadObject(dataDir+filenames[i]);
	
		addObj(scenes[i]);
	        tgs[i] = (TransformGroup)objTG.getChild(i);
	        tfs[i] = new Transform3D();
	        tgs[i].getTransform(tfs[i]);
	    }
	   

	    tfs[0].setTranslation(new Vector3f(  0.5f, -0.6f, 0.5f));
	    tfs[1].setTranslation(new Vector3f( -0.8f, 0, -1.0f));
	    for (int i=0; i<n; i++) 
		    tgs[i].setTransform(tfs[i]);

	    setTransform(0,0,0, 0,0,objDistance*0.95f);
	    topGroup.addChild(objTG);
      }

      public void createPrimShape(String shapeName) {
	       TransformGroup tgs = new TransformGroup();
	       Transform3D tfs = new Transform3D();
	       tgs.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
	       tgs.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	       tgs.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
	       tgs.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
	       tgs.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
               int primflags = Primitive.GENERATE_NORMALS ;
	       Appearance shapeApp = new Appearance();
	       shapeApp.setMaterial(new Material());
	       if (shapeName.toLowerCase().equals("box")) {
		   Box  box;
		   if (wrapTexture) box = new Box(1.0f, 1.0f, 1.0f, Box.GENERATE_TEXTURE_COORDS, normApp);
		   else box = new Box(1.0f, 1.0f, 1.0f, primflags, shapeApp);
	           tgs.addChild(box);
	       }
	       else if (shapeName.toLowerCase().equals("sphere")) {
                   Sphere sphere;
		   if (wrapTexture) sphere = new Sphere(0.5f, Sphere.GENERATE_TEXTURE_COORDS, 100, dotApp);
		   else sphere = new Sphere(0.5f, primflags, 100, shapeApp);
	           tgs.addChild(sphere);
	       }
	       else if (shapeName.toLowerCase().equals("cone")) {
                   Cone cone;
		   if (wrapTexture) cone =  new Cone(0.5f, 1.0f, Cone.GENERATE_TEXTURE_COORDS,dotApp);
		   else cone = new Cone(0.5f, 1.0f, primflags, shapeApp);
	           tgs.addChild(cone);
	       }
	       else if (shapeName.toLowerCase().equals("cylinder")) {
                   Cylinder cylinder;
		   if (wrapTexture) cylinder = new Cylinder (0.5f, 1.0f,Cylinder.GENERATE_TEXTURE_COORDS,dotApp );
		   else cylinder = new Cylinder (0.5f, 1.0f,primflags,shapeApp );
	           tgs.addChild(cylinder );
	       }
	       objTG.addChild(tgs);
	       setTransform(0,0,0, 0,-0.3f,objDistance*0.95f);
	       topGroup.addChild(objTG);
      }

  }
  public void objsRotation() {
          TransformGroup objTG = objectDisplay.objTG;
	  int n = objTG.numChildren();
	  TransformGroup [] tgs = new TransformGroup[n];
	  Transform3D [] tfs = new Transform3D[n];
	  Transform3D  tf;

	  for (int i=0; i<n; i++) {
	      tgs[i] = (TransformGroup)objTG.getChild(i);
	      tfs[i] = new Transform3D();
	      tgs[i].getTransform(tfs[i]);
	      tf = new Transform3D();
	      tf.rotY(2.0f*rad);
	      tfs[i].mul(tf);
	      tgs[i].setTransform(tfs[i]);
	  }
  }

}
