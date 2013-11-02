/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 18, 2004
 * Time: 12:29:55 AM
 * $Id:;
 */
package com.schizogenesis.antez.resourcebuilder;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 18, 2004  12:29:55 AM
 */
public abstract class BinaryResourceSection implements ResourceSection {
    protected final Collection resources = new ArrayList();
    protected String code;
    private File dir;

    public void setCode(String code) { this.code = code; }
    public void setDir(File dir) { this.dir = dir; }
    File getDir() { return dir; }

    public BinaryResource createResource() {
        BinaryResource resource = new BinaryResource(this);
        resources.add(resource);
        return resource;
    };

    public String generateIndexEntries() {
        StringBuffer indexEntries = new StringBuffer();
        int idx = 0;
        for (Iterator itResources = resources.iterator(); itResources.hasNext();) {
            BinaryResource resource = (BinaryResource) itResources.next();
            String description = resource.getDescription();
            String code = resource.getCode();

            if (description!=null) indexEntries.append("\n/** ["+idx+"] "+description+" */\n");
            if (code!=null) indexEntries.append("public static final int " + code + " = " + idx + ";\n");

            idx++;
        }
        return indexEntries.toString();
    }

    public String generateLoader() {
        StringBuffer resourceSizes = new StringBuffer();
        for (Iterator itResources = resources.iterator(); itResources.hasNext();) {
            BinaryResource resource = (BinaryResource) itResources.next();
            resourceSizes.append(resource.getContentSize()).append(", ");
        }
        resourceSizes.delete(resourceSizes.length()-2, resourceSizes.length()-1);
        return  "   { // generator: " + getClass().getName() + "\n" +
                "       int[] sizes = new int[]{" + resourceSizes + "};\n" +
                "       for (int i=0; i<sizes.length; i++) {\n" +
                "           byte[] data = new byte[sizes[i]];\n" +
                "           for (int j = 0; j < data.length; j++) data[j] = (byte) (input.read() & 0xFF);\n" +
                "           " + code + "[i] = " + generateInstantiate() + ";\n"+
                "       }\n" +
                "   }\n";
    }

    public void writeContent(OutputStream out) throws IOException {
        for (Iterator itResources = resources.iterator(); itResources.hasNext();) {
            BinaryResource resource = (BinaryResource) itResources.next();
            resource.writeContent(out);
        }
    }

    protected abstract String generateInstantiate();
}
