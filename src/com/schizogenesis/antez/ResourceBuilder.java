/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 17, 2004
 * Time: 4:41:03 PM
 * $Id:;
 */
package com.schizogenesis.antez;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import com.schizogenesis.antez.resourcebuilder.ImageResourceSection;
import com.schizogenesis.antez.resourcebuilder.ResourceSection;


/**
 * @author dimiter@schizogenesis.com
 * @since Jan 17, 2004  4:41:03 PM
 */
public class ResourceBuilder extends Task {
    private File codeoutput;
    private File destfile;
    private Collection resourceSections = new ArrayList();

    public void execute() throws BuildException {
        StringBuffer resourceIndex = new StringBuffer();
        StringBuffer containerVariables = new StringBuffer();
        StringBuffer loadMethod = new StringBuffer();
        try {
            OutputStream resourceFile = new BufferedOutputStream(new FileOutputStream(destfile));
            for (Iterator itSections = resourceSections.iterator(); itSections.hasNext();) {
                ResourceSection section = (ResourceSection) itSections.next();
                resourceIndex.append(section.generateIndexEntries());
                loadMethod.append(section.generateLoader());
                containerVariables.append(section.generateContainer());
                section.writeContent(resourceFile);
            }
            resourceFile.close();

            System.out.println("codeoutput = " + codeoutput);
            if (codeoutput!=null) {
                System.out.println("generating...");
                Writer codestream = new FileWriter(codeoutput);
                codestream.write(
                        "/**\n"+
                        " * <!-- " +
                        " * IMPORTANT: this is a generated file. Normaly it should be generated at \n" +
                        " *            build time, so please do not make any modifications and don't\n" +
                        " *            put it under source control system.\n" +
                        " * --> \n" +
                        " * " + getDescription() + "\n" +
                        " */\n"+
                        "class Resources {\n" +
                        containerVariables.toString() + "\n\n" +
                        "public static void load() throws java.io.IOException { new Resources(); } \n" +
                        "private Resources() throws java.io.IOException {" +
                        "   java.io.InputStream input = getClass().getResourceAsStream(\"/"+destfile.getName()+"\");\n"+
                        loadMethod.toString() +
                        "}\n\n" +
                        resourceIndex.toString() + "\n" +
                        "}"
                );
                codestream.flush();
                codestream.close();
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public void setCodeoutput(File codeoutput) { this.codeoutput = codeoutput; }
    public void setDestfile(File destfile) { this.destfile = destfile; }

    public void addImages(ImageResourceSection images){
        resourceSections.add(images);
    }


}



