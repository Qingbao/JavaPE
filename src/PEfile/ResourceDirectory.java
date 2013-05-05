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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of resources.
 */
public class ResourceDirectory implements BinaryRecord {
    private long location;

    public long characteristics; // uint32_t
    public long timeDateStamp; // uint32_t
    public int majorVersion; // uint16_t
    public int minorVersion; // uint16_t
    // int NumberOfNamedEntries; // uint16_t
    // int NumberOfIdEntries; // uint16_t

    private List<ResourceEntry> namedEntries =
            new ArrayList<ResourceEntry>();
    private List<ResourceEntry> idEntries =
            new ArrayList<ResourceEntry>();

    public void addNamedEntry(ResourceEntry entry) {
        this.namedEntries.add(entry);
    }

    public void addIdEntry(ResourceEntry entry) {
        this.idEntries.add(entry);
    }

    public void addEntry(ResourceEntry entry) {
        if (entry.name != null) {
            addNamedEntry(entry);
        } else {
            addIdEntry(entry);
        }
    }

    public void dump(PrintStream out, int level) {
        indent(level, out);
        out.println("Directory: ");
        indent(level, out);
        out.println("Characteristics=" + this.characteristics);
        indent(level, out);
        out.println("TimeDateStamp=" + this.timeDateStamp);
        indent(level, out);
        out.println("MajorVersion=" + this.majorVersion);
        indent(level, out);
        out.println("MinorVersion=" + this.minorVersion);
        indent(level, out);
        out.println("NumberOfNamedEntries=" + this.namedEntries.size());
        indent(level, out);
        out.println("NumberOfIdEntries=" + this.idEntries.size());
        indent(level, out);
        out.println("Named Entries:");
        for (int i = 0; i < namedEntries.size();
                i++) {
            ResourceEntry re = namedEntries.get(i);
            re.dump(out, level + 1);
        }
        indent(level, out);
        out.println("Id Entries:");
        for (int i = 0; i < idEntries.size();
                i++) {
            ResourceEntry re = idEntries.get(i);
            re.dump(out, level + 1);
        }
    }

    private void indent(int level, PrintStream out) {
        for (int i = 0; i < level;
                i++) {
            out.print("    ");
        }
    }

    public int diskSize() {
        int size = 16;
        for (int i = 0; i < this.namedEntries.size();
                i++) {
            ResourceEntry re = namedEntries.get(i);
            size += re.diskSize();
        }
        for (int i = 0; i < this.idEntries.size();
                i++) {
            ResourceEntry re = idEntries.get(i);
            size += re.diskSize();
        }
        if ((size % 4) > 0) {
            size += 4 - (size % 4);
        }
        return size;
    }

    public ResourceEntry getResourceEntry(String name) {
        // If name == null, get the first entry in lexical
        // order. If no entry in lexical order, choose the
        // lowest integer nameOrId entry.
        if (name == null) {
            if (namedEntries.size() > 0) {
                return namedEntries.get(0);
            }
            if (idEntries.size() > 0) {
                return idEntries.get(0);
            }
            return null;
        }
        if ((name.length() > 0) && (name.charAt(0) == '#')) {
            try {
                String nb = name.substring(1);
                int i = Integer.parseInt(nb);
                return getResourceEntry(i);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        for (Iterator<ResourceEntry> i =
                this.namedEntries.iterator(); i.hasNext();) {
            ResourceEntry re = i.next();
            /*if (name.equals(re.resourceEntry.this.name)) {
                return re;
            } todo */
        }
        return null;
    }

    public ResourceEntry getResourceEntry(int id) {
        for (Iterator<ResourceEntry> i =
                this.idEntries.iterator(); i.hasNext();) {
            ResourceEntry re = i.next();
            /* todo if (nameOrId == re.ResourceEntry.this.nameOrId) {
                return re;
            }*/
        }
        return null;
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
        buffer.putInt((int) this.characteristics);
        buffer.putInt((int) this.timeDateStamp);
        buffer.putShort((short) this.majorVersion);
        buffer.putShort((short) this.minorVersion);
        buffer.putShort((short) this.namedEntries.size());
        buffer.putShort((short) this.idEntries.size());
        int dataOffset = buffer.position() + (namedEntries.size() * 8) +
                (idEntries.size() * 8);
        for (int i = 0; i < this.namedEntries.size();
                i++) {
            ResourceEntry re = this.namedEntries.get(i);
            // TODO dataOffset = re.buildBuffer(buffer, virtualBaseOffset, dataOffset);
        }
        for (int i = 0; i < this.idEntries.size();
                i++) {
            ResourceEntry re = this.idEntries.get(i);
            // TODO dataOffset = re.buildBuffer(buffer, virtualBaseOffset, dataOffset);
        }
        buffer.position(dataOffset);
        return buffer;
    }

    @Override
    public void setData(ByteBuffer header) {
        characteristics = header.getInt();
        timeDateStamp = header.getInt();
        majorVersion = header.getShort();
        minorVersion = header.getShort();
        short numberOfNamedEntries = header.getShort();
        short numberOfIdEntries = header.getShort();
        for (int i = 0; i < numberOfNamedEntries; i++) {
            ResourceEntry re = new ResourceEntry();
            re.setData(header);
            namedEntries.add(re);
        }
        for (int i = 0; i < numberOfIdEntries; i++) {
            ResourceEntry re = new ResourceEntry();
            re.setData(header);
            idEntries.add(re);
        }
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
        for (ResourceEntry re : namedEntries) {
            re.materialize(buf, resourceSectionOffset,
                    resourceSectionVirtualAddress);
        }
        for (ResourceEntry re : this.idEntries) {
            re.materialize(buf, resourceSectionOffset,
                    resourceSectionVirtualAddress);
        }
    }

    /**
     * Returns an entry with the specified ID. Creates a new entry if it
     * does not exist
     *
     * @param nameOrId ID of the entry
     * @return entry with the specified ID (without any data or
     *     subdirectory)
     */
    /* todo
    public ResourceEntry getOrCreateResourceEntry(int nameOrId) {
    ResourceEntry r = getResourceEntry(nameOrId);
    if (r == null) {
    r = buildResourceEntry(nameOrId, (DataEntry) null);
    addEntry(r);
    }
    return r;
    }*/
    /**
     * Returns an entry with the specified ID. Creates a new entry if it
     * does not exist
     *
     * @param nameOrId ID of the entry
     * @return entry with the specified ID (without any data or
     *     subdirectory)
     */
    /* todo
    public ResourceEntry getOrCreateResourceEntry(int nameOrId) {
    ResourceEntry r = getResourceEntry(nameOrId);
    if (r == null) {
    r = buildResourceEntry(nameOrId, (DataEntry) null);
    addEntry(r);
    }
    return r;
    }*/
}
