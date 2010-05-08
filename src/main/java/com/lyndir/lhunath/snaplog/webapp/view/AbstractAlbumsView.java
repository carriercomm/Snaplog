package com.lyndir.lhunath.snaplog.webapp.view;

import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.provider.AbstractListProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AbstractAlbumsView}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 23, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public abstract class AbstractAlbumsView extends DataView<Album> {

    @Inject
    AlbumService albumService;


    /**
     * Create a new {@link AbstractAlbumsView} instance.
     *
     * @param id            The wicket ID to bind this component on.
     * @param ownerUser     The model that provides the owner whose albums to enumerate.
     * @param albumsPerPage The maximum amount of albums to show at once before hiding the rest behind a pager.
     */
    protected AbstractAlbumsView(final String id, final IModel<User> ownerUser, final int albumsPerPage) {

        this( id, new IPredicate<Album>() {

            @Override
            public boolean apply(final Album input) {

                return input != null && ObjectUtils.equal( input.getOwnerProfile().getUser(), ownerUser.getObject() );
            }
        }, albumsPerPage );
    }

    /**
     * Create a new {@link AbstractAlbumsView} instance.
     *
     * @param id            The wicket ID to bind this component on.
     * @param predicate     The predicate that should evaluate to <code>true</code> for each album to return.
     * @param albumsPerPage The maximum amount of albums to show at once before hiding the rest behind a pager.
     */
    protected AbstractAlbumsView(final String id, final IPredicate<Album> predicate, final int albumsPerPage) {

        super( id, new AbstractListProvider<Album>() {

            @Override
            protected List<Album> load() {

                AlbumService albumService = GuiceContext.get().getInstance( AlbumService.class );
                return albumService.queryAlbums( SnaplogSession.get().newToken(), predicate );
            }

            @Override
            public IModel<Album> model(final Album object) {

                return new Model<Album>( object );
            }
        }, albumsPerPage );
    }

    /**
     * @param albumModel The model that provides the album whose cover to return.
     *
     * @return A new model that will provide the cover {@link Media} for the album provided by the given model.
     */
    public IModel<Media> cover(final IModel<Album> albumModel) {

        return new LoadableDetachableModel<Media>() {

            @Override
            protected Media load() {

                Iterator<Media> it = albumService.iterateFiles( SnaplogSession.get().newToken(),
                                                                albumModel.getObject() );
                if (it.hasNext())
                    return Iterators.getLast( it );

                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {

        return getItemCount() > 0;
    }
}
