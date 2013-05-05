/*
 * JavaPE: a simple Java PE reader
 * Copyright (C) 2013 Qingbao Guo <qingbao.guo@hig.no>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package PEfile;

import java.util.*;
import java.nio.*;
import java.awt.*;
import java.awt.image.*;

/**
 * Icon in the resource section of a PE?
 */
public class ResIcon implements BinaryRecord {
    private long location;

    public long size;            /* size of this header in bytes DWORD*/

    public long width;           /* Image width in pixels LONG*/

    public long height;          /* Image height in pixels LONG*/

    public int planes;          /* Number of color planes WORD*/

    public int bitsPerPixel;    /* Number of bits per pixel WORD*/
    /* Fields added for Windows 3.x follow this line */

    public long compression;     /* compression methods used DWORD*/

    public long sizeOfBitmap;    /* size of bitmap in bytes DWORD*/

    public long horzResolution;  /* Horizontal resolution in pixels per meter LONG*/

    public long vertResolution;  /* Vertical resolution in pixels per meter LONG*/

    public long colorsUsed;      /* Number of colors in the image DWORD*/

    public long colorsImportant; /* Minimum number of important colors DWORD*/

    public PaletteElement[] palette;
    public short[] bitmapXOR;
    public short[] bitmapAND;

    @Override
    public long getLocation() {
        return location;
    }

    @Override
    public void setLocation(long location) {
        this.location = location;
    }

    @Override
    public void setData(ByteBuffer in) {
        size = in.getInt();
        width = in.getInt();
        height = in.getInt();
        planes = in.getShort();
        bitsPerPixel = in.getShort();
        compression = in.getInt();
        sizeOfBitmap = in.getInt();
        horzResolution = in.getInt();
        vertResolution = in.getInt();
        colorsUsed = in.getInt();
        colorsImportant = in.getInt();

        int cols = (int) colorsUsed;
        if (cols == 0) {
            cols = 1 << bitsPerPixel;
        }

        palette = new PaletteElement[cols];
        for (int i = 0; i < palette.length; i++) {
            PaletteElement el = new PaletteElement();
            el.Blue = in.get();
            el.Green = in.get();
            el.Red = in.get();
            el.Reserved = in.get();
            palette[i] = el;
        }

        // int xorbytes = (((int)height/2) * (int)width * (int)bitsPerPixel) / 8;
        int xorbytes = (((int) height / 2) * (int) width);
        //		System.out.println("POSITION " + in.position() + " : xorbitmap = " + xorbytes + " bytes");

        bitmapXOR = new short[xorbytes];
        for (int i = 0; i < bitmapXOR.length; i++) {
            switch (bitsPerPixel) {
                case 4: {
                    int pix = in.get();
                    bitmapXOR[i] = (short) ((pix >> 4) & 0x0F);
                    i++;
                    bitmapXOR[i] = (short) (pix & 0x0F);
                }
                break;
                case 8: {
                    bitmapXOR[i] = in.get();
                }
                break;
            }
        }


        int height = (int) (this.height / 2);
        int rowsize = (int) width / 8;
        if ((rowsize % 4) > 0) {
            rowsize += 4 - (rowsize % 4);
        }

        //		System.out.println("POSITION " + in.position() + " : andbitmap = " + andbytes + " bytes");

        int andbytes = height * rowsize;   // (((int)height/2) * (int)width) / 8;

        bitmapAND = new short[andbytes];
        for (int i = 0; i < bitmapAND.length; i++) {
            bitmapAND[i] = in.get();
        }
    }

    public class PaletteElement {

        public int Blue;
        public int Green;
        public int Red;
        public int Reserved;

        @Override
        public String toString() {
            return "{" + Blue + "," + Green + "," + Red + "," + Reserved + "}";
        }
    }

