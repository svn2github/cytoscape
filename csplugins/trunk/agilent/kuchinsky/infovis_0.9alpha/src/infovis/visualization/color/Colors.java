/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 * Class Colors
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class Colors {
    public static ColorSpace sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    public static ColorSpace GRAY= ColorSpace.getInstance(ColorSpace.CS_GRAY);
    private static float[] triple = new float[3];
    public static float UprimeWhite;
    public static float VprimeWhite;
    public static Colors instance;
    public static float[] XYZwhite;
    
    public static float[] RGBtoXYZ(float[] rgb) {

        return sRGB.toCIEXYZ(rgb);
    }
    public static float[] XYZtoRGB(float[] xyz) {
        return sRGB.fromCIEXYZ(xyz);
    }
    
    public static float getLuminance(Color c) {
        if (c == null) return 1;
        float[] g = GRAY.fromRGB(c.getRGBColorComponents(triple));
        return g[0];
    }
//    
//    /**
//     * Converts XYZ to L*u*v*
//     * 
//     * @param xyz the values in XYZ colorspace
//     * @return the values in L*u*v* colorspace
//     */
//    public static float[] XYZtoLuv(float[] xyz) {
//        float uprime = XYZtoUprime(xyz);
//        float vprime = XYZtoVprime(xyz);
//        float Lstar = YtoL(xyz[1]);        
//        float ustar = 13*Lstar*(uprime-UprimeWhite);
//        float vstar = 13*Lstar*(vprime-VprimeWhite);        
//        float[] ret = new float[3];
//        
//        ret[0] = Lstar;
//        ret[1] = ustar;
//        ret[2] = vstar;
//        return ret;
//    }
    
    static final double LABCST = 7.787037037037037037037037037037;

    private static double labF(double t) {
        if (t > 0.008856) {
            return Math.pow(t, 1.0/3);
        }
        else {
            return LABCST * t + 16/116;
        }
    }
    
    private static double labF_1(double t) {
        if (t <= ((LABCST*0.008856) + (16./116.))) {
            return Math.max(0, (t - (16./116.)) / LABCST);
        }
        return t * t * t;
    }
    /**
    * Converts from La*b* to XYZ.
    * 
    * @param Lab the La*b* values.
    * 
    * @return the XYZ values. Reuses the array.
    */
    public static float[] LabtoXYZ(float[] Lab) {
        float L = Lab[0];
        float a = Lab[1];
        float b = Lab[2];
        float X,Y,Z;
        
        if (L <= 0) {
            X = Y = Z = 0;
        }
        else {
            Y = (L + 16) / 116;
            X = Y + a/500;
            Z = Y - b/200;
        }
        Lab[0] = (float)labF_1(X);
        Lab[1] = (float)labF_1(Y);
        Lab[2] = (float)labF_1(Z);
        return Lab;
    }
    /**
    * Converts from XYZ to La*b*.
    * 
    * @param xyz the XYZ parameters
    * 
    * @return the La*b* parameters in the passed array.
    */
    public static float[] XYZtoLab(float[] xyz) {
        float X = xyz[0];
        float Y = xyz[1];
        float Z = xyz[2];
        
        if (X == 0 && Y == 0 && Z == 0) {
            return xyz;
        }

        double labFX = labF(X);
        double labFY = labF(Y);
        double labFZ = labF(Z);
        
        double L;
        if(Y > 0.008856f) {
            L = (float)(116 * labFY - 16); 
        }
        else {
            L = 903.3f * Y;
        }

        double a = 500 * (labFX - labFY);
        double b = 200 * (labFY - labFZ);

        xyz[0] = (float)L;
        xyz[1] = (float)a;
        xyz[2] = (float)b;
        return xyz;
    }
    
    /**
     * Convert from LCH to La*b* colorspace.
     * 
     * @param lch 
     * @return the La*b* values from the LCH colorspace. 
     */
    public static float[] LCHtoLab(float lch[]) {
        double C = lch[1];
        double H = lch[2];
        double tanH = Math.tan((H*Math.PI) / 180);
        
        while (H > 360) {
            H -= 360;
        }
        while (H < 0) {
            H += 360;
        }
        double a, b;
        if (H < 90 || H > 270) {
            a = C / Math.sqrt(tanH*tanH + 1);
        }
        else {
            a = -C / Math.sqrt(tanH*tanH + 1);
        }
        
        if (H < 180) {
            b = Math.sqrt(C*C-a*a);
        }
        else {
            b = -Math.sqrt(C*C-a*a);
        }
        lch[1] = (float)a;
        lch[2] = (float)b;
        return lch;
    }
    
    public static float[] LabtoLCH(float Lab[]) {
        float a = Lab[1];
        float b = Lab[2];
        double C = Math.sqrt(a*a + b*b);
        double H;
        if (a == 0) {
            H = 0;
        }
        else {
            H = Math.atan2(b, a);
        }
        H *= 180 / Math.PI;
        
        while (H >= 360) {
            H -= 360;
        }
        while (H < 0) {
            H += 360;
        }
        Lab[1] = (float)C;
        Lab[2] = (float)H;
        return Lab;
    }
    
    public static float[] LCHtoRGB(float[] comp) {
        LCHtoLab(comp);
        LabtoXYZ(comp);
        XYZtoRGB(comp);
        return comp;
    }
    
    public static int LCHtoRGB(float l, float c, float h) {
        float[] comp = { l, c, h };
        LCHtoRGB(comp);
        return comptoRGB(comp[0], comp[1], comp[2]);
    }
    
    public static int comptoRGB(float r, float g, float b) {
        return comptoRGB(
                (int)(r*255+0.5),
                (int)(g*255+0.5), 
                (int)(b*255+0.5));        
    }

    public static int comptoRGB(int r, int g, int b) {
        return 0xFF000000 
        | ((r & 0xFF) << 16) 
        | ((g & 0xFF) << 8)  
        | ((b & 0xFF) << 0);
        
    }
}
