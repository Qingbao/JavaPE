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

import java.io.*;
import java.awt.image.*;

/**
 * Codec for .ico files.
 */
public class IcoCodec {
    static public class IconDir {
        int idType;
        int idCount;

        public IconDir(BinaryInputStream in) throws IOException {
            in.readUShortLE();
            idType = in.readUShortLE();
            idCount = in.readUShortLE();
        }

        @Override
        public String toString() {
            return "{ idType=" + idType + ", idCount=" + idCount + " }";
        }
    }

    static public class IconEntry {
        short width;
        short height;
        short colorCount;
        short reserved;
        int planes;
        int bitCount;
        long bytesInRes;
        long imageOffset;

        public IconEntry(BinaryInputStream in) throws IOException {
            width = in.readUByte();
            height = in.readUByte();
            colorCount = in.readUByte();
            reserved = in.readUByte();
            planes = in.readUShortLE();
            bitCount = in.readUShortLE();
            bytesInRes = in.readUIntLE();
            imageOffset = in.readUIntLE();
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("{ bWidth=").append(width).append("\n");
            buffer.append("  bHeight=").append(height).append("\n");
            buffer.append("  bColorCount=").append(colorCount).append("\n");
            buffer.append("  wPlanes=").append(planes).append("\n");
            buffer.append("  wBitCount=").append(bitCount).append("\n");
            buffer.append("  dwBytesInRes=").append(bytesInRes).append("\n");
            buffer.append("  dwImageOffset=").append(imageOffset).append("\n");
            buffer.append("}");

            return buffer.toString();
        }
    }

    static public class IconHeader {
        public long size;            /* size of this header in bytes DWORD 0*/

        public long width;           /* Image width in pixels LONG 4*/

        public long height;          /* Image height in pixels LONG 8*/

        public int planes;          /* Number of color planes WORD 12 */

        public int bitsPerPixel;    /* Number of bits per pixel WORD 14 */
        /* Fields added for Windows 3.x follow this line */

        public long compression;     /* compression methods used DWORD 16 */

        public long sizeOfBitmap;    /* size of bitmap in bytes DWORD 20 */

        public long horzResolution;  /* Horizontal resolution in pixels per meter LONG 24 */

        public long vertResolution;  /* Vertical resolution in pixels per meter LONG 28*/

        public long colorsUsed;      /* Number of colors in the image DWORD 32 */

        public long colorsImportant; /* Minimum number of important colors DWORD 36 */


        public IconHeader(BinaryInputStream in) throws IOException {
            size = in.readUIntLE();
            width = in.readUIntLE();
            height = in.readUIntLE();
            planes = in.readUShortLE();
            bitsPerPixel = in.readUShortLE();
            compression = in.readUIntLE();
            sizeOfBitmap = in.readUIntLE();
            horzResolution = in.readUIntLE();
            vertResolution = in.readUIntLE();
            colorsUsed = in.readUIntLE();
            colorsImportant = in.readUIntLE();
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("Size=");
            buffer.append(size);
            buffer.append("\nWidth=");
            buffer.append(width);
            buffer.append("\nHeight=");
            buffer.append(height);
            buffer.append("\nPlanes=");
            buffer.append(planes);
            buffer.append("\nBitsPerPixel=");
            buffer.append(bitsPerPixel);
            buffer.append("\nCompression=");
            buffer.append(compression);
            buffer.append("\nSizeOfBitmap=");
            buffer.append(sizeOfBitmap);
            buffer.append("\nHorzResolution=");
            buffer.append(horzResolution);
            buffer.append("\nVertResolution=");
            buffer.append(vertResolution);
            buffer.append("\nColorsUsed=");
            buffer.append(colorsUsed);
            buffer.append("\nColorsImportant=");
            buffer.append(colorsImportant);

            return buffer.toString();
        }
    }

