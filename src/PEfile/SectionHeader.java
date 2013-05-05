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
import java.nio.*;

/**
 * A section header in a PE file.
 */
public class SectionHeader implements Cloneable, BinaryRecord {
    /** this field is always 8 bytes long */
    public byte[] ansiName; // Name of the Section. Can be anything (0)(8BYTES)

    // The size of the section when it is mapped to memory. Must be a multiple of 4096. (8)(DWORD)
    public long virtualSize;

    // An rva to where it should be mapped in memory. (12)(DWORD)
    public long virtualAddress;

    // The size of the section in the PE file. Must be a multiple of 512 (16)(DWORD)
    public long sizeOfRawData;

    // A file based offset which points to the location of this sections data (20)(DWORD)
    public long pointerToRawData;

    // In EXE's this field is meaningless, and is set 0 (24)(DWORD)
    public long pointerToRelocations;

    // This is the file-based offset of the line number table. This field is only used for debug purposes, and is usualy set to 0 (28)(DWORD)
    public long pointerToLinenumbers;

    // In EXE's this field is meaningless, and is set 0 (32)(WORD)
    public int numberOfRelocations;

    // The number of line numbers in the line number table for this section. This field is only used for debug purposes, and is usualy set to 0 (34)(WORD)
    public int numberOfLinenumbers;

    // The kind of data stored in this section ie. Code, Data, Import data, Relocation data (36)(DWORD)
    public long characteristics;
    
    private long m_baseoffset;

    /**
     * Creates a new instance of SectionHeader
     *
     * @param baseoffset offset of this section header
     */
    public SectionHeader(long baseoffset) {
        m_baseoffset = baseoffset;
    }

    /**
     * Creates a new instance of SectionHeader.
     * 
     * @param name name of the section (no more than 8 ANSI characters)
     */
    public SectionHeader(String name) {
        this.ansiName = new byte[8];
        byte[] bytes = name.getBytes();
        System.arraycopy(bytes, 0, this.ansiName, 0,
                bytes.length);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getName() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            buffer.append((char) ansiName[i]);
        }
        return buffer.toString();
    }

    @Override
    public void setData(ByteBuffer head) {
        ansiName = new byte[8];
        for (int i = 0; i < 8; i++) {
            ansiName[i] = head.get();
        }

        virtualSize = head.getInt();
        virtualAddress = head.getInt();
        sizeOfRawData = head.getInt();
        pointerToRawData = head.getInt();
        pointerToRelocations = head.getInt();
        pointerToLinenumbers = head.getInt();
        numberOfRelocations = head.getShort();
        numberOfLinenumbers = head.getShort();
        characteristics = head.getInt();
    }

    public void dumpSectionInfo(PrintStream out) {
        
        out.print("Name= ");
        for (int i = 0; i < 8; i++) {
            out.print((char) ansiName[i]);
        }
        out.println("");
        out.println(
                "  VirtualSize= " + virtualSize
                + "  // 	The size of the section when it is mapped to memory. Must be a multiple of 4096. (8)(DWORD)");
        out.println(
                "  VirtualAddress= " + virtualAddress
                + "   // 	An rva to where it should be mapped in memory. (12)(DWORD)");
        out.println(
                "  SizeOfRawData= " + sizeOfRawData
                + "   // 	The size of the section in the PE file. Must be a multiple of 512 (16)(DWORD)");
        out.println(
                "  PointerToRawData= " + pointerToRawData
                + "   // 	A file based offset which points to the location of this sections data (20)(DWORD)");
        out.println(
                "  PointerToRelocations= " + pointerToRelocations
                + "   // 	In EXE's this field is meaningless, and is set 0 (24)(DWORD)");
        out.println(
                "  PointerToLinenumbers= " + pointerToLinenumbers
                + "   // 	This is the file-based offset of the line number table. This field is only used for debug purposes, and is usualy set to 0 (28)(DWORD)");
        out.println(
                "  NumberOfRelocations= " + numberOfRelocations
                + "   // 	In EXE's this field is meaningless, and is set 0 (32)(WORD)");
        out.println(
                "  NumberOfLinenumbers= " + numberOfLinenumbers
                + "   // 	The number of line numbers in the line number table for this section. This field is only used for debug purposes, and is usualy set to 0 (34)(WORD)");
        out.println(
                "  Characteristics= " + characteristics
                + "   // 	The kind of data stored in this section ie. Code, Data, Import data, Relocation data (36)(DWORD)");

    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer head = ByteBuffer.allocate(40);
        head.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 8; i++) {
            head.put(ansiName[i]);
        }

        head.putInt((int) virtualSize);
        head.putInt((int) virtualAddress);
        head.putInt((int) sizeOfRawData);
        head.putInt((int) pointerToRawData);
        head.putInt((int) pointerToRelocations);
        head.putInt((int) pointerToLinenumbers);
        head.putShort((short) numberOfRelocations);
        head.putShort((short) numberOfLinenumbers);
        head.putInt((int) characteristics);

        head.position(0);
        return head;
    }

    @Override
    public long getLocation() {
        return m_baseoffset;
    }

    @Override
    public void setLocation(long location) {
        this.m_baseoffset = location;
    }
}
