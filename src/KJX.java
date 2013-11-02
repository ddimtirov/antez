/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Feb 5, 2004
 * Time: 3:48:07 PM
 * $Id:;
 */

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * @author dimiter@schizogenesis.com
 * @since Feb 5, 2004  3:48:07 PM
 */
public class KJX {
    public static final int KJX_MAX_SIZE = 51200;
    public static final byte[] KJX_PREAMBLE = {75, 74, 88};

    private final String jadFilename;
    private final byte[] jadData;
    private final byte[] jarData;
    private final String charEncoding;
    private String kjxFilename;

    public KJX(String kjxFileName, String jadFileName, byte[] jaddata, byte[] jardata, String charEncoding) {
        this.kjxFilename = kjxFileName;
        this.jadFilename = jadFileName;
        this.charEncoding = charEncoding;

        this.jadData = jaddata;
        this.jarData = jardata;
    }

    public KJX(String kjxFilename, File jadFile, File jarFile, String charEncoding) throws IOException {
        this.charEncoding = charEncoding;

        String  jadFilename = jadFile.getName();
        if (!jadFilename.endsWith(".jad")) jadFilename = jadFilename  + ".jad";
        String jarFilename = jadFilename.substring(0, jadFilename.length() - 4) + ".jar";

        this.jadFilename = jadFilename;

        FileChannel jadChannel = new FileInputStream(jadFile).getChannel();
        byte[] jadChannelBytes = jadChannel.map(FileChannel.MapMode.READ_ONLY, 0, jadChannel.size()).array();

        this.jadData = new byte[jadChannelBytes.length];
        System.arraycopy(jadChannelBytes, 0, jadData, 0, jadData.length);
        jadChannel.close();

        this.kjxFilename = kjxFilename;
        FileChannel jarChannel = new FileInputStream(jarFile).getChannel();
        byte[] jarChannelBytes = jarChannel.map(FileChannel.MapMode.READ_ONLY, 0, jarChannel.size()).array();

        this.jarData = new byte[jarChannelBytes.length];
        System.arraycopy(jarChannelBytes, 0, jarData, 0, jarData.length);
        jarChannel.close();

    }

    public void writeKjx(OutputStream out) throws IOException {
        byte[] kjxFilenameBytes = kjxFilename.getBytes(charEncoding);
        byte[] jadFilenameBytes = jadFilename.getBytes(charEncoding);

        int dataOffset = KJX_PREAMBLE.length
                + 2 // the dataOffset (header size)
                + 1 // the kjx filename length
                + kjxFilenameBytes.length
                + 2 // the jad filename length (note it's 16 bit)
                + jadFilenameBytes.length;

        out.write(KJX_PREAMBLE);
        out.write(dataOffset);
        out.write(kjxFilenameBytes.length & 255);
        out.write(kjxFilenameBytes);
        out.write(jadData.length >> 8 & 255);
        out.write(jadData.length & 255);
        out.write(jadFilenameBytes.length & 255);
        out.write(jadFilenameBytes);
        out.write(jadData);
        out.write(jarData);
    }

    public static KJX readKjx(InputStream kjxStream, String charEncoding) throws IOException {
        for (int i = 0; i < KJX_PREAMBLE.length; i++) {
            if ((KJX_PREAMBLE[i]&0xFF) != kjxStream.read()) throw new IOException("No KJX File Format.");
        }

        int dataOffset = kjxStream.read();

        int kjxFilenameLength = kjxStream.read();
        if (kjxFilenameLength>24) System.out.println("Warning KJX File Name Length over 24.");

        byte[] kjxFileNameBytes = new byte[kjxFilenameLength];
        kjxStream.read(kjxFileNameBytes);

        int jadsizeMsb = kjxStream.read();
        int jadsizeLsb = kjxStream.read();
        int jadsize = jadsizeMsb << 8 | jadsizeLsb;

        int jadFilenameLength = kjxStream.read();
        if (24 < jadFilenameLength) System.out.println("Warning JAD File Name Length over 24.");

        byte[] jadFileNameBytes = new byte[jadFilenameLength];
        kjxStream.read(jadFileNameBytes);

        int readSoFar = KJX_PREAMBLE.length
                + 1 // dataOffset
                + 1 // kjxFilenameLength
                + kjxFileNameBytes.length
                + 2 // jadsize
                + 1 // jadFilenameLength
                + jadFileNameBytes.length;

        if (dataOffset != readSoFar) throw new IOException("Illegal KJX File Format. Bad dataOffset: [header:"+dataOffset+"; actual:" + readSoFar + "]");

        byte[] jaddata = new byte[jadsize];
        kjxStream.read(jaddata);

        byte[] jardata = new byte[kjxStream.available()];
        kjxStream.read(jardata);

        return new KJX(
                new String(kjxFileNameBytes, charEncoding),
                new String(jadFileNameBytes, charEncoding),
                jaddata, jardata, charEncoding
        );
    }

    public String getCharEncoding() {
        return charEncoding;
    }

    public byte[] getJadData() {
        return jadData;
    }

    public String getJadFilename() {
        return jadFilename;
    }

    public byte[] getJarData() {
        return jarData;
    }

    public String getKjxFilename() {
        return kjxFilename;
    }

    public void setKjxFilename(String kjxFilename) {
        this.kjxFilename = kjxFilename;
    }

}
