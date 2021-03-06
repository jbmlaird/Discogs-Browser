package bj.vinylbrowser.search;

import org.jetbrains.annotations.NotNull;

import bj.vinylbrowser.di.scopes.FragmentScope;
import dagger.Subcomponent;

/**
 * Created by Josh Laird on 20/02/2017.
 */
@FragmentScope
@Subcomponent(modules = {SearchModule.class})
public interface SearchComponent
{
    void inject(@NotNull SearchController searchController);

    @Subcomponent.Builder
    interface Builder
    {
        Builder searchModule(SearchModule searchModule);

        SearchComponent build();
    }
}
