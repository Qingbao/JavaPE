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

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * data for a resource entry.
 */
public class ResourceDataEntry implements BinaryRecord {
    private long location;

    long offsetToData; // To update at each change
    long size;
    long codePage; // never changed
    long reserved; // never changed

    ByteBuffer data;

    public int diskSize() {
        int size = 16 + (int) this.size;
        if ((size % 4) > 0) {
            size += 4 - (size % 4);
        }
        return size;
    }

    public void dump(PrintStream out, int level) {
        indent(level, out);
        out.println("OffsetToData=" + offsetToData);
        indent(level, out);
        out.println("Size=" + size);
        indent(level, out);
        out.println("CodePage=" + codePage);
        indent(level, out);
        out.println("Reserved=" + reserved);
        indent(level, out);
        out.print("Data={ ");
        for (int i = 0; i < this.data.capacity();
                i++) {
            out.print("" + Integer.toHexString(data.get(i)) + ",");
        }
        out.println(" }");
    }

    private void indent(int level, PrintStream out) {
        for (int i = 0; i < level;
                i++) {
            out.print("    ");
        }
    }

    @Override
    public void setData(ByteBuffer buf) {
        offsetToData = buf.getInt();
        size = buf.getInt();
        codePage = buf.getInt();
        reserved = buf.getInt();
    }

    @Override
    public long getLocation() {
        return location;
    }

    @Override
    public void setLocation(long location) {
        this.location = location;
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buffer = ByteBuffer.allocate(100); // TODO
        long virtualBaseOffset = 0; // TODO
        int dataOffset = 0; // TODO
        //			System.out.println("Building data Entry buffer @ " + buffer.position() + " (" + dataOffset + ")");
        dataOffset = buffer.position() + 16;
        buffer.putInt((int) (dataOffset + virtualBaseOffset));
        buffer.putInt((int) size);
        buffer.putInt((int) codePage);
        buffer.putInt((int) reserved);
        data.position(0);
        buffer.put(data);
        dataOffset += size;
        if ((dataOffset % 4) > 0) {
            dataOffset += (4 - (dataOffset % 4));
        }
        return buffer;
    }

    /**
     * Loads all dependant objects so that the file can be closed and re-created
     * only from the data in memory.
     *
     * @param buf file. The position of the buffer will not change.
     * @param resourceSectionOffset offset of the resource section
     * @param resourceSectionVirtualAddress virtual address of the resource
     *     section
     */
    public void materialize(ByteBuffer buf, int resourceSectionOffset,
            int resourceSectionVirtualAddress) {
        int oldPos = buf.position();

        long datapos = this.offsetToData;
        buf.position((int) datapos - resourceSectionVirtualAddress +
                resourceSectionOffset);

        this.data = ByteBuffer.allocate((int) size);
        data.order(ByteOrder.LITTLE_ENDIAN);
        buf.get(data.array());

        buf.position(oldPos);
    }
}
