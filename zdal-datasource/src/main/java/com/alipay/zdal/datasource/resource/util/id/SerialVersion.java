/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package com.alipay.zdal.datasource.resource.util.id;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.log4j.Logger;

/**
 * Serialization version compatibility mode constants.<p>
 *
 * Contains static constants and attributes to help with serialization
 * versioning.<p>
 * 
 * Set the system property <pre>org.jboss.j2ee.LegacySerialization</pre>
 * to serialization compatibility with jboss-4.0.1 and earlier. The
 * serialVersionUID values were synched with the j2ee 1.4 ri classes and
 * explicitly set in jboss-4.0.2 which is what
 *
 * @author  <a href="mailto:Adrian.Brock@JBoss.com">Adrian Brock</a>.
 * @version $Revision: 37390 $
 */
public class SerialVersion {
    private static final Logger logger    = Logger.getLogger(SerialVersion.class);
    // Static --------------------------------------------------------

    /** Legacy, jboss-4.0.1 through jboss-4.0.0 */
    public static final int     LEGACY    = 0;

    /** The serialization compatible with Sun's RI, jboss-4.0.2+ */
    public static final int     JBOSS_402 = 1;

    /**
     * The serialization version to use
     */
    public static int           version   = JBOSS_402;

    /** Determine the serialization version */
    static {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    if (System.getProperty("org.jboss.j2ee.LegacySerialization") != null)
                        version = LEGACY;
                } catch (Throwable ignored) {
                    logger.error(ignored);
                }
                return null;
            }
        });
    }
}
