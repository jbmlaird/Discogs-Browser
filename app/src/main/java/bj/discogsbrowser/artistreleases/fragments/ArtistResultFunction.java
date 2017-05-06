package bj.discogsbrowser.artistreleases.fragments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bj.discogsbrowser.model.artistrelease.ArtistRelease;
import io.reactivex.functions.Function;

/**
 * Created by Josh Laird on 11/04/2017.
 */

public class ArtistResultFunction
{
    @Inject
    public ArtistResultFunction()
    {

    }

    public Function<List<ArtistRelease>, List<ArtistRelease>> map(String parameterToMapTo)
    {
        return releases ->
        {
            ArrayList<ArtistRelease> remixes = new ArrayList<>();
            for (ArtistRelease release : releases)
            {
                if (release.getType().equals(parameterToMapTo))
                    remixes.add(release);
            }
            return remixes;
        };
    }
}