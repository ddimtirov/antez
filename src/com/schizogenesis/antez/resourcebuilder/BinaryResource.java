/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 18, 2004
 * Time: 12:27:49 AM
 * $Id:;
 */
package com.schizogenesis.antez.resourcebuilder;

import java.io.*;

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 18, 2004  12:27:49 AM
 */
public class BinaryResource {
    private String srcfile;
    private String code;
    private String description;
    private final BinaryResourceSection parent;

    public BinaryResource(BinaryResourceSection parent) {
        this.parent = parent;
    }

    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    String getCode() { return code; }
    String getDescription() { return description; }

    public void setSrcfile(String srcfile) { this.srcfile = srcfile; }

    public void writeContent(OutputStream out) throws IOException {
        InputStream in = new FileInputStream(normalizedFile());
        byte[] data = new byte[in.available()];
        in.read(data);
        in.close();
        out.write(data);
    }

    private File normalizedFile() {
        return new File(parent.getDir(), srcfile);
    }

    public int getContentSize() {
        try {
            InputStream in = new FileInputStream(normalizedFile());
            int size = in.available();
            in.close();
            return size;
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
