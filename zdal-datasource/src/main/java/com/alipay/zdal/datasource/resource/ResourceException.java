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
package com.alipay.zdal.datasource.resource;

import com.alipay.zdal.datasource.resource.util.id.SerialVersion;

/**
 * This is the root exception for the exception hierarchy defined for the
 * connector architecture.
 *
 * A ResourceException contains three items, the first two of which are set from
 * the constructor. The first is a standard message string which is accessed via
 * the getMessage() method. The second is an errorCode which is accessed via the
 * getErrorCode() method. The third is a linked exception which provides more
 * information from a lower level in the resource manager. Linked exceptions are
 * accessed via get/setLinkedException.
 */
public class ResourceException extends Exception {
    /** @since 4.0.2 */
    static final long serialVersionUID;
    static {
        if (SerialVersion.version == SerialVersion.LEGACY)
            serialVersionUID = 4770679801401540475L;
        else
            serialVersionUID = 547071213627824490L;
    }

    /**
     * The error code
     */
    private String    errorCode;

    /**
     * The linked exception
     */
    private Exception linkedException;

    /**
     * Create an exception with a null reason.
     */
    public ResourceException() {
        super();
    }

    /**
     * Create an exception with a reason.
     * @param reason the reason
     */
    public ResourceException(String reason) {
        super(reason);
    }

    /**
     * Create an exception with a reason and an errorCode.
     * @param reason the reason
     * @param errorCode the error code
     */
    public ResourceException(String reason, String errorCode) {
        super(reason);
        this.errorCode = errorCode;
    }

    /**
     * Create an exception with a reason and an errorCode.
     * @param reason the reason
     * @param throwable the linked error
     */
    public ResourceException(String reason, Throwable throwable) {
        super(reason, throwable);
    }

    /**
     * Create an exception with a reason and an errorCode.
     * @param throwable the linked error
     */
    public ResourceException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Get the error code.
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get any linked exception.
     * @return the linked exception
     */
    public Exception getLinkedException() {
        return linkedException;
    }

    /**
     * Get the message composed of the reason and error code.
     * 
     * @return message composed of the reason and error code.
     */
    public String getMessage() {
        String msg = super.getMessage();
        String ec = getErrorCode();
        if ((msg == null) && (ec == null)) {
            return null;
        }
        if ((msg != null) && (ec != null)) {
            return (msg + ", error code: " + ec);
        }
        return ((msg != null) ? msg : ("error code: " + ec));
    }

    /**
     * Set the error code.
     * @param errorCode code the error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Set a linked exception.
     * @param linkedException the linked exception
     * @deprecated use initCause
     */
    public void setLinkedException(Exception linkedException) {
        this.linkedException = linkedException;
        initCause(linkedException);
    }
}