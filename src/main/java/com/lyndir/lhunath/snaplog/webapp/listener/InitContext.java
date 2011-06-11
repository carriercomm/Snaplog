/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.webapp.listener;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.snaplog.data.service.InitDAO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * <h2>{@link InitContext}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 11, 2010</i> </p>
 *
 * @author lhunath
 */
public class InitContext implements ServletContextListener {

    static final Logger logger = Logger.get( InitContext.class );

    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        GuiceContext.get( sce ).getInstance( InitDAO.class ).initialize();
        logger.inf( "Database initialization completed." );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        GuiceContext.get( sce ).getInstance( InitDAO.class ).shutdown();
    }
}
