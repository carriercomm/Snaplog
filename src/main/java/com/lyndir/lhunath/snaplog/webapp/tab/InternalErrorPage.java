/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.snaplog.webapp.tab;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.model.Models;
import com.lyndir.lhunath.opal.wayward.navigation.AbstractTabState;
import com.lyndir.lhunath.opal.wayward.navigation.IncompatibleStateException;
import com.lyndir.lhunath.snaplog.data.object.Issue;
import com.lyndir.lhunath.snaplog.error.IssueNotFoundException;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.snaplog.model.service.IssueService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import com.lyndir.lhunath.snaplog.webapp.page.LayoutPage;
import com.lyndir.lhunath.snaplog.webapp.tool.SnaplogTool;
import java.util.List;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link InternalErrorPage}<br> <sub>Page that is shown when an uncaught exception occurs.</sub></h2>
 *
 * <p> <i>Jun 10, 2009</i> </p>
 *
 * @author lhunath
 */
public class InternalErrorPage extends LayoutPage {

    static final Logger logger = Logger.get( InternalErrorPage.class );

    final Issue issue;

    public InternalErrorPage(final Issue issue) {

        this.issue = issue;
    }

    @Override
    protected void onBeforeRender() {

        try {
            if (issue != null)
                getController().activateTabWithState( InternalErrorTabDescriptor.instance, new InternalErrorState( issue ) );
            else {
                // No issue; odd - something must have gone wrong while building error context. Just display a new error page without state.
                logger.wrn( "InternalErrorPage loaded without issue." );
                getController().activateNewTab( InternalErrorTabDescriptor.instance );
            }
        }
        catch (IncompatibleStateException e) {
            Session.get().error( e.getLocalizedMessage() );

            getController().activateNewTab( InternalErrorTabDescriptor.instance );
        }

        super.onBeforeRender();
    }

    static class InternalErrorTabPanel extends Panel {

        private Issue issue;

        InternalErrorTabPanel(final String id) {

            super( id );

            add( new TextField<String>( "issueCode", new LoadableDetachableModel<String>() {

                @Override
                protected String load() {

                    return issue.getIssueCode();
                }
            } ) {

                @Override
                public boolean isVisible() {

                    return super.isVisible() && issue != null;
                }
            } );
        }

        Issue getIssue() {

            return issue;
        }

        void setIssue(final Issue issue) {

            this.issue = issue;
        }
    }


    static class InternalErrorTabDescriptor implements SnaplogTabDescriptor<InternalErrorTabPanel, InternalErrorState> {

        public static final InternalErrorTabDescriptor instance = new InternalErrorTabDescriptor();

        @Override
        public List<? extends SnaplogTool> listTools(final InternalErrorTabPanel panel) {

            return ImmutableList.of();
        }

        @NotNull
        @Override
        public String getFragment() {

            return "error";
        }

        @NotNull
        @Override
        public InternalErrorState newState(@NotNull final InternalErrorTabPanel panel) {

            if (panel.getIssue() == null)
                return new InternalErrorState();

            return new InternalErrorState( panel.getIssue() );
        }

        @NotNull
        @Override
        public IModel<String> getTitle() {

            return Models.unsupportedOperation();
        }

        @Override
        public boolean shownInNavigation() {

            return true;
        }

        @NotNull
        @Override
        public Class<InternalErrorTabPanel> getContentPanelClass() {

            return InternalErrorTabPanel.class;
        }

        @NotNull
        @Override
        public InternalErrorState newState(@NotNull final String fragment) {

            return new InternalErrorState( fragment );
        }
    }


    static class InternalErrorState extends AbstractTabState<InternalErrorTabPanel> {

        private final IssueService issueService = GuiceContext.getInstance( IssueService.class );

        private final String issueCode;

        InternalErrorState() {

            issueCode = null;
            logger.dbg( "Created error state without fragments: %s", getStateFragments() );
        }

        InternalErrorState(final String fragment) {

            super( fragment );

            issueCode = findFragment( 1 );
        }

        InternalErrorState(final Issue issue) {

            checkNotNull( issue, "Issue can't be null when creating state based on it." );

            appendFragment( issueCode = issue.getIssueCode() );
            logger.dbg( "Created error state with fragments: %s", getStateFragments() );
        }

        @Nullable
        public Issue findIssue()
                throws IssueNotFoundException, PermissionDeniedException {

            if (issueCode == null)
                return null;

            return issueService.getIssue( SnaplogSession.get().newToken(), issueCode );
        }

        @Override
        public void apply(@NotNull final InternalErrorTabPanel panel)
                throws IncompatibleStateException {

            try {
                panel.setIssue( findIssue() );
            }

            catch (IssueNotFoundException e) {
                throw new IncompatibleStateException( e );
            }
            catch (PermissionDeniedException e) {
                throw new IncompatibleStateException( e );
            }
        }
    }
}
