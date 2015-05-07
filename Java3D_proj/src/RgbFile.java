// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 7/7/2006 1:25:55 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 



import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;

class RgbFile extends BufferedInputStream
{

    short getShort()
        throws IOException
    {
        short word0 = (short)read();
        if(word0 == -1)
            throw new IOException("Unexpected EOF");
        short word1 = (short)read();
        if(word1 == -1)
            throw new IOException("Unexpected EOF");
        else
            return (short)(word0 << 8 | word1);
    }

    byte getByte()
        throws IOException
    {
        int i = read();
        if(i == -1)
            throw new IOException("Unexpected EOF");
        else
            return (byte)i;
    }

    int getInt()
        throws IOException
    {
        int i = 0;
        for(int j = 0; j < 4; j++)
        {
            int k = read();
            if(k == -1)
                throw new IOException("Unexpected EOF");
            i = i << 8 | k;
        }

        return i;
    }

    public BufferedImage getImage()
        throws IOException
    {
        short word0 = getShort();
        if(word0 != 474)
            throw new IOException("Unrecognized file format.");
        byte byte0 = getByte();
        if(byte0 != 0)
            throw new IOException("RLE Compressed files not supported");
        byte byte1 = getByte();
        dimension = getShort();
        xSize = getShort();
        ySize = getShort();
        zSize = getShort();
        int i = getInt();
        int j = getInt();
        skip(84L);
        int k = getInt();
        if(i != 0 || j != 255 || k != 0 || byte1 != 1)
            throw new IOException("Unsupported options in file");
        skip(404L);
        ComponentColorModel componentcolormodel = null;
        if(zSize == 1)
        {
            ColorSpace colorspace = ColorSpace.getInstance(1003);
            int ai[] = {
                8
            };
            componentcolormodel = new ComponentColorModel(colorspace, ai, false, false, 1, 0);
        } else
        if(zSize == 2)
        {
            ColorSpace colorspace1 = ColorSpace.getInstance(1003);
            int ai1[] = {
                8, 8
            };
            componentcolormodel = new ComponentColorModel(colorspace1, ai1, true, false, 3, 0);
        } else
        if(zSize == 3)
        {
            ColorSpace colorspace2 = ColorSpace.getInstance(1000);
            int ai2[] = {
                8, 8, 8
            };
            componentcolormodel = new ComponentColorModel(colorspace2, ai2, false, false, 1, 0);
        } else
        if(zSize == 4)
        {
            ColorSpace colorspace3 = ColorSpace.getInstance(1000);
            int ai3[] = {
                8, 8, 8, 8
            };
            componentcolormodel = new ComponentColorModel(colorspace3, ai3, true, false, 3, 0);
        } else
        {
            throw new IOException("Unsupported options in file");
        }
        WritableRaster writableraster = componentcolormodel.createCompatibleWritableRaster(xSize, ySize);
        BufferedImage bufferedimage = new BufferedImage(componentcolormodel, writableraster, false, null);
        byte abyte0[] = ((DataBufferByte)writableraster.getDataBuffer()).getData();
        for(short word1 = 0; word1 < zSize; word1++)
        {
            for(int i1 = ySize - 1; i1 >= 0; i1--)
            {
                for(short word2 = 0; word2 < xSize; word2++)
                {
                    int l = read();
                    if(l == -1)
                        throw new IOException("Unexpected EOF");
                    abyte0[i1 * (xSize * zSize) + word2 * zSize + word1] = (byte)l;
                }

            }

        }

        return bufferedimage;
    }

    public RgbFile(InputStream inputstream)
    {
        super(inputstream);
    }

    short dimension;
    short xSize;
    short ySize;
    short zSize;
    String filename;
    private static final int DEBUG = 0;
}
