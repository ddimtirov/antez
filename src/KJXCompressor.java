/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 28, 2004
 * Time: 6:09:07 PM
 * $Id:;
 */
import java.io.*;

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 28, 2004  6:09:07 PM
 */
public class KJXCompressor {
    public static final void main(String[] args) throws IOException {
        if ("-c".equals(args[0])) {
            KJX kjx = new KJX(args[3], new File(args[2]), new File(args[1]), null);

            OutputStream kjxOutput = new FileOutputStream(args[3]);
            kjx.writeKjx(kjxOutput);
            kjxOutput.close();
        } else if ("-x".equals(args[0])) {
            InputStream kjxInput = new FileInputStream(args[1]);
            KJX kjx = KJX.readKjx(kjxInput, null);
            kjxInput.close();

            String jadFilename = kjx.getJadFilename();
            if (!jadFilename.endsWith(".jad")) jadFilename = jadFilename  + ".jad";
            String jarFilename = jadFilename.substring(0, jadFilename.length() - 4) + ".jar";

            writeFile(jadFilename, kjx.getJadData());
            writeFile(jarFilename, kjx.getJarData());
        } else {
            System.out.println("SYNOPSIS:");
            System.out.println("\nCreate KJX: \n\t java KJX -c jadFileName jarFileName kjxFileName");
            System.out.println("\nExtract KJX:\n\t java KJX -x kjxFileName");
        }
    }

    private static void writeFile(String filename, byte[] buffer) throws IOException {
        OutputStream jadOutput = new BufferedOutputStream(new FileOutputStream(filename));
        jadOutput.write(buffer);
        jadOutput.close();
    }

    private static byte[] readFile(String filename) throws IOException {
        InputStream in = new FileInputStream(filename);
        byte[] kjxBuffer = new byte[in.available()];
        int actuallyRead = in.read(kjxBuffer);
        if (actuallyRead!=kjxBuffer.length) throw new IOException("actuallyRead!=srcBuffer.length");
        in.close();
        return kjxBuffer;
    }
}
