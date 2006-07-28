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
 * $Id: WebBenchmark.java,v 1.3 2006/07/28 07:35:24 akara Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package sample.harness;

import com.sun.faban.harness.DefaultFabanBenchmark;

/**
 * Harness hook for the sample web benchmark. This class is not needed
 * for benchmarks implemented using the Faban Driver Framework if the
 * default behavior is sufficient. We just show the hooks you can
 * customize in this class. If the default behavior is desired, you can
 * leave out the benchmark-class element in benchmark.xml.
 *
 * @author Akara Sucharitakul
 */
public class WebBenchmark extends DefaultFabanBenchmark {

    /**
     * This method is called to configure the specific benchmark run
     * Tasks done in this method include reading user parameters,
     * logging them and initializing various local variables.
     *
     * @throws Exception If configuration was not successful
     */
    public void configure() throws Exception {
        // Add additional configuration needs such as restarting/reconfiguring
        // servers here.
        super.configure();
    }

    /**
     * This method is responsible for starting the benchmark run
     */
    public void start() throws Exception {
        // Any changes in start policy are added here.
        super.start();
    }

    /**
     * This method aborts the current benchmark run and is
     * called when a user asks for a run to be killed
     */
    public void kill() throws Exception {
        // Unlikely, but just in case, you'll want to customize the kill.
        super.kill();
    }

}