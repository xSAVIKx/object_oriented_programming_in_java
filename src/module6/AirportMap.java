package module6;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An applet that shows airports (and routes)
 * on a world map.
 *
 * @author Iurii Sergiichuk
 */
public class AirportMap extends PApplet {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private UnfoldingMap map;
    private List<Marker> routeList;
    private List<Marker> airportList;

    private CommonMarker lastSelected;
    private AirportMarker lastClickedAirport;
    private UnfoldingMap map1;
    private UnfoldingMap map2;
    private UnfoldingMap map3;

    public void setup() {
        // setting up PAppler
        size(WIDTH, HEIGHT, OPENGL);

        // setting up map and default events

        map1 = new UnfoldingMap(this, 200, 50, 800, 600, new OpenStreetMap.OpenStreetMapProvider());
        map2 = new UnfoldingMap(this, 200, 50, 800, 600, new Google.GoogleMapProvider());
        map3 = new UnfoldingMap(this, 200, 50, 800, 600, new Microsoft.AerialProvider());
        MapUtils.createDefaultEventDispatcher(this, map1, map2, map3);
        map = map1;


        List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");

        airportList = new ArrayList<>();
        HashMap<Integer, AirportMarker> airports = new HashMap<>();

        for (PointFeature feature : features) {
            AirportMarker m = new AirportMarker(feature);

            m.setRadius(5);
            airportList.add(m);
            airports.put(Integer.parseInt(feature.getId()), m);

        }


        // parse route data
        List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
        routeList = new ArrayList<>();
        for (ShapeFeature route : routes) {

            // get source and destination airportIds
            int source = Integer.parseInt(route.getStringProperty("source"));
            int dest = Integer.parseInt(route.getStringProperty("destination"));

            // get locations for airports on route
            AirportMarker sourceAirportMarker = airports.get(source);
            AirportMarker destAirportMarker = airports.get(dest);
            if (sourceAirportMarker != null && destAirportMarker != null) {
                route.addLocation(sourceAirportMarker.getLocation());
                route.addLocation(destAirportMarker.getLocation());
            }

            SimpleLinesMarker sl = new RouteMarker(route.getLocations(), route.getProperties());

            System.out.println(sl.getProperties());
            sl.setHidden(true);
            routeList.add(sl);

            if (sourceAirportMarker != null) {
                sourceAirportMarker.routes.add(sl);
            }
        }

        map.addMarkers(routeList);

        map.addMarkers(airportList);
        map2.addMarkers(map.getMarkers());
        map3.addMarkers(map.getMarkers());
    }

    public void draw() {
        background(0);
        map.draw();
        addKey();
    }

    public void keyPressed() {
        if (key == '1') {
            map = map1;
        } else if (key == '2') {
            map = map2;
        } else if (key == '3') {
            map = map3;
        }
    }

    /**
     * Event handler that gets called automatically when the
     * mouse moves.
     */
    @Override
    public void mouseMoved() {
        // clear the last selection

        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;

        }
        selectMarkerIfHover(airportList);
        //loop();
    }

    // If there is a marker selected
    private void selectMarkerIfHover(List<Marker> markers) {
        // Abort if there's already a marker selected
        if (lastSelected != null) {
            return;
        }

        for (Marker m : markers) {
            CommonMarker marker = (CommonMarker) m;
            if (marker.isInside(map, mouseX, mouseY)) {
                lastSelected = marker;
                marker.setSelected(true);
                return;
            }
        }
    }

    private void unhideAirports() {
        for (Marker marker : airportList) {
            marker.setHidden(false);
        }
    }

    private void hideRoutes() {
        for (SimpleLinesMarker route : lastClickedAirport.routes) {
            route.setHidden(true);
        }
    }

    @Override
    public void mouseClicked() {
        if (lastClickedAirport != null) {
            lastClickedAirport.setClicked(false);
            unhideAirports();
            hideRoutes();
            lastClickedAirport = null;
        } else {
            checkAirportForClick();
            showRelatedRoutesAndAirports();
        }
    }

    private void showRelatedRoutesAndAirports() {
        if (lastClickedAirport != null) {
            for (SimpleLinesMarker route : lastClickedAirport.routes) {
                route.setHidden(false);
                String destinationAirportId = route.getStringProperty("destination");
                for (Marker marker : airportList) {
                    if (marker.getId().equalsIgnoreCase(destinationAirportId)) {
                        marker.setHidden(false);
                        break;
                    }
                }
            }
        }
    }

    private void checkAirportForClick() {
        if (lastClickedAirport != null) return;
        // Loop over the earthquake markers to see if one of them is selected
        for (Marker marker : airportList) {
            if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
                lastClickedAirport = (AirportMarker) marker;
                lastClickedAirport.setClicked(true);
                // Hide all the other earthquakes and hide
                for (Marker mhide : airportList) {
                    if (mhide != lastClickedAirport) {
                        mhide.setHidden(true);
                    }
                }
                return;
            }
        }
    }

    private void addKey() {
        fill(255, 250, 240);

        int xbase = 25;
        int ybase = 50;

        rect(xbase, ybase, 150, 250);

        fill(0);
        textAlign(LEFT, CENTER);
        textSize(12);
        text("Airport Key", xbase + 25, ybase + 25);

        ellipse(xbase + 35,
                ybase + 70,
                7,
                7);

        fill(0, 0, 0);
        textAlign(LEFT, CENTER);

        text("Airport", xbase + 50, ybase + 70);
        text("Route", xbase + 50, ybase + 110);
        text("Selected Airport", xbase + 50, ybase + 150);
        text("Source and Dest", xbase + 50, ybase + 180);
        text("Airports", xbase + 50, ybase + 195);

        line(xbase + 29, ybase + 110, xbase + 40, ybase + 110);
        fill(255, 0, 0);
        ellipse(xbase + 35,
                ybase + 150,
                12,
                12);
        noFill();
        ellipse(xbase + 35,
                ybase + 150,
                5,
                5);
        fill(0);
        line(xbase + 29, ybase + 180, xbase + 37, ybase + 180);
        fill(255, 0, 0);
        ellipse(xbase + 23,
                ybase + 180,
                12,
                12);
        noFill();
        ellipse(xbase + 23,
                ybase + 180,
                5,
                5);
        fill(0);
        ellipse(xbase + 40,
                ybase + 180,
                7,
                7);
    }
}
