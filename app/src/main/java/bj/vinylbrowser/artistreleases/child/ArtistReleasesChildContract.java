package bj.vinylbrowser.artistreleases.child;

import bj.vinylbrowser.common.BasePresenter;

/**
 * Created by Josh Laird on 11/05/2017.
 */
public interface ArtistReleasesChildContract
{
    interface View
    {
    }

    interface Presenter extends BasePresenter
    {
        void connectToBehaviorRelay(String searchFilter);

        void bind(ArtistReleasesChildController childController);
    }
}
