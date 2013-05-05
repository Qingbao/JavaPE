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

import java.nio.ByteBuffer;

/**
 * A binary record that simply reads/writes the specified amount of bytes.
 */
public class SimpleBinaryRecord implements BinaryRecord {
    private long location;
    private ByteBuffer data;

    /**
     * @param size size of the record in bytes
     */
    public SimpleBinaryRecord(int size) {
        this.data = ByteBuffer.allocate(size);
    }

    @Override
    public long getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(long location) {
        this.location = location;
    }

    @Override
    public ByteBuffer getData() {
        data.position(0);
        return data;
    }

    @Override
    public void setData(ByteBuffer data) {
        data.get(this.data.array());
    }
}
