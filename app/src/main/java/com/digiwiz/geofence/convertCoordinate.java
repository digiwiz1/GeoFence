package com.digiwiz.geofence;

import java.text.DecimalFormat;

/**
 * Created by digiwiz on 2-10-2015.
 */
public class convertCoordinate {

    static public String convertLatitude(Double c) {
        //Format coordinate to 5 decimals (which represent an accuracy of 1.1 meter. See http://gis.stackexchange.com/questions/8650/how-to-measure-the-accuracy-of-latitude-and-longitude for more info)
        DecimalFormat f = new DecimalFormat("##.00000");
        String rslt;

        if (c < 0) {
            c = c * -1;
            rslt = "S " + f.format(c) + '\u00B0';
        } else
            rslt = "N " + f.format(c) + '\u00B0';

        return rslt;
    }

    static public String convertLongitude(Double c) {
        //Format coordinate to 5 decimals (which represent an accuracy of 1.1 meter. See http://gis.stackexchange.com/questions/8650/how-to-measure-the-accuracy-of-latitude-and-longitude for more info)
        DecimalFormat f = new DecimalFormat("##.00000");
        String rslt;

        if (c < 0) {
            c = c * -1;
            rslt = "E " + f.format(c) + '\u00B0';
        } else
            rslt = "W " + f.format(c) + '\u00B0';

        return rslt;
    }
}
