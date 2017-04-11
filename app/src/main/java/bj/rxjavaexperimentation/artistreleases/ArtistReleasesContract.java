package bj.rxjavaexperimentation.artistreleases;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

/**
 * Created by Josh Laird on 10/04/2017.
 */

public interface ArtistReleasesContract
{
    interface View {}

    interface Presenter
    {
        void getArtistReleases(String id);

        void setupViewPager(TabLayout tabLayout, ViewPager viewPager, FragmentManager supportFragmentManager);
    }
}
