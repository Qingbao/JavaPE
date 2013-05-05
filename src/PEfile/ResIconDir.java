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

import java.nio.*;

/**
 * Directory of icons in the resource section ?
 */
public class ResIconDir implements BinaryRecord {
    private long location;

    private int idReserved;   // Reserved (must be 0)
    private int idType;       // Resource Type (1 for icons)
    private int idCount;      // How many images?
    private IconDirEntry[] entries;

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(6 + (16 * idCount));
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.position(0);

        buf.putShort((short) idReserved);
        buf.putShort((short) idType);
        buf.putShort((short) idCount);

        for (int i = 0; i < idCount; i++) {
            ByteBuffer b = entries[i].getData();
            b.position(0);
            buf.put(b);
        }

        return buf;
    }

    public IconDirEntry[] getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("m_idReserved: ").append(idReserved).append("\n");   // Reserved (must be 0)
        out.append("m_idType: ").append(idType).append("\n");       // Resource Type (1 for icons)
        out.append("m_idCount: ").append(idCount).append("\n");      // How many images?
        out.append("entries: ---- \n");
        for (int i = 0; i < entries.length; i++) {
            out.append(entries[i].toString());
        }

        return out.toString();
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
    public void setData(ByteBuffer buf) {
        idReserved = buf.getShort();
        idType = buf.getShort();
        idCount = buf.getShort();

        entries = new IconDirEntry[idCount];
        for (int i = 0; i < idCount; i++) {
            entries[i] = new IconDirEntry(buf);
        }
    }
}
