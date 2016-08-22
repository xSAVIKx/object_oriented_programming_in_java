package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/**
 * Implements a visual marker for ocean earthquakes on an earthquake map
 *
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author xSAVIKx
 */
public class OceanQuakeMarker extends EarthquakeMarker {

    public OceanQuakeMarker(PointFeature quake) {
        super(quake);

        // setting field in earthquake marker
        isOnLand = false;
    }


    @Override
    public void drawEarthquake(PGraphics pg, float x, float y) {
        float r = getRadius();
        pg.rect(x - r / 2, y - r / 2, r, r);
    }


}