    static public BufferedImage[] loadImages(File f) throws IOException {
        InputStream istream = new FileInputStream(f);
        BufferedInputStream buffin = new BufferedInputStream(istream);
        BinaryInputStream in = new BinaryInputStream(buffin);

        try {
            in.mark(32000);

            IconDir dir = new IconDir(in);
            //	    System.out.println("DIR = " + dir);

            IconEntry[] entries = new IconEntry[dir.idCount];
            BufferedImage[] images = new BufferedImage[dir.idCount];

            for (int i = 0; i < dir.idCount; i++) {
                entries[i] = new IconEntry(in);
                //		    System.out.println("ENTRY " + i + " = " + entries[i]);
            }

            IconEntry entry = entries[0];
            //	    System.out.println("ENTRYx = " + entry);

            for (int i = 0; i < dir.idCount; i++) {
                in.reset();
                in.skip(entries[i].imageOffset);

                IconHeader header = new IconHeader(in);
                //		    System.out.println("Header: " + header);

                long toskip = header.size - 40;
                if (toskip > 0) {
                    in.skip((int) toskip);
                }

                //		    System.out.println("skipped data");

                BufferedImage image = new BufferedImage((int) header.width, (int) header.height
                        / 2,
                        BufferedImage.TYPE_INT_ARGB);

                switch (header.bitsPerPixel) {
                    case 4:
                    case 8:
                        loadPalettedImage(in, entries[i], header, image);
                        break;

                    default:
                        throw new Exception("Unsupported ICO color depth: "
                                + header.bitsPerPixel);
                }

                images[i] = image;
            }

            return images;

        } catch (Exception exc) {
        }

        return null;
    }

    static private void loadPalettedImage(BinaryInputStream in, IconEntry entry,
            IconHeader header, BufferedImage image) throws Exception {
        //	System.out.println("Loading image...");

        //	System.out.println("Loading palette...");

        //
        // First, load the palette
        //
        int cols = (int) header.colorsUsed;
        if (cols == 0) {
            if (entry.colorCount != 0) {
                cols = entry.colorCount;
            } else {
                cols = 1 << header.bitsPerPixel;
            }
        }

        int[] redp = new int[cols];
        int[] greenp = new int[cols];
        int[] bluep = new int[cols];

        for (int i = 0; i < cols; i++) {
            bluep[i] = in.readUByte();
            greenp[i] = in.readUByte();
            redp[i] = in.readUByte();
            in.readUByte();
        }

        //	System.out.println("Palette read!");

        //
        // Set the image

        int xorbytes = (((int) header.height / 2) * (int) header.width);
        int readbytes = 0;

        for (int y = (int) (header.height / 2) - 1; y >= 0; y--) {
            for (int x = 0; x < header.width; x++) {
                switch (header.bitsPerPixel) {
                    case 4: {
                        int pix = in.readUByte();
                        readbytes++;

                        int col1 = (pix >> 4) & 0x0F;
                        int col2 = pix & 0x0F;
                        image.setRGB(x, y, (0xFF << 24) | (redp[col1] << 16) | (greenp[col1]
                                << 8) | bluep[col1]);
                        image.setRGB(++x, y, (0xFF << 24) | (redp[col2] << 16) | (greenp[col2]
                                << 8) | bluep[col2]);
                    }
                    break;
                    case 8: {
                        int col1 = in.readUByte();
                        readbytes++;

                        image.setRGB(x, y, (0xFF << 24) | (redp[col1] << 16) | (greenp[col1]
                                << 8) | bluep[col1]);
                    }
                    break;
                }
            }
        }
        //	System.out.println("XOR data read (" + readbytes + " bytes)");

        int height = (int) (header.height / 2);

        int rowsize = (int) header.width / 8;
        if ((rowsize % 4) > 0) {
            rowsize += 4 - (rowsize % 4);
        }

        //	System.out.println("rowsize = " + rowsize);
        int[] andbytes = new int[rowsize * height];

        for (int i = 0; i < andbytes.length; i++) {
            andbytes[i] = in.readUByte();
        }


        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < header.width; x++) {
                int offset = ((height - (y + 1)) * rowsize) + (x / 8);
                if ((andbytes[offset] & (1 << (7 - x % 8))) != 0) {
                    image.setRGB(x, y, 0);
                }
            }
        }

         	for (int i=0; i<andbytes.length; i++)
         	    {
         		int pix = in.readUByte();
         		readbytes++;

         		int xb = (i*8) % (int)header.width;
         		int yb = ((int)header.height/2) - (((i*8) / (int)header.width)+1);

         		for (int offset=7; offset>=0; offset--)
         		    {
         			//
         			// Modify the transparency only if necessary
         			//
         			System.out.println("SET AND (" + xb + "," + yb + ")-" + (7-offset));

         			if (((1<<offset) & pix)!=0)
         			    {
         				int argb = image.getRGB(xb+(7-offset), yb);
         				image.setRGB(xb+(7-offset), yb, argb & 0xFFFFFF);
         			    }
         		    }
         	    }

        //	System.out.println("AND data read (" + readbytes + " bytes total)");
    }
}
