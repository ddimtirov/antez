/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 18, 2004
 * Time: 12:28:39 AM
 * $Id:;
 */
package com.schizogenesis.antez.resourcebuilder;

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 18, 2004  12:28:39 AM
 */
public class ImageResourceSection extends BinaryResourceSection {
    public String generateContainer() {
        return "static final javax.microedition.lcdui.Image[] " + code + " = new javax.microedition.lcdui.Image["+resources.size()+"];\n";
    }

    protected String generateInstantiate() {
        return " javax.microedition.lcdui.Image.createImage(data, 0, data.length)";
    }

}
