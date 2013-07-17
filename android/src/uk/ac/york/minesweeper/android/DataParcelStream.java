package uk.ac.york.minesweeper.android;

import java.io.DataInput;
import java.io.DataOutput;

import android.os.Parcel;

/**
 * Implementation of DataInput and DataOutput which reads and writes data to a Parcel
 */
public class DataParcelStream implements DataInput, DataOutput
{
    private final Parcel parcel;

    /**
     * Creates a new DataParcelStream using the given parcel
     *
     * @param parcel parcel to read from / write to
     */
    public DataParcelStream(Parcel parcel)
    {
        this.parcel = parcel;
    }

    @Override
    public boolean readBoolean()
    {
        return readByte() != 0;
    }

    @Override
    public byte readByte()
    {
        return parcel.readByte();
    }

    @Override
    public char readChar()
    {
        return (char) readShort();
    }

    @Override
    public double readDouble()
    {
        return parcel.readDouble();
    }

    @Override
    public float readFloat()
    {
        return parcel.readFloat();
    }

    @Override
    public void readFully(byte[] dst)
    {
        readFully(dst, 0, dst.length);
    }

    @Override
    public void readFully(byte[] dst, int offset, int byteCount)
    {
        // There is no method to do this in Parcel, so we have to read the bytes manually
        for (int i = 0; i < byteCount; i++)
            dst[i + offset] = readByte();
    }

    @Override
    public int readInt()
    {
        return parcel.readInt();
    }

    @Override
    public String readLine()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long readLong()
    {
        return parcel.readLong();
    }

    @Override
    public short readShort()
    {
        return (short) readInt();
    }

    @Override
    public String readUTF()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readUnsignedByte()
    {
        return readByte() & 0xFF;
    }

    @Override
    public int readUnsignedShort()
    {
        return readShort() & 0xFFFF;
    }

    @Override
    public int skipBytes(int count)
    {
        // Advance count bytes if possible
        if (count >= parcel.dataAvail())
        {
            int bytesSkipped = parcel.dataAvail();
            parcel.setDataPosition(parcel.dataSize());
            return bytesSkipped;
        }
        else
        {
            parcel.setDataPosition(parcel.dataPosition() + count);
            return count;
        }
    }

    @Override
    public void write(byte[] buffer)
    {
        write(buffer, 0, buffer.length);
    }

    @Override
    public void write(int oneByte)
    {
        writeByte(oneByte);
    }

    @Override
    public void write(byte[] buffer, int offset, int count)
    {
        for (int i = 0; i < count; i++)
            parcel.writeByte(buffer[i + offset]);
    }

    @Override
    public void writeBoolean(boolean val)
    {
        writeByte(val ? 1 : 0);
    }

    @Override
    public void writeByte(int val)
    {
        parcel.writeByte((byte) val);
    }

    @Override
    public void writeBytes(String str)
    {
        for (int i = 0; i < str.length(); i++)
            writeByte(str.codePointAt(i));
    }

    @Override
    public void writeChar(int val)
    {
        writeShort(val);
    }

    @Override
    public void writeChars(String str)
    {
        for (int i = 0; i < str.length(); i++)
            writeChar(str.codePointAt(i));
    }

    @Override
    public void writeDouble(double val)
    {
        parcel.writeDouble(val);
    }

    @Override
    public void writeFloat(float val)
    {
        parcel.writeFloat(val);
    }

    @Override
    public void writeInt(int val)
    {
        parcel.writeInt(val);
    }

    @Override
    public void writeLong(long val)
    {
        parcel.writeLong(val);
    }

    @Override
    public void writeShort(int val)
    {
        writeInt(val);
    }

    @Override
    public void writeUTF(String str)
    {
        throw new UnsupportedOperationException();
    }
}
