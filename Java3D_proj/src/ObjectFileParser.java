// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 7/7/2006 1:25:55 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 


import com.sun.j3d.loaders.ParsingErrorException;
import java.io.*;

class ObjectFileParser extends StreamTokenizer
{

    void setup()
    {
        resetSyntax();
        eolIsSignificant(true);
        lowerCaseMode(true);
        wordChars(33, 126);
        commentChar(33);
        whitespaceChars(32, 32);
        whitespaceChars(10, 10);
        whitespaceChars(13, 13);
        whitespaceChars(9, 9);
        ordinaryChar(35);
        ordinaryChar(47);
        ordinaryChar(92);
    }

    void getToken()
        throws ParsingErrorException
    {
        boolean flag = false;
        try
        {
            do
            {
                int i = nextToken();
                if(i == 92)
                {
                    int j = nextToken();
                    if(ttype != 10)
                        flag = true;
                } else
                {
                    flag = true;
                }
            } while(!flag);
        }
        catch(IOException ioexception)
        {
            throw new ParsingErrorException("IO error on line " + lineno() + ": " + ioexception.getMessage());
        }
    }

    void printToken()
    {
        switch(ttype)
        {
        case 10: // '\n'
            System.out.println("Token EOL");
            break;

        case -1: 
            System.out.println("Token EOF");
            break;

        case -3: 
            System.out.println("Token TT_WORD: " + sval);
            break;

        case 47: // '/'
            System.out.println("Token /");
            break;

        case 92: // '\\'
            System.out.println("Token \\");
            break;

        case 35: // '#'
            System.out.println("Token #");
            break;
        }
    }

    void skipToNextLine()
        throws ParsingErrorException
    {
        while(ttype != 10) 
            getToken();
    }

    void getNumber()
        throws ParsingErrorException
    {
        try
        {
            getToken();
            if(ttype != -3)
                throw new ParsingErrorException("Expected number on line " + lineno());
            nval = Double.valueOf(sval).doubleValue();
        }
        catch(NumberFormatException numberformatexception)
        {
            throw new ParsingErrorException(numberformatexception.getMessage());
        }
    }

    ObjectFileParser(Reader reader)
    {
        super(reader);
        setup();
    }

    private static final char BACKSLASH = 92;
}
