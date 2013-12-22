/* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package com.alipay.zdal.datasource.resource;

import javax.naming.Reference;

/**
 * The Referenceable interface extends the javax.naming.Referenceable
 * interface. It enables support for the JNDI Reference mechanism for the
 * registration of the connection factory in the JNDI name space. Note that the
 * implementation and structure of a Reference is specific to an application
 * server.
 * 
 * The implementation class for a connection factory interface is required to
 * implement both the java.io.Serializable and the javax.resource.Referenceable
 * interfaces to support JNDI registration.
 */
public interface Referenceable extends javax.naming.Referenceable
{
   /**
	 * Sets the reference instance
	 * 
	 * @param reference the reference
	 */
   void setReference(Reference reference);
}
