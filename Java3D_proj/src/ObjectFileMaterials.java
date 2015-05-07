// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 7/7/2006 1:25:55 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 



import com.sun.j3d.loaders.ParsingErrorException;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import com.sun.j3d.utils.image.TextureLoader;
// Referenced classes of package com.sun.j3d.loaders.objectfile:
//            RgbFile, ObjectFileParser

class ObjectFileMaterials
    implements ImageObserver
{
    private class ObjectFileMaterial
    {

        public void ObjectFileMaterial()
        {
            Ka = null;
            Kd = null;
            Ks = null;
            illum = -1;
            Ns = -1F;
            t = null;
        }

        public Color3f Ka;
        public Color3f Kd;
        public Color3f Ks;
        public int illum;
        public float Ns;
        public Texture2D t;
        public boolean transparent;
        public float transparencyLevel;

        private ObjectFileMaterial()
        {
            super();
        }

    }


    void assignMaterial(String s, Shape3D shape3d)
    {
        ObjectFileMaterial objectfilematerial = null;
        Material material = new Material();
        objectfilematerial = (ObjectFileMaterial)materials.get(s);
        Appearance appearance = new Appearance();
        if(objectfilematerial != null)
        {
            if(objectfilematerial.Ka != null)
                material.setAmbientColor(objectfilematerial.Ka);
            if(objectfilematerial.Kd != null)
                material.setDiffuseColor(objectfilematerial.Kd);
            if(objectfilematerial.Ks != null && objectfilematerial.illum != 1)
                material.setSpecularColor(objectfilematerial.Ks);
            else
            if(objectfilematerial.illum == 1)
                material.setSpecularColor(0.0F, 0.0F, 0.0F);
            if(objectfilematerial.illum >= 1)
                material.setLightingEnable(true);
            else
            if(objectfilematerial.illum == 0)
                material.setLightingEnable(false);
            if(objectfilematerial.Ns != -1F)
                material.setShininess(objectfilematerial.Ns);
            if(objectfilematerial.t != null)
            {
                appearance.setTexture(objectfilematerial.t);
                if((((GeometryArray)shape3d.getGeometry()).getVertexFormat() & 0x20) == 0)
                {
                    TexCoordGeneration texcoordgeneration = new TexCoordGeneration();
                    appearance.setTexCoordGeneration(texcoordgeneration);
                }
            }
            if(objectfilematerial.transparent)
                appearance.setTransparencyAttributes(new TransparencyAttributes(1, objectfilematerial.transparencyLevel));
        }
        appearance.setMaterial(material);
        shape3d.setAppearance(appearance);
    }

    private void readName(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getToken();
        if(objectfileparser.ttype == -3)
        {
            if(curName != null)
                materials.put(curName, cur);
            curName = new String(objectfileparser.sval);
            cur = new ObjectFileMaterial();
        }
        objectfileparser.skipToNextLine();
    }

    private void readAmbient(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        Color3f color3f = new Color3f();
        objectfileparser.getNumber();
        color3f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.y = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.z = (float)objectfileparser.nval;
        cur.Ka = color3f;
        objectfileparser.skipToNextLine();
    }

    private void readDiffuse(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        Color3f color3f = new Color3f();
        objectfileparser.getNumber();
        color3f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.y = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.z = (float)objectfileparser.nval;
        cur.Kd = color3f;
        objectfileparser.skipToNextLine();
    }

    private void readSpecular(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        Color3f color3f = new Color3f();
        objectfileparser.getNumber();
        color3f.x = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.y = (float)objectfileparser.nval;
        objectfileparser.getNumber();
        color3f.z = (float)objectfileparser.nval;
        cur.Ks = color3f;
        objectfileparser.skipToNextLine();
    }

    private void readIllum(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getNumber();
        cur.illum = (int)objectfileparser.nval;
        objectfileparser.skipToNextLine();
    }

    private void readTransparency(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getNumber();
        cur.transparencyLevel = (float)objectfileparser.nval;
        if(cur.transparencyLevel < 1.0F)
            cur.transparent = true;
        objectfileparser.skipToNextLine();
    }

    private void readShininess(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getNumber();
        cur.Ns = (float)objectfileparser.nval;
        if(cur.Ns < 1.0F)
            cur.Ns = 1.0F;
        else
        if(cur.Ns > 128F)
            cur.Ns = 128F;
        objectfileparser.skipToNextLine();
    }

    public void readMapKd(ObjectFileParser objectfileparser)
    {
        objectfileparser.lowerCaseMode(false);
        String s = null;
        do
        {
            objectfileparser.getToken();
            if(objectfileparser.ttype == -3)
                s = objectfileparser.sval;
        } while(objectfileparser.ttype != 10);
        objectfileparser.lowerCaseMode(true);
        if(s != null && s.lastIndexOf('.') != -1)
            try
            {
                String s1 = s.substring(s.lastIndexOf('.') + 1).toLowerCase();
                TextureLoader textureloader = null;
                if(s1.equals("int") || s1.equals("inta") || s1.equals("rgb") || s1.equals("rgba") || s1.equals("bw") || s1.equals("sgi"))
                {
                    RgbFile rgbfile;
                    if(fromUrl)
                        rgbfile = new RgbFile((new URL(basePath + s)).openStream());
                    else
                        rgbfile = new RgbFile(new FileInputStream(basePath + s));
                    java.awt.image.BufferedImage bufferedimage = rgbfile.getImage();
                    boolean flag = s1.equals("int") || s1.equals("inta");
                    boolean flag1 = s1.equals("inta") || s1.equals("rgba") || s1.equals("rgb");
                    cur.transparent = flag1;
                    String s2 = null;
                    if(flag && flag1)
                        s2 = "LUM8_ALPHA8";
                    else
                    if(flag)
                        s2 = "LUMINANCE";
                    else
                    if(flag1)
                        s2 = "RGBA";
                    else
                        s2 = "RGB";
                    textureloader = new TextureLoader(bufferedimage, s2, 1);
                } else {
                	String s2 = "RGB";
                	
                	if(s1.equals("gif") || s1.equals("png"))
                		s2 = "RGBA";
                	
	                if(fromUrl)
	                    textureloader = new TextureLoader(new URL(basePath + s), s2, 1, null);
	                else
	                    textureloader = new TextureLoader(basePath + s, s2, 1, null);
	                
	                
                }
                Texture2D texture2d = (Texture2D)textureloader.getTexture();
                if(texture2d != null)
                {
                    cur.t = texture2d;
                    //cur.transparent = textureloader.hasAlpha();
		    cur.transparent = false;
                }
            }
            catch(FileNotFoundException filenotfoundexception) { }
            catch(MalformedURLException malformedurlexception) { }
            catch(IOException ioexception) { }
        objectfileparser.skipToNextLine();
    }
    
    private void readFile(ObjectFileParser objectfileparser)
        throws ParsingErrorException
    {
        objectfileparser.getToken();
        while(objectfileparser.ttype != -1) 
        {
            if(objectfileparser.ttype == -3)
                if(objectfileparser.sval.equals("newmtl"))
                    readName(objectfileparser);
                else
                if(objectfileparser.sval.equals("ka"))
                    readAmbient(objectfileparser);
                else
                if(objectfileparser.sval.equals("kd"))
                    readDiffuse(objectfileparser);
                else
                if(objectfileparser.sval.equals("ks"))
                    readSpecular(objectfileparser);
                else
                if(objectfileparser.sval.equals("illum"))
                    readIllum(objectfileparser);
                else
                if(objectfileparser.sval.equals("d"))
                    readTransparency(objectfileparser);
                else
                if(objectfileparser.sval.equals("ns"))
                    readShininess(objectfileparser);
                else
                if(objectfileparser.sval.equals("tf"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("sharpness"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("map_kd"))
                    readMapKd(objectfileparser);
                else
                if(objectfileparser.sval.equals("map_ka"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("map_ks"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("map_ns"))
                    objectfileparser.skipToNextLine();
                else
                if(objectfileparser.sval.equals("bump"))
                    objectfileparser.skipToNextLine();
            objectfileparser.skipToNextLine();
            objectfileparser.getToken();
        }
        if(curName != null)
            materials.put(curName, cur);
    }

    void readMaterialFile(boolean flag, String s, String s1)
        throws ParsingErrorException
    {
        basePath = s;
        fromUrl = flag;
        Object obj;
        try
        {
            if(flag)
                obj = new InputStreamReader(new BufferedInputStream((new URL(s + s1)).openStream()));
            else
                obj = new BufferedReader(new FileReader(s + s1));
        }
        catch(IOException ioexception)
        {
            return;
        }
        ObjectFileParser objectfileparser = new ObjectFileParser(((java.io.Reader) (obj)));
        readFile(objectfileparser);
    }

    ObjectFileMaterials()
        throws ParsingErrorException
    {
        curName = null;
        cur = null;
        StringReader stringreader = new StringReader("newmtl amber\nKa 0.0531 0.0531 0.0531\nKd 0.5755 0.2678 0.0000\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl amber_trans\nKa 0.0531 0.0531 0.0531\nKd 0.5755 0.2678 0.0000\nKs 0.3000 0.3000 0.3000\nillum 2\nd 0.8400\nNs 60.0000\n\nnewmtl charcoal\nKa 0.0082 0.0082 0.0082\nKd 0.0041 0.0041 0.0041\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl lavendar\nKa 0.1281 0.0857 0.2122\nKd 0.2187 0.0906 0.3469\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl navy_blue\nKa 0.0000 0.0000 0.0490\nKd 0.0000 0.0000 0.0531\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl pale_green\nKa 0.0444 0.0898 0.0447\nKd 0.0712 0.3796 0.0490\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl pale_pink\nKa 0.0898 0.0444 0.0444\nKd 0.6531 0.2053 0.4160\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl pale_yellow\nKa 0.3606 0.3755 0.0935\nKd 0.6898 0.6211 0.1999\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl peach\nKa 0.3143 0.1187 0.0167\nKd 0.6367 0.1829 0.0156\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl periwinkle\nKa 0.0000 0.0000 0.1184\nKd 0.0000 0.0396 0.8286\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl redwood\nKa 0.0204 0.0027 0.0000\nKd 0.2571 0.0330 0.0000\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl smoked_glass\nKa 0.0000 0.0000 0.0000\nKd 0.0041 0.0041 0.0041\nKs 0.1878 0.1878 0.1878\nillum 2\nd 0.9800\nNs 91.4700\n\nnewmtl aqua_filter\nKa 0.0000 0.0000 0.0000\nKd 0.3743 0.6694 0.5791\nKs 0.1878 0.1878 0.1878\nillum 2\nd 0.9800\nNs 91.4700\n\nnewmtl yellow_green\nKa 0.0000 0.0000 0.0000\nKd 0.1875 0.4082 0.0017\nKs 0.1878 0.1878 0.1878\nillum 2\nNs 91.4700\n\nnewmtl bluetint\nKa 0.1100 0.4238 0.5388\nKd 0.0468 0.7115 0.9551\nKs 0.3184 0.3184 0.3184\nillum 9\nd 0.5700\nNs 60.0000\nsharpness 60.0000\n\nnewmtl plasma\nKa 0.4082 0.0816 0.2129\nKd 1.0000 0.0776 0.4478\nKs 0.3000 0.3000 0.3000\nillum 9\nd 0.7500\nNs 60.0000\nsharpness 60.0000\n\nnewmtl emerald\nKa 0.0470 1.0000 0.0000\nKd 0.0470 1.0000 0.0000\nKs 0.2000 0.2000 0.2000\nillum 9\nd 0.7500\nNs 60.0000\nsharpness 60.0000\n\nnewmtl ruby\nKa 1.0000 0.0000 0.0000\nKd 1.0000 0.0000 0.0000\nKs 0.2000 0.2000 0.2000\nillum 9\nd 0.7500\nNs 60.0000\nsharpness 60.0000\n\nnewmtl sapphire\nKa 0.0235 0.0000 1.0000\nKd 0.0235 0.0000 1.0000\nKs 0.2000 0.2000 0.2000\nillum 9\nd 0.7500\nNs 60.0000\nsharpness 60.0000\n\nnewmtl white\nKa 0.4000 0.4000 0.4000\nKd 1.0000 1.0000 1.0000\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl red\nKa 0.4449 0.0000 0.0000\nKd 0.7714 0.0000 0.0000\nKs 0.8857 0.0000 0.0000\nillum 2\nNs 136.4300\n\nnewmtl blue_pure\nKa 0.0000 0.0000 0.5000\nKd 0.0000 0.0000 1.0000\nKs 0.0000 0.0000 0.5000\nillum 2\nNs 65.8900\n\nnewmtl lime\nKa 0.0000 0.5000 0.0000\nKd 0.0000 1.0000 0.0000\nKs 0.0000 0.5000 0.0000\nillum 2\nNs 65.8900\n\nnewmtl green\nKa 0.0000 0.2500 0.0000\nKd 0.0000 0.2500 0.0000\nKs 0.0000 0.2500 0.0000\nillum 2\nNs 65.8900\n\nnewmtl yellow\nKa 1.0000 0.6667 0.0000\nKd 1.0000 0.6667 0.0000\nKs 1.0000 0.6667 0.0000\nillum 2\nNs 65.8900\n\nnewmtl purple\nKa 0.5000 0.0000 1.0000\nKd 0.5000 0.0000 1.0000\nKs 0.5000 0.0000 1.0000\nillum 2\nNs 65.8900\n\nnewmtl orange\nKa 1.0000 0.1667 0.0000\nKd 1.0000 0.1667 0.0000\nKs 1.0000 0.1667 0.0000\nillum 2\nNs 65.8900\n\nnewmtl grey\nKa 0.5000 0.5000 0.5000\nKd 0.1837 0.1837 0.1837\nKs 0.5000 0.5000 0.5000\nillum 2\nNs 65.8900\n\nnewmtl rubber\nKa 0.0000 0.0000 0.0000\nKd 0.0100 0.0100 0.0100\nKs 0.1000 0.1000 0.1000\nillum 2\nNs 65.8900\n\nnewmtl flaqua\nKa 0.0000 0.4000 0.4000\nKd 0.0000 0.5000 0.5000\nillum 1\n\nnewmtl flblack\nKa 0.0000 0.0000 0.0000\nKd 0.0041 0.0041 0.0041\nillum 1\n\nnewmtl flblue_pure\nKa 0.0000 0.0000 0.5592\nKd 0.0000 0.0000 0.7102\nillum 1\n\nnewmtl flgrey\nKa 0.2163 0.2163 0.2163\nKd 0.5000 0.5000 0.5000\nillum 1\n\nnewmtl fllime\nKa 0.0000 0.3673 0.0000\nKd 0.0000 1.0000 0.0000\nillum 1\n\nnewmtl florange\nKa 0.6857 0.1143 0.0000\nKd 1.0000 0.1667 0.0000\nillum 1\n\nnewmtl flpurple\nKa 0.2368 0.0000 0.4735\nKd 0.3755 0.0000 0.7510\nillum 1\n\nnewmtl flred\nKa 0.4000 0.0000 0.0000\nKd 1.0000 0.0000 0.0000\nillum 1\n\nnewmtl flyellow\nKa 0.7388 0.4925 0.0000\nKd 1.0000 0.6667 0.0000\nillum 1\n\nnewmtl pink\nKa 0.9469 0.0078 0.2845\nKd 0.9878 0.1695 0.6702\nKs 0.7429 0.2972 0.2972\nillum 2\nNs 106.2000\n\nnewmtl flbrown\nKa 0.0571 0.0066 0.0011\nKd 0.1102 0.0120 0.0013\nillum 1\n\nnewmtl brown\nKa 0.1020 0.0185 0.0013\nKd 0.0857 0.0147 0.0000\nKs 0.1633 0.0240 0.0000\nillum 2\nNs 65.8900\n\nnewmtl glass\nKa 1.0000 1.0000 1.0000\nKd 0.4873 0.4919 0.5306\nKs 0.6406 0.6939 0.9020\nillum 2\nNs 200.0000\n\nnewmtl flesh\nKa 0.4612 0.3638 0.2993\nKd 0.5265 0.4127 0.3374\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl aqua\nKa 0.0000 0.4000 0.4000\nKd 0.0000 0.5000 0.5000\nKs 0.5673 0.5673 0.5673\nillum 2\nNs 60.0000\n\nnewmtl black\nKa 0.0000 0.0000 0.0000\nKd 0.0020 0.0020 0.0020\nKs 0.5184 0.5184 0.5184\nillum 2\nNs 157.3600\n\nnewmtl silver\nKa 0.9551 0.9551 0.9551\nKd 0.6163 0.6163 0.6163\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl dkblue_pure\nKa 0.0000 0.0000 0.0449\nKd 0.0000 0.0000 0.1347\nKs 0.0000 0.0000 0.5673\nillum 2\nNs 65.8900\n\nnewmtl fldkblue_pure\nKa 0.0000 0.0000 0.0449\nKd 0.0000 0.0000 0.1347\nillum 1\n\nnewmtl dkgreen\nKa 0.0000 0.0122 0.0000\nKd 0.0058 0.0245 0.0000\nKs 0.0000 0.0490 0.0000\nillum 2\nNs 60.0000\n\nnewmtl dkgrey\nKa 0.0490 0.0490 0.0490\nKd 0.0490 0.0490 0.0490\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl ltbrown\nKa 0.1306 0.0538 0.0250\nKd 0.2776 0.1143 0.0531\nKs 0.3000 0.1235 0.0574\nillum 2\nNs 60.0000\n\nnewmtl fldkgreen\nKa 0.0000 0.0122 0.0000\nKd 0.0058 0.0245 0.0000\nillum 1\n\nnewmtl flltbrown\nKa 0.1306 0.0538 0.0250\nKd 0.2776 0.1143 0.0531\nillum 1\n\nnewmtl tan\nKa 0.4000 0.3121 0.1202\nKd 0.6612 0.5221 0.2186\nKs 0.5020 0.4118 0.2152\nillum 2\nNs 60.0000\n\nnewmtl fltan\nKa 0.4000 0.3121 0.1202\nKd 0.6612 0.4567 0.1295\nillum 1\n\nnewmtl brzskin\nKa 0.4408 0.2694 0.1592\nKd 0.3796 0.2898 0.2122\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 25.0000\n\nnewmtl lips\nKa 0.4408 0.2694 0.1592\nKd 0.9265 0.2612 0.2898\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 25.0000\n\nnewmtl redorange\nKa 0.3918 0.0576 0.0000\nKd 0.7551 0.0185 0.0000\nKs 0.4694 0.3224 0.1667\nillum 2\nNs 132.5600\n\nnewmtl blutan\nKa 0.4408 0.2694 0.1592\nKd 0.0776 0.2571 0.2041\nKs 0.1467 0.1469 0.0965\nillum 2\nNs 25.0000\n\nnewmtl bluteal\nKa 0.0041 0.1123 0.1224\nKd 0.0776 0.2571 0.2041\nKs 0.1467 0.1469 0.0965\nillum 2\nNs 25.0000\n\nnewmtl pinktan\nKa 0.4408 0.2694 0.1592\nKd 0.6857 0.2571 0.2163\nKs 0.1467 0.1469 0.0965\nillum 2\nNs 25.0000\n\nnewmtl brnhair\nKa 0.0612 0.0174 0.0066\nKd 0.0898 0.0302 0.0110\nKs 0.1306 0.0819 0.0352\nillum 2\nNs 60.4700\n\nnewmtl blondhair\nKa 0.4449 0.2632 0.0509\nKd 0.5714 0.3283 0.0443\nKs 0.7755 0.4602 0.0918\nillum 2\nNs 4.6500\n\nnewmtl flblonde\nKa 0.4449 0.2632 0.0509\nKd 0.5714 0.3283 0.0443\nillum 1\n\nnewmtl yelloworng\nKa 0.5837 0.1715 0.0000\nKd 0.8857 0.2490 0.0000\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl bone\nKa 0.3061 0.1654 0.0650\nKd 0.9000 0.7626 0.4261\nKs 0.8939 0.7609 0.5509\nillum 2\nNs 200.0000\n\nnewmtl teeth\nKa 0.6408 0.5554 0.3845\nKd 0.9837 0.7959 0.4694\nillum 1\n\nnewmtl brass\nKa 0.2490 0.1102 0.0000\nKd 0.4776 0.1959 0.0000\nKs 0.5796 0.5796 0.5796\nillum 2\nNs 134.8800\n\nnewmtl dkred\nKa 0.0939 0.0000 0.0000\nKd 0.2286 0.0000 0.0000\nKs 0.2490 0.0000 0.0000\nillum 2\nNs 60.0000\n\nnewmtl taupe\nKa 0.1061 0.0709 0.0637\nKd 0.2041 0.1227 0.1058\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 84.5000\n\nnewmtl dkteal\nKa 0.0000 0.0245 0.0163\nKd 0.0000 0.0653 0.0449\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 55.0400\n\nnewmtl dkdkgrey\nKa 0.0000 0.0000 0.0000\nKd 0.0122 0.0122 0.0122\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl dkblue\nKa 0.0000 0.0029 0.0408\nKd 0.0000 0.0041 0.0571\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl gold\nKa 0.7224 0.1416 0.0000\nKd 1.0000 0.4898 0.0000\nKs 0.7184 0.3695 0.3695\nillum 2\nNs 123.2600\n\nnewmtl redbrick\nKa 0.1102 0.0067 0.0067\nKd 0.3306 0.0398 0.0081\nillum 1\n\nnewmtl flmustard\nKa 0.4245 0.2508 0.0000\nKd 0.8898 0.3531 0.0073\nillum 1\n\nnewmtl flpinegreen\nKa 0.0367 0.0612 0.0204\nKd 0.1061 0.2163 0.0857\nillum 1\n\nnewmtl fldkred\nKa 0.0939 0.0000 0.0000\nKd 0.2286 0.0082 0.0082\nillum 1\n\nnewmtl fldkgreen2\nKa 0.0025 0.0122 0.0014\nKd 0.0245 0.0694 0.0041\nillum 1\n\nnewmtl flmintgreen\nKa 0.0408 0.1429 0.0571\nKd 0.1306 0.2898 0.1673\nillum 1\n\nnewmtl olivegreen\nKa 0.0167 0.0245 0.0000\nKd 0.0250 0.0367 0.0000\nKs 0.2257 0.2776 0.1167\nillum 2\nNs 97.6700\n\nnewmtl skin\nKa 0.2286 0.0187 0.0187\nKd 0.1102 0.0328 0.0139\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 17.8300\n\nnewmtl redbrown\nKa 0.1469 0.0031 0.0000\nKd 0.2816 0.0060 0.0000\nKs 0.3714 0.3714 0.3714\nillum 2\nNs 141.0900\n\nnewmtl deepgreen\nKa 0.0000 0.0050 0.0000\nKd 0.0000 0.0204 0.0050\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 113.1800\n\nnewmtl flltolivegreen\nKa 0.0167 0.0245 0.0000\nKd 0.0393 0.0531 0.0100\nillum 1\n\nnewmtl jetflame\nKa 0.7714 0.0000 0.0000\nKd 0.9510 0.4939 0.0980\nKs 0.8531 0.5222 0.0000\nillum 2\nNs 132.5600\n\nnewmtl brownskn\nKa 0.0122 0.0041 0.0000\nKd 0.0204 0.0082 0.0000\nKs 0.0735 0.0508 0.0321\nillum 2\nNs 20.1600\n\nnewmtl greenskn\nKa 0.0816 0.0449 0.0000\nKd 0.0000 0.0735 0.0000\nKs 0.0490 0.1224 0.0898\nillum 3\nNs 46.5100\nsharpness 146.5100\n\nnewmtl ltgrey\nKa 0.5000 0.5000 0.5000\nKd 0.3837 0.3837 0.3837\nKs 0.5000 0.5000 0.5000\nillum 2\nNs 65.8900\n\nnewmtl bronze\nKa 0.0449 0.0204 0.0000\nKd 0.0653 0.0367 0.0122\nKs 0.0776 0.0408 0.0000\nillum 3\nNs 137.2100\nsharpness 125.5800\n\nnewmtl bone1\nKa 0.6408 0.5554 0.3845\nKd 0.9837 0.7959 0.4694\nillum 1\n\nnewmtl flwhite1\nKa 0.9306 0.9306 0.9306\nKd 1.0000 1.0000 1.0000\nillum 1\n\nnewmtl flwhite\nKa 0.6449 0.6116 0.5447\nKd 0.9837 0.9309 0.8392\nKs 0.8082 0.7290 0.5708\nillum 2\nNs 200.0000\n\nnewmtl shadow\nKd 0.0350 0.0248 0.0194\nillum 0\nd 0.7500\n\nnewmtl fldkolivegreen\nKa 0.0056 0.0082 0.0000\nKd 0.0151 0.0204 0.0038\nillum 1\n\nnewmtl fldkdkgrey\nKa 0.0000 0.0000 0.0000\nKd 0.0122 0.0122 0.0122\nillum 1\n\nnewmtl lcdgreen\nKa 0.4000 0.4000 0.4000\nKd 0.5878 1.0000 0.5061\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl brownlips\nKa 0.1143 0.0694 0.0245\nKd 0.1429 0.0653 0.0408\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 25.0000\n\nnewmtl muscle\nKa 0.2122 0.0077 0.0154\nKd 0.4204 0.0721 0.0856\nKs 0.1184 0.1184 0.1184\nillum 2\nNs 25.5800\n\nnewmtl flltgrey\nKa 0.5224 0.5224 0.5224\nKd 0.8245 0.8245 0.8245\nillum 1\n\nnewmtl offwhite.warm\nKa 0.5184 0.4501 0.3703\nKd 0.8367 0.6898 0.4490\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl offwhite.cool\nKa 0.5184 0.4501 0.3703\nKd 0.8367 0.6812 0.5703\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl yellowbrt\nKa 0.4000 0.4000 0.4000\nKd 1.0000 0.7837 0.0000\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl chappie\nKa 0.4000 0.4000 0.4000\nKd 0.5837 0.1796 0.0367\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 60.0000\n\nnewmtl archwhite\nKa 0.2816 0.2816 0.2816\nKd 0.9959 0.9959 0.9959\nillum 1\n\nnewmtl archwhite2\nKa 0.2816 0.2816 0.2816\nKd 0.8408 0.8408 0.8408\nillum 1\n\nnewmtl lighttan\nKa 0.0980 0.0536 0.0220\nKd 0.7020 0.4210 0.2206\nKs 0.8286 0.8057 0.5851\nillum 2\nNs 177.5200\n\nnewmtl lighttan2\nKa 0.0980 0.0492 0.0144\nKd 0.3143 0.1870 0.0962\nKs 0.8286 0.8057 0.5851\nillum 2\nNs 177.5200\n\nnewmtl lighttan3\nKa 0.0980 0.0492 0.0144\nKd 0.1796 0.0829 0.0139\nKs 0.8286 0.8057 0.5851\nillum 2\nNs 177.5200\n\nnewmtl lightyellow\nKa 0.5061 0.1983 0.0000\nKd 1.0000 0.9542 0.3388\nKs 1.0000 0.9060 0.0000\nillum 2\nNs 177.5200\n\nnewmtl lighttannew\nKa 0.0980 0.0492 0.0144\nKd 0.7878 0.6070 0.3216\nKs 0.8286 0.8057 0.5851\nillum 2\nNs 177.5200\n\nnewmtl default\nKa 0.4000 0.4000 0.4000\nKd 0.7102 0.7020 0.6531\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 128.0000\n\nnewmtl ship2\nKa 0.0000 0.0000 0.0000\nKd 1.0000 1.0000 1.0000\nKs 0.1143 0.1143 0.1143\nillum 2\nNs 60.0000\n\nnewmtl dkpurple\nKa 0.0082 0.0000 0.0163\nKd 0.0245 0.0000 0.0490\nKs 0.1266 0.0000 0.2531\nillum 2\nNs 65.8900\n\nnewmtl dkorange\nKa 0.4041 0.0123 0.0000\nKd 0.7143 0.0350 0.0000\nKs 0.7102 0.0870 0.0000\nillum 2\nNs 65.8900\n\nnewmtl mintgrn\nKa 0.0101 0.1959 0.0335\nKd 0.0245 0.4776 0.0816\nKs 0.0245 0.4776 0.0816\nillum 2\nNs 65.8900\n\nnewmtl fgreen\nKa 0.0000 0.0449 0.0000\nKd 0.0000 0.0449 0.0004\nKs 0.0062 0.0694 0.0000\nillum 2\nNs 106.2000\n\nnewmtl glassblutint\nKa 0.4000 0.4000 0.4000\nKd 0.5551 0.8000 0.7730\nKs 0.7969 0.9714 0.9223\nillum 4\nd 0.3300\nNs 60.0000\nsharpness 60.0000\n\nnewmtl bflesh\nKa 0.0122 0.0122 0.0122\nKd 0.0245 0.0081 0.0021\nKs 0.0531 0.0460 0.0153\nillum 2\nNs 20.1600\n\nnewmtl meh\nKa 0.4000 0.4000 0.4000\nKd 0.5551 0.8000 0.7730\nKs 0.7969 0.9714 0.9223\nillum 4\nd 0.7500\nNs 183.7200\nsharpness 60.0000\n\nnewmtl violet\nKa 0.0083 0.0000 0.1265\nKd 0.0287 0.0269 0.1347\nKs 0.2267 0.4537 0.6612\nillum 2\nNs 96.9000\n\nnewmtl iris\nKa 0.3061 0.0556 0.0037\nKd 0.0000 0.0572 0.3184\nKs 0.8041 0.6782 0.1477\nillum 2\nNs 188.3700\n\nnewmtl blugrn\nKa 0.4408 0.4144 0.1592\nKd 0.0811 0.6408 0.2775\nKs 0.1467 0.1469 0.0965\nillum 2\nNs 25.0000\n\nnewmtl glasstransparent\nKa 0.2163 0.2163 0.2163\nKd 0.4694 0.4694 0.4694\nKs 0.6082 0.6082 0.6082\nillum 4\nd 0.7500\nNs 200.0000\nsharpness 60.0000\n\nnewmtl fleshtransparent\nKa 0.4000 0.2253 0.2253\nKd 0.6898 0.2942 0.1295\nKs 0.7388 0.4614 0.4614\nillum 4\nd 0.7500\nNs 6.2000\nsharpness 60.0000\n\nnewmtl fldkgrey\nKa 0.0449 0.0449 0.0449\nKd 0.0939 0.0939 0.0939\nillum 1\n\nnewmtl sky_blue\nKa 0.1363 0.2264 0.4122\nKd 0.1241 0.5931 0.8000\nKs 0.0490 0.0490 0.0490\nillum 2\nNs 13.9500\n\nnewmtl fldkpurple\nKa 0.0443 0.0257 0.0776\nKd 0.1612 0.0000 0.3347\nKs 0.0000 0.0000 0.0000\nillum 2\nNs 13.9500\n\nnewmtl dkbrown\nKa 0.0143 0.0062 0.0027\nKd 0.0087 0.0038 0.0016\nKs 0.2370 0.2147 0.1821\nillum 3\nNs 60.0000\nsharpness 60.0000\n\nnewmtl bone2\nKa 0.6408 0.5388 0.3348\nKd 0.9837 0.8620 0.6504\nillum 1\n\nnewmtl bluegrey\nKa 0.4000 0.4000 0.4000\nKd 0.1881 0.2786 0.2898\nKs 0.3000 0.3000 0.3000\nillum 2\nNs 14.7300\n\nnewmtl metal\nKa 0.9102 0.8956 0.1932\nKd 0.9000 0.7626 0.4261\nKs 0.8939 0.8840 0.8683\nillum 2\nNs 200.0000\n\nnewmtl sand_stone\nKa 0.1299 0.1177 0.0998\nKd 0.1256 0.1138 0.0965\nKs 0.2370 0.2147 0.1821\nillum 3\nNs 60.0000\nsharpness 60.0000\n\nnewmtl hair\nKa 0.0013 0.0012 0.0010\nKd 0.0008 0.0007 0.0006\nKs 0.0000 0.0000 0.0000\nillum 3\nNs 60.0000\nsharpness 60.0000\n");
        ObjectFileParser objectfileparser = new ObjectFileParser(stringreader);
        materials = new HashMap(50);
        readFile(objectfileparser);
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        return (i & 0xa0) == 0;
    }

    private static final int DEBUG = 0;
    private String curName;
    private ObjectFileMaterial cur;
    private HashMap materials;
    private String basePath;
    private boolean fromUrl;
}
