package com.lyndir.lhunath.snaplog.webapp.view;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.lyndir.lhunath.lib.system.collection.SizedIterator;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.collection.IPredicate;
import com.lyndir.lhunath.lib.wayward.provider.AbstractSizedIteratorProvider;
import com.lyndir.lhunath.snaplog.data.media.Album;
import com.lyndir.lhunath.snaplog.data.media.Media;
import com.lyndir.lhunath.snaplog.data.user.User;
import com.lyndir.lhunath.snaplog.model.AlbumService;
import com.lyndir.lhunath.snaplog.webapp.SnaplogSession;
import com.lyndir.lhunath.snaplog.webapp.listener.GuiceContext;
import java.util.Iterator;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link AbstractAlbumsView}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 23, 2010</i> </p>
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

        super( id, new AbstractSizedIteratorProvider<Album>() {

            @Override
            protected SizedIterator<Album> load() {

                return GuiceContext.getInstance( AlbumService.class ).iterateAlbums( SnaplogSession.get().newToken(), predicate );
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

                Iterator<Media> it = albumService.iterateMedia( SnaplogSession.get().newToken(), albumModel.getObject() );
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
