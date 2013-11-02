/*
 * Copyright (c) 2003 GBW KK
 * All Rights Reserved.
 *
 * Date: Jan 18, 2004
 * Time: 12:28:49 AM
 * $Id:;
 */
package com.schizogenesis.antez.resourcebuilder;

import java.io.OutputStream;
import java.io.IOException;

/**
 * @author dimiter@schizogenesis.com
 * @since Jan 18, 2004  12:28:49 AM
 */
public interface ResourceSection {

    /**
     * Generates a source fragment defining an array containing the loaded
     * resources of this resource section. The source fragment is inserted
     * at the class-level of the generated class.
     * Each section should have a container.
     *
     * @return source fragment declaring the container variable.
     */
    String generateContainer();


    /**
     * Generates a source fragment defining index constants, used as index
     * in this resource section's container. The source fragment is inserted
     * at the class-level of the generated class. It's not required that each
     * resource in the resource section has an index.
     *
     * <p>
     * Sample result:<br />
     * <code>
     * "public static final IDX_FOO = 5;\n public static final IDX_BAR = 6;"
     * </code>
     * </p>
     * <p>
     * These indices are used as offsets in the container array of this
     * resource section.
     * </p>
     * @return source fragment defining index constants, used as offset
     *        in this resource section's container.
     * @see #generateContainer()
     */
    String generateIndexEntries();

    /**
     * Generates a source fragment defining a loader of the resource section.
     * The source fragment is inserted in the load() method. It is required
     * that each resource section provides a loader.
     * <p>
     * In this source fragment you can reference the predefined variable "input",
     * which is an InputStream, providing the serialized data for this
     * resource section.
     * </p><p>
     * Make sure that you do not read more data then you have written in the
     * {@link #writeContent(java.io.OutputStream)} method.
     * </p><p>
     * Make sure that your loader has no side effects (i.e. leaving variables
     * at method scope). In order to limit the variable scope, it's recommended
     * to put all the code in a block (parentheses).
     * </p>
     *
     * @return source fragment defining a loader of the resource section.
     */
    String generateLoader();

    /**
     * Writes the binary resource to the output stream.
     * @param out the output stream
     */
    void writeContent(OutputStream out) throws IOException;

}
