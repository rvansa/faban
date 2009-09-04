/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html or
 * install_dir/legal/LICENSE
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at install_dir/legal/LICENSE.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.services;

import com.sun.faban.common.Command;
import com.sun.faban.common.CommandHandle;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.harness.Configure;
import com.sun.faban.harness.Context;
import com.sun.faban.harness.Start;
import com.sun.faban.harness.Stop;
import com.sun.faban.harness.services.ServiceContext;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the service to start/stop Memcached instances.
 * It can be used by any benchmark to manage memcached servers and
 * perform these operations remotely using this Service.
 *
 * @author Sheetal Patil based on work done by Shanti Subramanyam.
 */

public class MemcachedService {
    
    @Context public ServiceContext ctx;
    Logger logger = Logger.getLogger(MemcachedService.class.getName());
    String memcachedCmdPath,  myServers[];
    List<NameValuePair<Integer>> myHostPorts;
    String memcachedStartCmd, memcachedBin, memcachedMemSize;
    private static final int DEFAULT_PORT = 11211;  // default port
    CommandHandle memcacheHandles[];

    @Configure public void configure() {        
        logger.info("Configuring memcached service ");
        myHostPorts = ctx.getUniqueHostPorts();
        memcachedCmdPath = ctx.getProperty("cmdPath");
        memcachedMemSize = ctx.getProperty("serverMemSize");
        /* if (!memcachedHome.endsWith(File.separator))
            memcachedHome = memcachedHome + File.separator;        
        memcachedBin = memcachedHome + "bin";*/
        memcachedStartCmd = memcachedCmdPath + " -u mysql -m " +
                memcachedMemSize;
        memcacheHandles = new CommandHandle[myHostPorts.size()];
        logger.info("MemcachedService Configure complete.");

    }

    @Start public void startup() {
        int i = 0;
        for (NameValuePair<Integer> myHostPort : myHostPorts) {
            logger.info("Starting memcached on " + myHostPort.name);
            Command startCmd;
            if (myHostPort.value != null) {
                startCmd = new Command(memcachedStartCmd + " -p " +
                        myHostPort.value);
            } else {
                startCmd = new Command(memcachedStartCmd + " -p " +
                        DEFAULT_PORT);
            }
            startCmd.setLogLevel(Command.STDOUT, Level.INFO);
            startCmd.setLogLevel(Command.STDERR, Level.INFO);
            logger.fine("Starting memcached with: " + memcachedStartCmd);
            startCmd.setSynchronous(false); // to run in bg
            try {
                // Run the command in the background
               memcacheHandles[i] = ctx.exec(myHostPort.name, startCmd);
               logger.info("Completed memcached server startup successfully on "
                        + myHostPort.name);
            } catch (Exception e) {
               logger.log(Level.WARNING, "Failed to start memcached on " +
                       myHostPort.name + '.', e);
            }
            ++i;
        }
    }

    @Stop public void shutdown() throws Exception {
        for (int i = 0; i < memcacheHandles.length; i++) {
            NameValuePair myHostPort = myHostPorts.get(i);
            if (memcacheHandles[i] != null) {
                try {
                    int exit = memcacheHandles[i].exitValue();

                    logger.warning("Memcached on " + myHostPort.name + ':' +
                            myHostPort.value + " has exited unexpectedly " +
                            "during run with exit value of " + exit);
                } catch (IllegalThreadStateException ie) {
                    // The server has not yet exited. Kill it
                    try {
                        memcacheHandles[i].destroy();
                        memcacheHandles[i] = null;
                    } catch (RemoteException re) {
                        logger.warning("Failed to stop memcached on " +
                                myHostPort.name + ':' + myHostPort.value +
                                " with " + re.toString());
                        logger.log(Level.FINE, "Exception", re);
                    }
                } catch (RemoteException re) {
                    logger.warning("exception while trying to get exitValue" +
                            "on " + myHostPort.name + ':' + myHostPort.value +
                            " - " + re.toString());
                    logger.log(Level.FINE, "Exception", re);
                }
            }
        }
    }
}
