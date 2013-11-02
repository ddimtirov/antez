/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 28, 2004
 * Time: 6:09:53 PM
 * $Id:;
 */

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 28, 2004  6:09:53 PM
 */
import java.io.OutputStream;
import java.io.IOException;

public class KJXFormat {

    public static final int KJX_MAX_SIZE = 51200;
    public static final byte[] KJX_PREAMBLE = {75, 74, 88};

    private final String kjxFileName;
    private final String jadFileName;
    private final String charEncoding;
    private final byte[] jaddata;
    private final byte[] jardata;
    private final int jadoffset;
    private final int jadsize;
    private final int jaroffset;
    private final int jarsize;

    public KJXFormat(String kjxFileName, String jadFileName, byte[] jaddata, byte[] jardata, String charEncoding) {
        this.kjxFileName = kjxFileName;
        this.jadFileName = jadFileName;
        this.charEncoding = charEncoding;

        this.jaddata = jaddata;
        jadoffset = 0;
        jadsize = this.jaddata.length;

        this.jardata = jardata;
        jaroffset = 0;
        jarsize = this.jardata.length;
    }

    public KJXFormat(byte[] kjxBuffer, String charEncoding) throws IOException {
        if (KJX_MAX_SIZE < kjxBuffer.length) System.out.println("Warring KJX File Size over 51200bytes.");
        this.charEncoding = charEncoding;

        int i;
        for (i = 0; i < KJX_PREAMBLE.length; i++) {
            if (KJX_PREAMBLE[i] != kjxBuffer[i]) throw new IOException("No KJX File Format.");
        }

        final int dataOffset = 255 & kjxBuffer[i++];

        final int kjxFilenameLength = 255 & kjxBuffer[i++];
        if (24 < kjxFilenameLength) System.out.println("Warring KJX File Name Length over 24.");
        this.kjxFileName = (charEncoding != null)
                ? new String(kjxBuffer, i, kjxFilenameLength, charEncoding)
                : new String(kjxBuffer, i, kjxFilenameLength);
        i += kjxFilenameLength;

        final int jadsizeMsb = 255 & kjxBuffer[i++];
        final int jadsizeLsb = 255 & kjxBuffer[i++];
        this.jadsize = jadsizeMsb << 8 | jadsizeLsb;

        final int jadFilenameLength = 255 & kjxBuffer[i++];
        if (24 < jadFilenameLength) System.out.println("Warring JAD File Name Length over 24.");
        jadFileName = (charEncoding != null)
                ? new String(kjxBuffer, i, jadFilenameLength, charEncoding)
                : new String(kjxBuffer, i, jadFilenameLength);
        i += jadFilenameLength;

        if (dataOffset != i) throw new IOException("Illegal KJX File Format. Bad dataOffset: [header:"+dataOffset+"; actual:" + i + "]");

        jaddata = kjxBuffer;
        jadoffset = i;
        i += jadsize;

        jardata = kjxBuffer;
        jaroffset = i;
        jarsize = kjxBuffer.length - i;
    }

    public void write(OutputStream outputstream) throws IOException {
        byte[] kjxFilenameLength = (charEncoding == null)
                ? kjxFileName.getBytes()
                : kjxFileName.getBytes(charEncoding);

        byte[] jadFilenameLength = (charEncoding == null)
                ? jadFileName.getBytes()
                : jadFileName.getBytes(charEncoding);

        int dataOffset = KJX_PREAMBLE.length
                + 2 // the dataOffset (header size)
                + 1 // the kjx filename length
                + kjxFilenameLength.length
                + 2 // the jad filename length (note it's 16 bit)
                + jadFilenameLength.length;

        outputstream.write(KJX_PREAMBLE);
        outputstream.write(dataOffset);
        outputstream.write(kjxFilenameLength.length & 255);
        outputstream.write(kjxFilenameLength);
        outputstream.write(jaddata.length >> 8 & 255);
        outputstream.write(jaddata.length & 255);
        outputstream.write(jadFilenameLength.length & 255);
        outputstream.write(jadFilenameLength);
        outputstream.write(jaddata, jadoffset, jadsize);
        outputstream.write(jardata, jaroffset, jarsize);
    }

    public final String getKJXFileName() {
        return kjxFileName;
    }

    public final String getJADFileName() {
        return jadFileName;
    }

    public final byte[] getJADData() {
        byte[] jaddata = new byte[jadsize];
        System.arraycopy(this.jaddata, jadoffset, jaddata, 0, jadsize);
        return jaddata;
    }

    public final byte[] getJARData() {
        byte[] jardata = new byte[jarsize];
        System.arraycopy(this.jardata, jaroffset, jardata, 0, jarsize);
        return jardata;
    }

}
