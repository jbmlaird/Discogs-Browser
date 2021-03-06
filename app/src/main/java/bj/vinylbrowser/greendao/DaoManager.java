package bj.vinylbrowser.greendao;

import android.text.TextUtils;

import java.util.Date;
import java.util.List;

import bj.vinylbrowser.model.release.Release;
import bj.vinylbrowser.utils.ArtistsBeautifier;

/**
 * Created by Josh Laird on 07/05/2017.
 * <p>
 * Wrapper for interacting with GreenDAO databases.
 */
public class DaoManager
{
    private ViewedReleaseDao viewedReleaseDao;
    private SearchTermDao searchTermDao;

    public DaoManager(DaoSession daoSession)
    {
        viewedReleaseDao = daoSession.getViewedReleaseDao();
        searchTermDao = daoSession.getSearchTermDao();
    }

    /**
     * @return Viewed releases in descending order by date
     */
    public List<ViewedRelease> getViewedReleases()
    {
        return viewedReleaseDao.queryBuilder().orderDesc(ViewedReleaseDao.Properties.Date).build().list();
    }

    public void storeViewedRelease(Release release, ArtistsBeautifier artistsBeautifier)
    {
        ViewedRelease viewedRelease = new ViewedRelease();
        if (release.getStyles().size() > 0 && release.getGenres().size() > 0)
            viewedRelease.setStyle(TextUtils.join(",", release.getStyles()));
        viewedRelease.setReleaseId(release.getId());
        if (release.getImages().size() > 0 && release.getImages().size() > 0)
            viewedRelease.setThumbUrl(release.getImages().get(0).getResourceUrl());
        else
            viewedRelease.setThumbUrl(release.getThumb());
        viewedRelease.setDate(new Date());
        viewedRelease.setReleaseName(release.getTitle());
        if (release.getLabels().size() > 0 && release.getLabels().size() > 0)
            viewedRelease.setLabelName(release.getLabels().get(0).getName());
        if (release.getArtists().size() > 0 && release.getArtists().size() > 0)
            viewedRelease.setArtists(artistsBeautifier.formatArtists(release.getArtists()));

        // Delete the last entry (oldest) to preserve 12 items in recently viewed
        List<ViewedRelease> viewedReleases = getViewedReleases();
        if (viewedReleases.size() == 12)
            viewedReleaseDao.delete(viewedReleases.get(11));
        viewedReleaseDao.insertOrReplace(viewedRelease);
    }

    /**
     * @return Recent search terms in descending order by date
     */
    public List<SearchTerm> getRecentSearchTerms()
    {
        return searchTermDao.queryBuilder().orderDesc(SearchTermDao.Properties.Date).build().list();
    }

    public void storeSearchTerm(CharSequence charSequence)
    {
        SearchTerm searchTerm = new SearchTerm();
        searchTerm.setSearchTerm(charSequence.toString());
        searchTerm.setDate(new Date());
        // Delete the last entry (oldest) to preserve 12 items in recent searches
        List<SearchTerm> recentSearchTerms = getRecentSearchTerms();
        if (recentSearchTerms.size() == 12)
            searchTermDao.delete(recentSearchTerms.get(11));
        searchTermDao.insertOrReplace(searchTerm);
    }

    public void clearRecentSearchTerms()
    {
        List<SearchTerm> recentSearchTerms = getRecentSearchTerms();
        int numberOfSearchTerms = recentSearchTerms.size();
        for (int i = numberOfSearchTerms - 1; i >= 0; i--)
        {
            searchTermDao.delete(recentSearchTerms.get(i));
        }
    }

    public void clearViewedReleases()
    {
        List<ViewedRelease> recentViewedReleases = getViewedReleases();
        int numberOfSearchTerms = recentViewedReleases.size();
        for (int i = numberOfSearchTerms - 1; i >= 0; i--)
        {
            viewedReleaseDao.delete(recentViewedReleases.get(i));
        }
    }
}
