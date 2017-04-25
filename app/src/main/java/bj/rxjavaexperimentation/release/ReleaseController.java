package bj.rxjavaexperimentation.release;

import android.content.Context;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import bj.rxjavaexperimentation.epoxy.common.BaseController;
import bj.rxjavaexperimentation.epoxy.common.SubDividerModel_;
import bj.rxjavaexperimentation.epoxy.release.MarketplaceListingsHeader_;
import bj.rxjavaexperimentation.epoxy.release.MarketplaceModel_;
import bj.rxjavaexperimentation.epoxy.release.NoListingsModel_;
import bj.rxjavaexperimentation.epoxy.release.TrackModel_;
import bj.rxjavaexperimentation.main.epoxy.DividerModel_;
import bj.rxjavaexperimentation.main.epoxy.LoadingModel_;
import bj.rxjavaexperimentation.main.epoxy.ViewMoreModel_;
import bj.rxjavaexperimentation.model.listing.ScrapeListing;
import bj.rxjavaexperimentation.model.release.Release;
import bj.rxjavaexperimentation.model.release.Track;
import bj.rxjavaexperimentation.utils.ArtistsBeautifier;
import bj.rxjavaexperimentation.utils.ImageViewAnimator;

/**
 * Created by Josh Laird on 24/04/2017.
 */
@Singleton
public class ReleaseController extends BaseController
{
    private final Context context;
    private final ReleaseContract.View view;
    private ArtistsBeautifier artistsBeautifier;
    private ImageViewAnimator imageViewAnimator;
    private Release release;
    private ArrayList<ScrapeListing> releaseListings;
    private boolean viewFullTracklist = false;
    private boolean isError = false;
    private boolean marketplaceLoading = true;
    private boolean viewAllListings = false;

    @Inject
    public ReleaseController(Context context, ReleaseContract.View view, ArtistsBeautifier artistsBeautifier, ImageViewAnimator imageViewAnimator)
    {
        this.context = context;
        this.view = view;
        this.artistsBeautifier = artistsBeautifier;
        this.imageViewAnimator = imageViewAnimator;
    }

    @Override
    protected void buildModels()
    {
        header
                .context(context)
                .title(title)
                .subtitle(subtitle)
                .imageUrl(imageUrl)
                .addTo(this);

        new DividerModel_()
                .id("divider1")
                .addTo(this);

        if (release != null)
        {
            for (Track track : release.getTracklist())
            {
                new TrackModel_()
                        .id("track" + release.getTracklist().indexOf(track))
                        .trackName(track.getTitle())
                        .trackNumber(track.getPosition())
                        .addTo(this);

                if (release.getTracklist().indexOf(track) == 4 && release.getTracklist().size() > 5 && !viewFullTracklist)
                {
                    new ViewMoreModel_()
                            .id("view more")
                            .title("View full tracklist")
                            .onClickListener(v -> setViewFullTracklist(true))
                            .addTo(this);
                    break;
                }
            }

            new DividerModel_()
                    .id("tracklist divider")
                    .addTo(this);

            new MarketplaceListingsHeader_()
                    .id("marketplace listings header")
                    .lowestPrice(release.getLowestPriceString())
                    .numForSale(String.valueOf(release.getNumForSale()))
                    .addTo(this);

            new LoadingModel_(imageViewAnimator)
                    .id("loading")
                    .addIf(marketplaceLoading, this);

            if (releaseListings != null)
            {
                if (releaseListings.size() == 0)
                    new NoListingsModel_()
                            .id("no listings")
                            .addTo(this);
                else
                {

                    for (ScrapeListing scrapeListing : releaseListings)
                    {
                        new MarketplaceModel_()
                                .id("release" + releaseListings.indexOf(scrapeListing))
                                .sleeve(scrapeListing.getSleeveCondition())
                                .media(scrapeListing.getMediaCondition())
                                .shipsFrom(scrapeListing.getShipsFrom())
                                .sellerName(scrapeListing.getSellerName())
                                .sellerRating(scrapeListing.getSellerRating())
                                .convertedPrice(scrapeListing.getConvertedPrice())
                                .price(scrapeListing.getPrice())
                                .onClickListener(v -> view.displayListingInformation(title, subtitle, scrapeListing))
                                .addTo(this);

                        new SubDividerModel_()
                                .id("marketplace divider" + releaseListings.indexOf(scrapeListing))
                                .addIf(releaseListings.indexOf(scrapeListing) != releaseListings.size() - 1, this);

                        if (releaseListings.indexOf(scrapeListing) == 4 && !viewAllListings && releaseListings.size() > 5)
                        {
                            new ViewMoreModel_()
                                    .id("view all")
                                    .title("View all label releases")
                                    .onClickListener(v -> setViewListings(true))
                                    .addTo(this);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setRelease(Release release)
    {
        this.release = release;
        if (release.getImages() != null)
            this.imageUrl = release.getImages().get(0).getUri();
        this.subtitle = artistsBeautifier.formatArtists(release.getArtists());
        requestModelBuild();
    }

    public void setReleaseListings(ArrayList<ScrapeListing> releaseListings)
    {
        this.releaseListings = releaseListings;
        marketplaceLoading = false;
        isError = false;
        requestModelBuild();
    }

    public void setReleaseListingsError()
    {
        isError = true;
        marketplaceLoading = false;
        requestModelBuild();
    }

    private void setViewFullTracklist(boolean viewFullTracklist)
    {
        this.viewFullTracklist = viewFullTracklist;
        requestModelBuild();
    }

    private void setViewListings(boolean viewFullListings)
    {
        this.viewAllListings = viewFullListings;
        requestModelBuild();
    }
}