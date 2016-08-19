package module3;

//Java utilities libraries

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//import java.util.Collections;
//import java.util.Comparator;
//Processing library
//Unfolding libraries
//Parsing library

/**
 * EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 *
 * @author xSAVIKx
 *         Date: 19.08.2016
 */
public class EarthquakeCityMap extends PApplet {

    // You can ignore this.  It's to keep eclipse from generating a warning.
    private static final long serialVersionUID = 1L;

    // IF YOU ARE WORKING OFFLINE, change the value of this variable to true
    private static final boolean offline = false;

    // Less than this threshold is a light earthquake
    public static final float THRESHOLD_MODERATE = 5f;
    // Less than this threshold is a minor earthquake
    public static final float THRESHOLD_LIGHT = 4f;

    public static final int RED = Color.RED.getRGB();
    public static final int BLACK = Color.BLACK.getRGB();
    public static final int WHITE = Color.WHITE.getRGB();
    public static final int YELLOW = Color.YELLOW.getRGB();
    public static final int BLUE = Color.BLUE.getRGB();
    public static final float DEFAULT_SIZE = 10f;
    public static final float SMALL_SIZE = 7f;
    public static final float MEDIUM_SIZE = 12f;
    public static final float LARGE_SIZE = 15f;

    /**
     * This is where to find the local tiles, for working without an Internet connection
     */
    public static String mbTilesString = "blankLight-1-3.mbtiles";

    // The map
    private UnfoldingMap map;

    //feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_month.atom";


    public void setup() {
        size(950, 600, OPENGL);

        if (offline) {
            map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
            earthquakesURL = "2.5_week.atom";    // Same feed, saved Aug 7, 2015, for working offline
        } else {
            map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
            // IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
            //earthquakesURL = "2.5_week.atom";
        }

        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map);

        // The List you will populate with new SimplePointMarkers
        List<Marker> markers = new ArrayList<Marker>();

        //Use provided parser to collect properties for each earthquake
        //PointFeatures have a getLocation method
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
        for (PointFeature earthquake : earthquakes) {
            map.addMarker(createMarker(earthquake));
        }
    }

    // A suggested helper method that takes in an earthquake feature and
    // returns a SimplePointMarker for that earthquake
    // TODO: Implement this method and call it from setUp, if it helps
    private SimplePointMarker createMarker(PointFeature feature) {
        // finish implementing and use this method, if it helps.
        SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
        marker.setId(feature.getId());
        marker.setProperties(feature.getProperties());
        styleMarker(marker, feature);
        return marker;
    }

    private void styleMarker(SimplePointMarker marker, PointFeature earthquake) {
        Object magObj = earthquake.getProperty("magnitude");
        marker.setStrokeWeight(0);
        if (magObj == null) {
            marker.setColor(BLACK);
            marker.setRadius(DEFAULT_SIZE);
            return;
        }
        float mag = Float.parseFloat(magObj.toString());
        if (mag < THRESHOLD_LIGHT) {
            marker.setColor(BLUE);
            marker.setRadius(SMALL_SIZE);
        } else if (mag < THRESHOLD_MODERATE) {
            marker.setColor(YELLOW);
            marker.setRadius(MEDIUM_SIZE);
        } else {
            marker.setColor(RED);
            marker.setRadius(LARGE_SIZE);
        }
    }

    public void draw() {
        background(10);
        map.draw();
        addKey();
    }


    // helper method to draw key in GUI
    // TODO: Implement this method to draw the key
    private void addKey() {
        // Remember you can use Processing's graphics methods here
        fill(WHITE);
        rect(25, 50, 150, 200, 10);
        fill(BLACK);
        textSize(16f);
        text("Earthquake Key", 35, 85);

        textSize(12f);
        fill(RED);
        strokeWeight(0);
        ellipse(40, 110, LARGE_SIZE, LARGE_SIZE);
        fill(BLACK);
        text("5.0+ Magnitude", 55, 115);

        fill(YELLOW);
        ellipse(40, 150, MEDIUM_SIZE, MEDIUM_SIZE);
        fill(BLACK);
        text("4.0+ Magnitude", 55, 155);

        fill(BLUE);
        ellipse(40, 185, SMALL_SIZE, SMALL_SIZE);
        fill(BLACK);
        text("Below 4.0", 55, 190);
    }
}
