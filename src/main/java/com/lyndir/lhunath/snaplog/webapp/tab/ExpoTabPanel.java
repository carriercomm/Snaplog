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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.AppendingStringBuffer;

import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.component.GenericLabel;
import com.lyndir.lhunath.lib.wayward.component.GenericPanel;
import com.lyndir.lhunath.lib.wayward.i18n.BooleanKeyAppender;
import com.lyndir.lhunath.lib.wayward.i18n.MessagesFactory;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media.Quality;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.model.UserService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.page.util.LayoutPageUtils;
import com.lyndir.lhunath.snaplog.webapp.tab.model.ExpoTabModels;
import com.lyndir.lhunath.snaplog.webapp.view.AbstractAlbumsView;
import com.lyndir.lhunath.snaplog.webapp.view.AbstractUsersView;
import com.lyndir.lhunath.snaplog.webapp.view.MediaView;
import com.lyndir.lhunath.snaplog.webapp.view.UserLink;


/**
 * <h2>{@link ExpoTabPanel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 1, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class ExpoTabPanel extends GenericPanel<ExpoTabModels> {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    static final int USERS_PER_PAGE = 3;
    static final int ALBUMS_PER_PAGE = 5;

    @Inject
    UserService userService;

    @Inject
    AlbumService albumService;

    // TODO: Remove when <https://issues.apache.org/jira/browse/WICKET-2797> is fixed.
    Form<?> searchForm;


    /**
     * Create a new {@link ExpoTabPanel} instance.
     * 
     * @param id
     *            The wicket ID that will hold the {@link ExpoTabPanel}.
     */
    public ExpoTabPanel(String id) {

        super( id, new ExpoTabModels().getModel() );
        getModelObject().attach( this );

        add( new Label( "usersHelp", getModelObject().usersHelp() ) );
        add( new AbstractUsersView( "users", USERS_PER_PAGE ) {

            @Override
            protected void populateItem(final Item<User> userItem) {

                userItem.add( new UserLink( "userName", userItem.getModel() ) );
                userItem.add( new AbstractAlbumsView( "albums", userItem.getModel(), ALBUMS_PER_PAGE ) {

                    @Override
                    protected void populateItem(Item<Album> albumItem) {

                        albumItem.add( new MediaView( "albumCover", cover( albumItem.getModel() ), Quality.THUMBNAIL,
                                true ) {

                            @Override
                            protected void onClick(AjaxRequestTarget target) {

                                SnaplogSession.get().setFocussedAlbum( getModelObject().getAlbum() );
                                LayoutPageUtils.setActiveTab( Tab.ALBUM, target );
                            }

                            @Override
                            protected String getCaptionString() {

                                return getModelObject().getAlbum().getName();
                            }
                        } );
                    }

                    @Override
                    public boolean isVisible() {

                        // userItem's visibility == the visibility of the albums view in it.
                        boolean visible = super.isVisible();
                        userItem.setVisible( visible );

                        return visible;
                    }
                } );
            }
        } );

        add( (searchForm = new Form<String>( "searchForm", new Model<String>() ) {

            AbstractUsersView usersView;
            AbstractAlbumsView albumsView;

            {
                // Search Query
                final IModel<String> queryModel = getModel();
                add( new RequiredTextField<String>( "query", queryModel ) );

                // Results
                add( new GenericLabel<Integer>( "results", new LoadableDetachableModel<Integer>() {

                    @Override
                    protected Integer load() {

                        return usersView.getItemCount() + albumsView.getItemCount();
                    }
                } ) {

                    @Override
                    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {

                        int resultCount = getModelObject();
                        if (resultCount == 0)
                            replaceComponentTagBody( markupStream, openTag, //
                                                     msgs.noResults() );
                        else if (resultCount == 1)
                            replaceComponentTagBody( markupStream, openTag, //
                                                     msgs.singularResult( resultCount ) );
                        else
                            replaceComponentTagBody( markupStream, openTag, //
                                                     msgs.multipleResults( resultCount ) );
                    }

                    @Override
                    public boolean isVisible() {

                        return queryModel.getObject() != null && queryModel.getObject().length() > 0;
                    }
                } );

                // Found Users
                add( usersView = new AbstractUsersView( "users", new IPredicate<User>() {

                    @Override
                    public boolean apply(User input) {

                        // Applies for a user whose userName contains the search string (case insensitively).
                        return input != null && queryModel.getObject() != null && queryModel.getObject().length() > 0
                               && input.getUserName().toUpperCase().contains( queryModel.getObject().toUpperCase() );
                    }
                }, USERS_PER_PAGE ) {

                    @Override
                    protected void populateItem(final Item<User> userItem) {

                        userItem.add( new UserLink( "userName", userItem.getModel() ) );
                        userItem.add( new AbstractAlbumsView( "albums", userItem.getModel(), ALBUMS_PER_PAGE ) {

                            @Override
                            protected void populateItem(Item<Album> albumItem) {

                                albumItem.add( new MediaView( "albumCover", cover( albumItem.getModel() ),
                                        Quality.THUMBNAIL, true ) {

                                    @Override
                                    protected void onClick(AjaxRequestTarget target) {

                                        SnaplogSession.get().setFocussedAlbum( getModelObject().getAlbum() );
                                        LayoutPageUtils.setActiveTab( Tab.ALBUM, target );
                                    }

                                    @Override
                                    protected String getCaptionString() {

                                        return getModelObject().getAlbum().getName();
                                    }
                                } );
                            }
                        } );
                    }
                } );

                // Found Albums
                add( albumsView = new AbstractAlbumsView( "albums", new IPredicate<Album>() {

                    @Override
                    public boolean apply(Album input) {

                        // Applies for an album whose name contains the search string (case insensitively).
                        return input != null && queryModel.getObject() != null && queryModel.getObject().length() > 0
                               && input.getName().toUpperCase().contains( queryModel.getObject().toUpperCase() );
                    }
                }, ALBUMS_PER_PAGE ) {

                    @Override
                    protected void populateItem(Item<Album> albumItem) {

                        albumItem.add( new MediaView( "cover", cover( albumItem.getModel() ), Quality.THUMBNAIL, true ) {

                            @Override
                            public void onClick(AjaxRequestTarget target) {

                                SnaplogSession.get().setFocussedAlbum( getModelObject().getAlbum() );
                                LayoutPageUtils.setActiveTab( Tab.ALBUM, target );
                            }

                            @Override
                            protected String getCaptionString() {

                                return getModelObject().getAlbum().getName();
                            }
                        } );
                    }
                } );
            }
        }).add( new AjaxFormSubmitBehavior( searchForm, "onsubmit" ) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {

                target.addComponent( getForm() );
            }

            @Override
            protected void onError(AjaxRequestTarget target) {

            // TODO: Feedback.
            }

            @Override
            protected CharSequence getEventHandler() {

                // Prevents the form from generating an http request.
                // If we do not provide this, the AJAX event is processed AND the form still gets submitted.
                // FIXME: Ugly. Should probably be moved into AjaxFormSubmitBehaviour.
                return new AppendingStringBuffer( super.getEventHandler() ).append( "; return false;" );
            }
        } ).setOutputMarkupId( true ) );
    }


    /**
     * <h2>{@link Messages}<br>
     * <sub>[in short] (TODO).</sub></h2>
     * 
     * <p>
     * <i>Mar 31, 2010</i>
     * </p>
     * 
     * @author lhunath
     */
    public static interface Messages {

        /**
         * @return Text on the interface tab to activate the {@link ExpoTabPanel}.
         */
        String expoTab();

        /**
         * @return The text to show when a search yields no results.
         */
        String noResults();

        /**
         * @param resultCount
         *            The amount of results yielded by the search (should be singular).
         * @return The text to show when a search yields a single result.
         */
        String singularResult(int resultCount);

        /**
         * @param resultCount
         *            The amount of results yielded by the search (should be plural).
         * @return The text to show when a search yields multiple results.
         */
        String multipleResults(int resultCount);

        /**
         * @param authenticated
         *            <code>true</code>: The current user has authenticated himself.<br>
         *            <code>false</code>: The current user has not identified himself.
         * @return The text that explains which albums are being shown.
         */
        String usersHelp(@BooleanKeyAppender(y = "auth", n = "anon") boolean authenticated);
    }
}


/**
 * <h2>{@link ExpoTab}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>May 31, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
class ExpoTab implements SnaplogTab {

    static final Logger logger = Logger.get( ExpoTab.class );
    static final ExpoTabPanel.Messages msgs = MessagesFactory.create( ExpoTabPanel.Messages.class, ExpoTabPanel.class );


    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<String> getTitle() {

        return new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return msgs.expoTab();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getPanel(String panelId) {

        return new ExpoTabPanel( panelId );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Panel getTools(String panelId) {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return true;
    }
}
