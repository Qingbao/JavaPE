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
 * Entry in a resource directory.
 */
public class ResourceEntry implements BinaryRecord {
    private long location;

    public int nameOrId;
    public int dataOrDirectory;

    public String name;
    public ResourceDirectory directory;
    public ResourceDataEntry data;

    /**
     * Default constructor can be used to read the data from a ByteBuffer
     */
    public ResourceEntry() {
    }

    public ResourceEntry(int id, ResourceDataEntry data) {
        this.nameOrId = id;
        this.data = data;
    }

    public ResourceEntry(String name, ResourceDataEntry data) {
        this.name = name;
        this.data = data;
    }

    public ResourceEntry(int id, ResourceDirectory dir) {
        this.nameOrId = id;
        this.directory = dir;
    }

    public ResourceEntry(String name, ResourceDirectory dir) {
        this.name = name;
        this.directory = dir;
    }

    public int diskSize() {
        int size = 8;
        if (name != null) {
            size += (name.length() * 2) + 2;
        }

        if (directory != null) {
            size += directory.diskSize();
        } else if (data != null) {
            size += data.diskSize();
        }

        if ((size % 4) > 0) {
            size += 4 - (size % 4);
        }
        return size;
    }

    public void dump(PrintStream out, int level) {
        indent(level, out);
        if (this.name != null) {
            out.println("Name=" + name);
        } else {
            out.println("Id=#" + nameOrId);
        }

        indent(level, out);
        if (this.directory != null) {
            out.println("ENTRY: DIRECTORY POINTER");
            this.directory.dump(out, level + 1);
        } else {
            out.println("ENTRY: DATA ENTRY");
            data.dump(out, level + 1);
        }
    }

    private void indent(int level, PrintStream out) {
        for (int i = 0; i < level; i++) {
            out.print("    ");
        }
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
        int dataOffset = 0; // TODO
        ByteBuffer buffer = ByteBuffer.allocate(100); // TODO
        if (name != null) {
            buffer.putInt(dataOffset | 0x80000000);

            int stringoffset = dataOffset;
            ByteBuffer strbuf = ByteBuffer.allocate(name.length() * 2 + 2);
            strbuf.order(ByteOrder.LITTLE_ENDIAN);

            strbuf.putShort((short) name.length());
            for (int i = 0; i < name.length(); i++) {
                strbuf.putShort((short) name.charAt(i));
            }
            strbuf.position(0);

            long oldpos = buffer.position();
            buffer.position(dataOffset);
            buffer.put(strbuf);
            dataOffset += name.length() * 2 + 2;
            if ((dataOffset % 4) != 0) {
                dataOffset += 4 - (dataOffset % 4);
            }
            buffer.position((int) oldpos);
        } else {
            buffer.putInt(nameOrId);
        }

        if (directory != null) {
            buffer.putInt(dataOffset | 0x80000000);

            int oldpos = buffer.position();
            buffer.position(dataOffset);
            // todo int dirsize = directory.buildBuffer(buffer, virtualBaseOffset);
            // dataOffset = dirsize;
            buffer.position(oldpos);

        } else if (data != null) {
            buffer.putInt(dataOffset);
            int oldpos = buffer.position();
            buffer.position(dataOffset);
            /* todo dataOffset = data.buildBuffer(buffer, virtualBaseOffset,
                    dataOffset);*/
            buffer.position(oldpos);
        } else {
            throw new RuntimeException("Directory and Data are both null!");
        }

        return buffer;
    }

    @Override
    public void setData(ByteBuffer buf) {
        nameOrId = buf.getInt();
        dataOrDirectory = buf.getInt();
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
        int oldPos_ = buf.position();

        int val = nameOrId;
        int offsetToData = dataOrDirectory;
        if (val < 0) {
            val &= 0x7FFFFFFF;
            int oldPos = buf.position();
            UnicodeString us = new UnicodeString();
            buf.position(val);
            us.setData(buf);
            this.name = us.getText();
            buf.position(oldPos);
        }

        if (offsetToData < 0) {
            offsetToData &= 0x7FFFFFFF;
            long oldPos = buf.position();
            buf.position(offsetToData + resourceSectionOffset);
            directory = new ResourceDirectory();
            directory.setData(buf);
            buf.position((int) oldPos);
            directory.materialize(buf, resourceSectionOffset,
                    resourceSectionVirtualAddress);
        } else {
            data = new ResourceDataEntry();
            int oldPos = buf.position();
            buf.position(offsetToData + resourceSectionOffset);
            data.setData(buf);
            buf.position(oldPos);
            data.materialize(buf, resourceSectionOffset,
                    resourceSectionVirtualAddress);
        }

        buf.position(oldPos_);
    }
}