    /** Creates a new instance based on the data of the Image argument.
     * @param img
     * @throws Exception ?
     */
    public ResIcon(Image img) throws Exception {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        if ((width % 8) != 0) {
            width += (7 - (width % 8));
        }

        if ((height % 8) != 0) {
            height += (7 - (height % 8));
        }

        //		System.out.println("FOUND WIDTH " + width + " (was " + img.getWidth(null) + ")");
        //		System.out.println("FOUND HEIGHT " + height + " (was " + img.getHeight(null) + ")");

        //	System.out.println("RESICON...");
        if (img instanceof BufferedImage) {
            BufferedImage result = (BufferedImage) img;

            for (int y = 0; y < result.getHeight(); y++) {
                for (int x = 0; x < result.getWidth(); x++) {
                    int rgb = result.getRGB(x, y);
                    if (((rgb >> 24) & 0xFF) > 0) {
                        //					System.out.print(".");
                    }
                    //				else
                    //				    System.out.print("*");
                }
                //			System.out.println("");
            }

        }

        int[] pixelbuffer = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(img, 0, 0, width, height,
                pixelbuffer, 0, width);
        try {
            grabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            throw new Exception("Can't load the image provided", e);
        }

        Hashtable colors = calculateColorCount(pixelbuffer);

        // FORCE ALWAYS to 8
        this.bitsPerPixel = 8;

        palette = new ResIcon.PaletteElement[1 << bitsPerPixel];
        //	System.out.println("Creating palette of " + palette.length + " colors (" + colors.size() + ")");
        for (Enumeration e = colors.keys(); e.hasMoreElements();) {
            Integer pixi = (Integer) e.nextElement();
            int pix = pixi.intValue();
            int index = ((Integer) colors.get(pixi)).intValue();
            //		System.out.println("set pixel " + index);

            palette[index] = new ResIcon.PaletteElement();
            palette[index].Blue = pix & 0xFF;
            palette[index].Green = (pix >> 8) & 0xff;
            palette[index].Red = (pix >> 16) & 0xff;
        }
        for (int i = 0; i < palette.length; i++) {
            if (palette[i] == null) {
                palette[i] = new ResIcon.PaletteElement();
            }
        }


        this.size = 40;
        this.width = width;
        this.height = height * 2;
        this.planes = 1;
        this.compression = 0;

        this.sizeOfBitmap = 0;
        this.horzResolution = 0;
        this.vertResolution = 0;

        this.colorsUsed = 0;
        this.colorsImportant = 0;

        //
        // We calculate the rowsize in bytes. It seems that it must be
        // aligned on a double word, although none of the
        // documentation I have on the icon format states so.
        //
        int rowsize = width / 8;
        if ((rowsize % 4) > 0) {
            rowsize += 4 - (rowsize % 4);
        }

        bitmapXOR = new short[(((int) this.height / 2) * (int) this.width
                * bitsPerPixel) / 8];
        bitmapAND = new short[((int) this.height / 2) * rowsize];

        int bxl = bitmapXOR.length - 1;
        int bal = bitmapAND.length - 1;

        for (int i = 0; i < pixelbuffer.length; i++) {
            int col = i % width;
            int line = i / width;

            bxl = (width * height) - (((i / width) + 1) * width) + (i % width);
            //		bal = ((width * height)/8) - ((line+1)*(width/8)) + (col/8);
            bal = (rowsize * height) - ((line + 1) * (rowsize)) + (col / 8);

            // if ((pixelbuffer[i] & 0xFF000000) != 0x00000000)

            //
            // If the color is transparent, any color will suit
            // (as it is not supposed to be displayed)
            //
            if ((((pixelbuffer[i] >> 24) & 0xFF) == 0)) {
                bitmapAND[bal] |= 1 << (7 - (i % 8));
                bitmapXOR[bxl] = 0xFF; // (short)getBrightest(); FF

                // 				int pixel = pixelbuffer[i] & 0x00FFFFFF;
                // 				pixel = 0x000000;
                // 				Integer icol = (Integer)colors.get(new Integer(pixel));
                // 				if (icol != null)
                // 				{
                // 					int palindex = icol.intValue();
                // 					bitmapXOR[bxl] = (short)palindex;
                // 				}
                // 				else
                // 				{
                // 				    bitmapXOR[bxl] = 0; // (short)getBrightest();
                // 				    System.out.println("Can't find TRANSP BLACK COL " + icol );
                // 				}
            } else {
                int pixel = pixelbuffer[i] & 0x00FFFFFF;
                // pixel = 0x000000;
                Integer icol = (Integer) colors.get(new Integer(pixel));
                if (icol != null) {
                    int palindex = icol.intValue();
                    bitmapXOR[bxl] = (short) palindex;
                }
            }
        }
    }

    private int getBrightest() {
        int result = 0;
        int averesult = 0;
        for (int i = 0; i < palette.length; i++) {
            int ave1 = (palette[0].Red + palette[0].Green + palette[0].Blue) / 3;
            if (ave1 > averesult) {
                averesult = ave1;
                result = i;
            }
        }
        return result;
    }

