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
package com.lyndir.lhunath.snaplog.webapp.page.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackagedTextTemplate;

import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tab.Tab;
import com.lyndir.lhunath.snaplog.webapp.tab.TabProvider;


/**
 * <h2>{@link LayoutPageUtils}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 13, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class LayoutPageUtils {

    /**
     * Activate the given tab in the session and switch to it in the {@link LayoutPage}.
     * 
     * @param tab
     *            The tab to activate.
     * @param target
     *            Optional AJAX request target. If specified, the components that need to be reloaded to update the page
     *            appropriately will be added to the target.
     */
    public static void setActiveTab(TabProvider tab, AjaxRequestTarget target) {

        SnaplogSession.get().setActiveTab( tab );

        if (!LayoutPage.class.equals( RequestCycle.get().getResponsePageClass() ))
            throw new RedirectToPageException( LayoutPage.class );

        LayoutPage layoutPage = (LayoutPage) RequestCycle.get().getResponsePage();
        layoutPage.setContentPanel( getActiveTabPanel( LayoutPage.CONTENT_PANEL ), target );
    }

    /**
     * @param wicketId
     *            The wicket ID to create the panel with.
     * @return The panel for the active tab.
     * 
     * @see SnaplogSession#getActiveTab()
     */
    public static Panel getActiveTabPanel(String wicketId) {

        // Find the active tab.
        TabProvider activeTab = SnaplogSession.get().getActiveTab();
        if (activeTab == null)
            SnaplogSession.get().setActiveTab( activeTab = Tab.DESKTOP );

        return activeTab.getTab().getPanel( wicketId );
    }

    /**
     * Generate some JavaScript to track a user hit on the given component in the analytics tracker.
     * 
     * @param trackComponent
     *            The component that we want to track a hit for.
     * @return The JavaScript code that, when executed, will track the hit.
     */
    public static String trackJS(Component trackComponent) {

        Map<String, Object> trackVariables = new HashMap<String, Object>();
        trackVariables.put( "pageView", trackComponent.getClass().getSimpleName() );

        JavaScriptTemplate trackJS = new JavaScriptTemplate(
                new PackagedTextTemplate( LayoutPage.class, "trackPage.js" ) );

        return trackJS.asString( trackVariables );
    }
}