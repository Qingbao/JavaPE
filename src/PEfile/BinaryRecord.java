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
 * Record (some continuous data) in a file.
 */
public interface BinaryRecord {
    /**
     * @return location of this data in the file (absolute position)
     */
    public long getLocation();

    /**
     * Changes the location of this record in the file. Some implementations
     * may decide to throw an IllegalArgumentException or similar for
     * file headers or other parts that cannot be moved.
     *
     * @param location new location in the file
     */
    public void setLocation(long location);

    /**
     * The returned data should not be modified. An implementation may decide
     * to cache the data or just present a view over a file. The returned data
     * must reside in the returned buffer from the position 0. The length 
     * of the returned buffer must be the length of the data of this record.
     * The returned buffer must have its position at 0.
     *
     * @return the data
     */
    public ByteBuffer getData();

    /**
     * Sets internal state of this record. The ByteBuffer passed to this method
     * should not be stored internally or modified. The method should throw an
     * IllegalArgumentException if some magic numbers are invalid or the content
     * is not valid in some other way. The position of the passed buffer should
     * point to the first byte after this record after this method call.
     * The position in the buffer is not
     * necessary 0 when this method is called. An implementation should either
     * retrieve the current position and read the data relative to it or just
     * use the {@link ByteBuffer#getInt()} and similar methods.
     *
     * @param data binary data
     */
    public void setData(ByteBuffer data);
}
