/*
 * 
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A Unicode string.
 */
public class UnicodeString implements BinaryRecord {
    private long location;
    private String data = "";

    /**
     * Empty string.
     */
    public UnicodeString() {
        this.data = "";
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setData(ByteBuffer data) {
        short size = data.getShort();
        ByteBuffer buffer = ByteBuffer.allocate(size * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        data.get(buffer.array());

        StringBuilder buf = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int c = buffer.getShort();
            buf.append((char) c);
        }

        this.data = buf.toString();
    }

    /**
     * @return content
     */
    public String getText() {
        return this.data;
    }
}