    private Hashtable<Integer, Integer> calculateColorCount(int[] pixels) {
        Hashtable<Integer, Integer> result =
                new Hashtable<Integer, Integer>();
        int colorindex = 0;
        for (int i = 0; i < pixels.length; i++) {
            int pix = pixels[i];
            if (((pix >> 24) & 0xFF) > 0) {
                pix &= 0x00FFFFFF;
                Integer pixi = new Integer(pix);
                Object o = result.get(pixi);
                if (o == null) {
                    result.put(pixi, new Integer(colorindex++));
                }
                //			if (colorindex > 256)
                //			    return result;
            }
        }
        return result;
    }

    /**
     * Creates and returns a ByteBuffer containing an image under
     * the .ico format expected by Windows.
     *
     * @return a ByteBuffer with the .ico data
     */
    @Override
    public ByteBuffer getData() {
        int cols = (int) colorsUsed;
        if (cols == 0) {
            cols = 1 << bitsPerPixel;
        }

        int rowsize = (int) width / 8;
        if ((rowsize % 4) > 0) {
            rowsize += 4 - (rowsize % 4);
        }

        ByteBuffer buf = ByteBuffer.allocate((int) (40 + (cols * 4) + (width * (height
                / 2) * bitsPerPixel) / 8 + (rowsize * (height / 2))));
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.position(0);

        buf.putInt((int) size);
        buf.putInt((int) width);
        buf.putInt((int) height);
        buf.putShort((short) planes);
        buf.putShort((short) bitsPerPixel);
        buf.putInt((int) compression);
        buf.putInt((int) sizeOfBitmap);
        buf.putInt((int) horzResolution);
        buf.putInt((int) vertResolution);
        buf.putInt((int) colorsUsed);
        buf.putInt((int) colorsImportant);

        //		System.out.println("GET DATA :: palette.size= "+palette.length + " // position=" + buf.position());
        for (int i = 0; i < palette.length; i++) {
            PaletteElement el = palette[i];
            buf.put((byte) el.Blue);
            buf.put((byte) el.Green);
            buf.put((byte) el.Red);
            buf.put((byte) el.Reserved);
        }

        switch (bitsPerPixel) {
            case 4: {
                for (int i = 0; i < bitmapXOR.length; i += 2) {
                    int v1 = bitmapXOR[i];
                    int v2 = bitmapXOR[i + 1];
                    buf.put((byte) ((v1 << 4) | v2));
                }
            }
            break;

            case 8: {
                //				System.out.println("GET DATA :: XORBitmap.size= "+bitmapXOR.length + " // position=" + buf.position());
                for (int i = 0; i < bitmapXOR.length; i++) {
                    buf.put((byte) bitmapXOR[i]);
                }
            }
            break;

            default:
                throw new RuntimeException("BitRes " + bitsPerPixel
                        + " not supported!");
        }

        //		System.out.println("GET DATA :: AndBitmap.size= "+bitmapAND.length + " // position=" + buf.position());
        for (int i = 0; i < bitmapAND.length; i++) {
            buf.put((byte) bitmapAND[i]);
        }

        //		System.out.println("GET DATA END AT " + buf.position());
        buf.position(0);
        return buf;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append("Size: ").append(size);
        out.append("\nWidth: ").append(width);
        out.append("\nHeight: ").append(height);
        out.append("\nPlanes: ").append(planes);
        out.append("\nBitsPerPixel: ").append(bitsPerPixel);
        out.append("\nCompression: ").append(compression);
        out.append("\nSizeOfBitmap: ").append(sizeOfBitmap);
        out.append("\nHorzResolution: ").append(horzResolution);
        out.append("\nVertResolution: ").append(vertResolution);
        out.append("\nColorsUsed: ").append(colorsUsed);
        out.append("\nColorsImportant: ").append(colorsImportant);

        //		for (int i = 0; i<palette.length; i++)
        //		{
        //			out.append("\n");
        //			out.append(palette[i].toString());
        //		}
        out.append("\nBitmapXOR[").append(bitmapXOR.length).append("]={");
        for (int i = 0; i < bitmapXOR.length; i++) {
            out.append((byte) bitmapXOR[i]);
        }
        out.append("}\nBitmapAnd[").append(bitmapAND.length).append("]={");
        for (int i = 0; i < bitmapAND.length; i++) {
            out.append((byte) bitmapAND[i]);
        }

        return out.toString();
    }
}
