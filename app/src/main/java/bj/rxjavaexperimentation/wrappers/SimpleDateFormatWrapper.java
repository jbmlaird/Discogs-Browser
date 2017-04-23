package bj.rxjavaexperimentation.wrappers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Josh Laird on 18/04/2017.
 */
@Singleton
public class SimpleDateFormatWrapper
{
    private static final String discogsDateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private DateFormat simpleDateFormat = new SimpleDateFormat(discogsDateFormat, Locale.getDefault());

    @Inject
    public SimpleDateFormatWrapper()
    {
    }

    public Date parse(String date) throws ParseException
    {
        return simpleDateFormat.parse(date);
    }
}