/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package vertgreen.agent;

import vertgreen.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by napster on 01.05.17.
 * <p>
 * Tries to recover the database from a failed state
 */

public class DBConnectionWatchdogAgent extends Thread {

    private static final Logger log = LoggerFactory.getLogger(DBConnectionWatchdogAgent.class);
    private static final int INTERVAL_MILLIS = 5000; // 5 sec

    private boolean shutdown = false;

    private DatabaseManager dbManager;

    public DBConnectionWatchdogAgent(DatabaseManager dbManager) {
        super(DBConnectionWatchdogAgent.class.getSimpleName());
        setDaemon(true);
        this.dbManager = dbManager;
    }

    @Override
    public void run() {
        log.info("Started database connection agent");

        while (!shutdown) {
            try {
                sleep(INTERVAL_MILLIS);

                //we have to proactively call this, as it checks the ssh tunnel for connectivity and does a validation
                //query against the DB
                //the ssh tunnel does detect a disconnect, but doesn't provide a callback for that, so we have to check
                //it ourselves
                dbManager.isAvailable();

                //only recover the database from a failed state
                if (dbManager.getState() == DatabaseManager.DatabaseState.FAILED) {
                    log.info("Attempting to recover failed database connection");
                    dbManager.reconnectSSH();
                }
            } catch (Exception e) {
                log.error("Caught an exception while trying to recover database connection!", e);
            }
        }
    }

    public void shutdown() {
        shutdown = true;
    }
}
