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
package com.lyndir.lhunath.snaplog.webapp;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;

import com.google.inject.Inject;
import com.lyndir.lhunath.snaplog.data.LinkID;
import com.lyndir.lhunath.snaplog.data.Provider;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.util.WicketUtils;


/**
 * <h2>{@link AuthenticationListener}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Jan 2, 2010</i>
 * </p>
 * 
 * @param <P>
 *            The type of {@link Provider} that we can load.
 * @author lhunath
 */
public class AuthenticationListener implements IComponentOnBeforeRenderListener {

    final UserService<Provider> userService;


    /**
     * @param userService
     *            See {@link UserService}
     */
    @Inject
    public AuthenticationListener(UserService<Provider> userService) {

        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBeforeRender(Component component) {

        String currentLinkID = WicketUtils.findLinkID();
        if (currentLinkID == null)
            SnaplogSession.get().setActiveUser( null );
        else
            SnaplogSession.get().setActiveUser( userService.findUserWithLinkID( new LinkID( currentLinkID ) ) );
    }
}
