package bj.discogsbrowser.marketplace;

import javax.inject.Singleton;

import bj.discogsbrowser.AppComponent;
import dagger.Component;

/**
 * Created by Josh Laird on 13/04/2017.
 */
@Singleton
@Component(dependencies = {AppComponent.class}, modules = {MarketplaceModule.class})
public interface MarketplaceComponent
{
    void inject(MarketplaceListingActivity marketplaceListingActivity);
}