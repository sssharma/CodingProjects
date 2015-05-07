// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 7/7/2006 1:25:55 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 


import java.awt.*;
import com.sun.j3d.loaders.*;
import com.sun.j3d.utils.geometry.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.media.j3d.*;
import com.sun.j3d.utils.image.TextureLoader;
import javax.vecmath.*;

// Referenced classes of package com.sun.j3d.loaders.objectfile:
//            ObjectFileParser, ObjectFileMaterials

public class MyObjectFile
    implements Loader
{
    Appearance dotApp;

    void readVertex(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        Point3f point3f = new Point3f();
        objectfileparser.getNumber();
        point3f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        point3f.y = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        point3f.z = (float)objectfileparser.nval;
        objectfileparser.skipToNextLine();
        coordList.add(point3f);
    }

    void readNormal(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        Vector3f vector3f = new Vector3f();
        objectfileparser.getNumber();
        vector3f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        vector3f.y = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        vector3f.z = (float)objectfileparser.nval;
        objectfileparser.skipToNextLine();
        normList.add(vector3f);
    }

    void readTexture(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        TexCoord2f texcoord2f = new TexCoord2f();
        objectfileparser.getNumber();
        texcoord2f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        texcoord2f.y = (float)objectfileparser.nval;
        objectfileparser.skipToNextLine();
        texList.add(texcoord2f);
    }

    void readFace(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        int l;
label0:
        {
            boolean flag = false;
            boolean flag1 = false;
            l = 0;
            objectfileparser.getToken();
            do
            {
                ObjectFileParser _tmp = objectfileparser;
                if(objectfileparser.ttype == 10)
                    break label0;
                objectfileparser.pushBack();
                objectfileparser.getNumber();
                int i = (int)objectfileparser.nval - 1;
                if(i < 0)
                    i += coordList.size() + 1;
                coordIdxList.add(new Integer(i));
                objectfileparser.getToken();
                if(objectfileparser.ttype == 47)
                {
                    objectfileparser.getToken();
                    ObjectFileParser _tmp1 = objectfileparser;
                    if(objectfileparser.ttype == -3)
                    {
                        objectfileparser.pushBack();
                        objectfileparser.getNumber();
                        int j = (int)objectfileparser.nval - 1;
                        if(j < 0)
                            j += texList.size() + 1;
                        texIdxList.add(new Integer(j));
                        objectfileparser.getToken();
                    }
                    if(objectfileparser.ttype == 47)
                    {
                        objectfileparser.getNumber();
                        int k = (int)objectfileparser.nval - 1;
                        if(k < 0)
                            k += normList.size() + 1;
                        normIdxList.add(new Integer(k));
                        objectfileparser.getToken();
                    }
                }
                l++;
            } while(true);
        }
        Integer integer = new Integer(stripCounts.size());
        stripCounts.add(new Integer(l));
        groups.put(integer, curGroup);
        if(curSgroup != null)
            sGroups.put(integer, curSgroup);
        objectfileparser.skipToNextLine();
    }

    void readPartName(ObjectFileParser objectfileparser)
    {
        objectfileparser.getToken();
        String s = (String)groupMaterials.get(curGroup);
        if(objectfileparser.ttype != -3)
            curGroup = "default";
        else
            curGroup = objectfileparser.sval;
        if(groupMaterials.get(curGroup) == null)
            groupMaterials.put(curGroup, s);
        objectfileparser.skipToNextLine();
    }

    void readMaterialName(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getToken();
        if(objectfileparser.ttype == -3)
            groupMaterials.put(curGroup, new String(objectfileparser.sval));
        objectfileparser.skipToNextLine();
    }

    void loadMaterialFile(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        String s = null;
        objectfileparser.lowerCaseMode(false);
        do
        {
            objectfileparser.getToken();
            if(objectfileparser.ttype == -3)
                s = objectfileparser.sval;
        } while(objectfileparser.ttype != 10);
        materials.readMaterialFile(fromUrl, fromUrl ? baseUrl.toString() : basePath, s);
        objectfileparser.lowerCaseMode(true);
        objectfileparser.skipToNextLine();
    }

    void readSmoothingGroup(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getToken();
        if(objectfileparser.ttype != -3)
        {
            objectfileparser.skipToNextLine();
            return;
        }
        if(objectfileparser.sval.equals("off"))
            curSgroup = "0";
        else
            curSgroup = objectfileparser.sval;
        objectfileparser.skipToNextLine();
    }

    void readFile(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getToken();
        while(objectfileparser.ttype != -1) 
        {
            if(objectfileparser.ttype == -3)
                if(objectfileparser.sval.equals("v"))
                    readVertex(objectfileparser);
                else
                if(objectfileparser.sval.equals("vn"))
                    readNormal(objectfileparser);
                else
                if(objectfileparser.sval.equals("vt"))
                    readTexture(objectfileparser);
                else
                if(objectfileparser.sval.equals("f"))
                    readFace(objectfileparser);
                else
                if(objectfileparser.sval.equals("fo"))
                    readFace(objectfileparser);
                else
                if(objectfileparser.sval.equals("g"))
                    readPartName(objectfileparser);
                else
                if(objectfileparser.sval.equals("s"))
                    readSmoothingGroup(objectfileparser);
                else
                if(objectfileparser.sval.equals("p"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("l"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("mtllib"))
                    loadMaterialFile(objectfileparser);
                else
                if(objectfileparser.sval.equals("usemtl"))
                    readMaterialName(objectfileparser);
                else
                if(objectfileparser.sval.equals("maplib"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("usemap"))
                    objectfileparser.skipToNextLine();
                else
                    throw new ParsingErrorException("Unrecognized token, line " + objectfileparser.lineno());
            objectfileparser.skipToNextLine();
            objectfileparser.getToken();
        }
    }

    public void setAppearance(Appearance dotApp) {
	    this.dotApp = dotApp;
    }
    public MyObjectFile(int i, float f)
    {
        basePath = null;
        baseUrl = null;
        fromUrl = false;
        coordArray = null;
        normArray = null;
        texArray = null;
        materials = null;
        setFlags(i);
        radians = f;

    }

    public MyObjectFile(int i)
    {
        this(i, -1F);
    }

    public MyObjectFile()
    {
        this(0, -1F);
    }

    private void setBasePathFromFilename(String s)
    {
        if(s.lastIndexOf(File.separator) == -1)
            setBasePath("." + File.separator);
        else
            setBasePath(s.substring(0, s.lastIndexOf(File.separator)));
    }

    public Scene load(String s)
        throws FileNotFoundException, IncorrectFormatException, ParsingErrorException
    {
        setBasePathFromFilename(s);
        BufferedReader bufferedreader = new BufferedReader(new FileReader(s));
        return load(((Reader) (bufferedreader)));
    }

    private void setBaseUrlFromUrl(URL url)
        throws FileNotFoundException
    {
        String s = url.toString();
        String s1;
        if(s.lastIndexOf('/') == -1)
            s1 = url.getProtocol() + ":";
        else
            s1 = s.substring(0, s.lastIndexOf('/') + 1);
        try
        {
            baseUrl = new URL(s1);
        }
        catch(MalformedURLException malformedurlexception)
        {
            throw new FileNotFoundException(malformedurlexception.getMessage());
        }
    }

    public Scene load(URL url)
        throws FileNotFoundException, IncorrectFormatException, ParsingErrorException
    {
        if(baseUrl == null)
            setBaseUrlFromUrl(url);
        BufferedReader bufferedreader;
        try
        {
            bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
        }
        catch(IOException ioexception)
        {
            throw new FileNotFoundException(ioexception.getMessage());
        }
        fromUrl = true;
        return load(((Reader) (bufferedreader)));
    }

    private Point3f[] getLimits()
    {
        Point3f point3f = new Point3f();
        Point3f apoint3f[] = new Point3f[2];
        apoint3f[0] = new Point3f(3.402823E+038F, 3.402823E+038F, 3.402823E+038F);
        apoint3f[1] = new Point3f(1.401298E-045F, 1.401298E-045F, 1.401298E-045F);
        for(int i = 0; i < coordList.size(); i++)
        {
            Point3f point3f1 = (Point3f)coordList.get(i);
            if(point3f1.x < apoint3f[0].x)
                apoint3f[0].x = point3f1.x;
            if(point3f1.x > apoint3f[1].x)
                apoint3f[1].x = point3f1.x;
            if(point3f1.y < apoint3f[0].y)
                apoint3f[0].y = point3f1.y;
            if(point3f1.y > apoint3f[1].y)
                apoint3f[1].y = point3f1.y;
            if(point3f1.z < apoint3f[0].z)
                apoint3f[0].z = point3f1.z;
            if(point3f1.z > apoint3f[1].z)
                apoint3f[1].z = point3f1.z;
        }

        return apoint3f;
    }

    private void resize()
    {
        Point3f point3f = new Point3f();
        Point3f apoint3f[] = getLimits();
        Vector3f vector3f = new Vector3f(-0.5F * (apoint3f[0].x + apoint3f[1].x), -0.5F * (apoint3f[0].y + apoint3f[1].y), -0.5F * (apoint3f[0].z + apoint3f[1].z));
        float f = apoint3f[1].x - apoint3f[0].x;
        if(f < apoint3f[1].y - apoint3f[0].y)
            f = apoint3f[1].y - apoint3f[0].y;
        if(f < apoint3f[1].z - apoint3f[0].z)
            f = apoint3f[1].z - apoint3f[0].z;
        f /= 2.0F;
        for(int i = 0; i < coordList.size(); i++)
        {
            Point3f point3f1 = (Point3f)coordList.get(i);
            point3f1.add(point3f1, vector3f);
            point3f1.x /= f;
            point3f1.y /= f;
            point3f1.z /= f;
        }

    }

    private int[] objectToIntArray(ArrayList arraylist)
    {
        int ai[] = new int[arraylist.size()];
        for(int i = 0; i < arraylist.size(); i++)
            ai[i] = ((Integer)arraylist.get(i)).intValue();

        return ai;
    }

    private Point3f[] objectToPoint3Array(ArrayList arraylist)
    {
        Point3f apoint3f[] = new Point3f[arraylist.size()];
        for(int i = 0; i < arraylist.size(); i++)
            apoint3f[i] = (Point3f)arraylist.get(i);

        return apoint3f;
    }

    private TexCoord2f[] objectToTexCoord2Array(ArrayList arraylist)
    {
        TexCoord2f atexcoord2f[] = new TexCoord2f[arraylist.size()];
        for(int i = 0; i < arraylist.size(); i++)
            atexcoord2f[i] = (TexCoord2f)arraylist.get(i);

        return atexcoord2f;
    }

    private Vector3f[] objectToVectorArray(ArrayList arraylist)
    {
        Vector3f avector3f[] = new Vector3f[arraylist.size()];
        for(int i = 0; i < arraylist.size(); i++)
            avector3f[i] = (Vector3f)arraylist.get(i);

        return avector3f;
    }

    private int[] groupIndices(ArrayList arraylist, ArrayList arraylist1)
    {
        int ai[] = new int[arraylist1.size() * 3];
        for(int i = 0; i < arraylist1.size(); i++)
        {
            int j = ((Integer)arraylist1.get(i)).intValue();
            ai[i * 3 + 0] = ((Integer)arraylist.get(j + 0)).intValue();
            ai[i * 3 + 1] = ((Integer)arraylist.get(j + 1)).intValue();
            ai[i * 3 + 2] = ((Integer)arraylist.get(j + 2)).intValue();
        }

        return ai;
    }

    private void smoothingGroupNormals()
    {
        NormalGenerator normalgenerator = new NormalGenerator(radians != -1F ? radians : 3.1415926535897931D);
        NormalGenerator normalgenerator1 = new NormalGenerator(0.0D);
        normList.clear();
        normIdxList = null;
        int ai[] = new int[coordIdxList.size()];
        Iterator iterator = triSgroups.keySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            ArrayList arraylist = (ArrayList)triSgroups.get(s);
            if(arraylist.size() > 0)
            {
                GeometryInfo geometryinfo = new GeometryInfo(1);
                geometryinfo.setCoordinateIndices(groupIndices(coordIdxList, arraylist));
                geometryinfo.setCoordinates(coordArray);
                if(s.equals("0"))
                    normalgenerator1.generateNormals(geometryinfo);
                else
                    normalgenerator.generateNormals(geometryinfo);
                Vector3f avector3f[] = geometryinfo.getNormals();
                int ai1[] = geometryinfo.getNormalIndices();
                int j = 0;
                int k = 0;
                while(k < arraylist.size()) 
                {
                    int l = ((Integer)arraylist.get(k)).intValue();
                    for(int i1 = 0; i1 < 3; i1++)
                    {
                        ai[l + i1] = normList.size();
                        normList.add(avector3f[ai1[j++]]);
                    }

                    k++;
                }
            }
        } while(true);
        normIdxList = new ArrayList(coordIdxList.size());
        for(int i = 0; i < coordIdxList.size(); i++)
            normIdxList.add(new Integer(ai[i]));

        normArray = objectToVectorArray(normList);
    }

    private void convertToTriangles()
    {
        boolean flag = (flags & 0x80) != 0;
        boolean flag1 = !texList.isEmpty() && !texIdxList.isEmpty() && texIdxList.size() == coordIdxList.size();
        boolean flag2 = !normList.isEmpty() && !normIdxList.isEmpty() && normIdxList.size() == coordIdxList.size();
        int i = stripCounts.size();
        boolean flag3 = curSgroup != null;
        triGroups = new HashMap(50);
        if(flag3)
            triSgroups = new HashMap(50);
        ArrayList arraylist = null;
        ArrayList arraylist1 = null;
        ArrayList arraylist2 = null;
        if(flag)
        {
            GeometryInfo geometryinfo = new GeometryInfo(5);
            geometryinfo.setStripCounts(objectToIntArray(stripCounts));
            geometryinfo.setCoordinates(coordArray);
            geometryinfo.setCoordinateIndices(objectToIntArray(coordIdxList));
            if(flag1)
            {
                geometryinfo.setTextureCoordinateParams(1, 2);
                geometryinfo.setTextureCoordinates(0, texArray);
                geometryinfo.setTextureCoordinateIndices(0, objectToIntArray(texIdxList));
            }
            if(flag2)
            {
                geometryinfo.setNormals(normArray);
                geometryinfo.setNormalIndices(objectToIntArray(normIdxList));
            }
            geometryinfo.convertToIndexedTriangles();
            int ai[] = geometryinfo.getCoordinateIndices();
            int l = 0;
            for(int j1 = 0; j1 < i; j1++)
                l += ((Integer)stripCounts.get(j1)).intValue() - 2;

            if(ai.length != l * 3)
            {
                flag = false;
            } else
            {
                int ai1[] = geometryinfo.getTextureCoordinateIndices();
                int ai2[] = geometryinfo.getNormalIndices();
                coordIdxList.clear();
                texIdxList.clear();
                normIdxList.clear();
                for(int i2 = 0; i2 < ai.length; i2++)
                {
                    coordIdxList.add(new Integer(ai[i2]));
                    if(flag1)
                        texIdxList.add(new Integer(ai1[i2]));
                    if(flag2)
                        normIdxList.add(new Integer(ai2[i2]));
                }

            }
        }
        if(!flag)
        {
            arraylist = new ArrayList();
            if(flag1)
                arraylist1 = new ArrayList();
            if(flag2)
                arraylist2 = new ArrayList();
        }
        int j = 0;
label0:
        for(int k = 0; k < i; k++)
        {
            int i1 = ((Integer)stripCounts.get(k)).intValue();
            Integer integer = new Integer(k);
            curGroup = (String)groups.get(integer);
            curTriGroup = (ArrayList)triGroups.get(curGroup);
            if(curTriGroup == null)
            {
                curTriGroup = new ArrayList();
                triGroups.put(curGroup, curTriGroup);
            }
            if(flag3)
            {
                curSgroup = (String)sGroups.get(integer);
                if(curSgroup == null)
                    curSgroup = "0";
                curTriSgroup = (ArrayList)triSgroups.get(curSgroup);
                if(curTriSgroup == null)
                {
                    curTriSgroup = new ArrayList();
                    triSgroups.put(curSgroup, curTriSgroup);
                }
            }
            if(flag)
            {
                int k1 = 0;
                do
                {
                    if(k1 >= i1 - 2)
                        continue label0;
                    Integer integer1 = new Integer(j);
                    curTriGroup.add(integer1);
                    if(flag3)
                        curTriSgroup.add(integer1);
                    j += 3;
                    k1++;
                } while(true);
            }
            for(int l1 = 0; l1 < i1 - 2; l1++)
            {
                Integer integer2 = new Integer(arraylist.size());
                curTriGroup.add(integer2);
                if(flag3)
                    curTriSgroup.add(integer2);
                arraylist.add(coordIdxList.get(j));
                arraylist.add(coordIdxList.get(j + l1 + 1));
                arraylist.add(coordIdxList.get(j + l1 + 2));
                if(flag1)
                {
                    arraylist1.add(texIdxList.get(j));
                    arraylist1.add(texIdxList.get(j + l1 + 1));
                    arraylist1.add(texIdxList.get(j + l1 + 2));
                }
                if(flag2)
                {
                    arraylist2.add(normIdxList.get(j));
                    arraylist2.add(normIdxList.get(j + l1 + 1));
                    arraylist2.add(normIdxList.get(j + l1 + 2));
                }
            }

            j += i1;
        }

        stripCounts = null;
        groups = null;
        sGroups = null;
        if(!flag)
        {
            coordIdxList = arraylist;
            texIdxList = arraylist1;
            normIdxList = arraylist2;
        }
    }

    private SceneBase makeScene()
    {
        SceneBase scenebase = new SceneBase();
        BranchGroup branchgroup = new BranchGroup();
        scenebase.setSceneGroup(branchgroup);
        boolean flag = normList.isEmpty() || normIdxList.isEmpty() || normIdxList.size() != coordIdxList.size();
        boolean flag1 = !texList.isEmpty() && !texIdxList.isEmpty() && texIdxList.size() == coordIdxList.size();
        coordArray = objectToPoint3Array(coordList);
        if(!flag)
            normArray = objectToVectorArray(normList);
        if(flag1)
            texArray = objectToTexCoord2Array(texList);
        convertToTriangles();

        if(flag && curSgroup != null)
        {
            smoothingGroupNormals();
            flag = false;
        }
        NormalGenerator normalgenerator = null;
        if(flag)
            normalgenerator = new NormalGenerator(radians);
        Stripifier stripifier = null;
        if((flags & 0x200) != 0)
            stripifier = new Stripifier();
        long l = 0L;
        long l1 = 0L;
        long l2 = 0L;
        long l3 = 0L;

        Iterator iterator = triGroups.keySet().iterator();
	int mcnt=0;
	/*
		        TextureLoader loader = new TextureLoader("data/dot2.gif",
                   // "LUMINANCE", new Container());
		   "INTENSITY", new Container());
                Texture texture = loader.getTexture();
                texture.setBoundaryModeS(Texture.WRAP      );
                texture.setBoundaryModeT(Texture.WRAP);
   
                TextureAttributes texAttr = new TextureAttributes();
                texAttr.setTextureMode(TextureAttributes.MODULATE);
	        //texAttr.setBoundaryModeS(Texture.WRAP);
		//texAttr.setBoundaryModeT(Texture.WRAP);
                Appearance dotApp = new Appearance();
                TexCoordGeneration tcg = new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR,
                                                        TexCoordGeneration.TEXTURE_COORDINATE_2);
                dotApp.setTexCoordGeneration(tcg);
		
                dotApp.setTexture(texture);
                dotApp.setTextureAttributes(texAttr);

                PolygonAttributes pa = new PolygonAttributes();
                pa.setCullFace(pa.CULL_BACK);
                dotApp.setPolygonAttributes(pa);
		*/
		  float sv=1;
           
        do
        {
            if(!iterator.hasNext())
                break;
        	    
            String s = (String)iterator.next();
            ArrayList arraylist = (ArrayList)triGroups.get(s);
	   
            if(arraylist.size() > 0)
            {
                GeometryInfo geometryinfo = new GeometryInfo(1);
                geometryinfo.setCoordinateIndices(groupIndices(coordIdxList, arraylist));
                geometryinfo.setCoordinates(coordArray);
                if(flag1)
                {
                    geometryinfo.setTextureCoordinateParams(1, 2);
                    geometryinfo.setTextureCoordinates(0, texArray);
                    geometryinfo.setTextureCoordinateIndices(0, groupIndices(texIdxList, arraylist));
                }
                if(flag)
                {
                    if((flags & 0x100) != 0)
                        geometryinfo.reverse();
                    normalgenerator.generateNormals(geometryinfo);
                } else
                {
                    geometryinfo.setNormalIndices(groupIndices(normIdxList, arraylist));
                    geometryinfo.setNormals(normArray);
                    if((flags & 0x100) != 0)
                        geometryinfo.reverse();
                }
                if((flags & 0x200) != 0)
                    stripifier.stripify(geometryinfo);
	

                Shape3D shape3d = new Shape3D();
                shape3d.setGeometry(geometryinfo.getGeometryArray(true, true, false));
		//shape3d.setAppearance(dotApp);
                String s1 = (String)groupMaterials.get(s);
		
                materials.assignMaterial(s1, shape3d);
                branchgroup.addChild(shape3d);
                scenebase.addNamedObject(s, shape3d);
            }
        } while(true);
        return scenebase;
    }

    public Scene load(Reader reader)
        throws FileNotFoundException, IncorrectFormatException, ParsingErrorException
    {
        ObjectFileParser objectfileparser = new ObjectFileParser(reader);
        coordList = new ArrayList();
        texList = new ArrayList();
        normList = new ArrayList();
        coordIdxList = new ArrayList();
        texIdxList = new ArrayList();
        normIdxList = new ArrayList();
        groups = new HashMap(50);
        curGroup = "default";
        sGroups = new HashMap(50);
        curSgroup = null;
        stripCounts = new ArrayList();
        groupMaterials = new HashMap(50);
        groupMaterials.put(curGroup, "default");
        materials = new ObjectFileMaterials();
        time = 0L;
        readFile(objectfileparser);
        if((flags & 0x40) != 0)
            resize();
        return makeScene();
    }

    public void setBaseUrl(URL url)
    {
        baseUrl = url;
    }

    public URL getBaseUrl()
    {
        return baseUrl;
    }

    public void setBasePath(String s)
    {
        basePath = s;
        if(basePath == null || basePath == "")
            basePath = "." + File.separator;
        basePath = basePath.replace('/', File.separatorChar);
        basePath = basePath.replace('\\', File.separatorChar);
        if(!basePath.endsWith(File.separator))
            basePath = basePath + File.separator;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setFlags(int i)
    {
        flags = i;
    }

    public int getFlags()
    {
        return flags;
    }

    private static final int DEBUG = 0;
    public static final int RESIZE = 64;
    public static final int TRIANGULATE = 128;
    public static final int REVERSE = 256;
    public static final int STRIPIFY = 512;
    private static final char BACKSLASH = 92;
    private int flags;
    private String basePath;
    private URL baseUrl;
    private boolean fromUrl;
    private float radians;
    private ArrayList coordList;
    private ArrayList texList;
    private ArrayList normList;
    private ArrayList coordIdxList;
    private ArrayList texIdxList;
    private ArrayList normIdxList;
    private ArrayList stripCounts;
    private HashMap groups;
    private String curGroup;
    private HashMap sGroups;
    private String curSgroup;
    private HashMap groupMaterials;
    private HashMap triGroups;
    private ArrayList curTriGroup;
    private HashMap triSgroups;
    private ArrayList curTriSgroup;
    private Point3f coordArray[];
    private Vector3f normArray[];
    private TexCoord2f texArray[];
    private long time;
    private ObjectFileMaterials materials;
}
