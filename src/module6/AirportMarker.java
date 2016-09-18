package module6;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to represent AirportMarkers on a world map.
 *
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 *         MOOC team
 */
public class AirportMarker extends CommonMarker {
    private static final String NAME_PROPERTY = "name";
    private static final String CITY_PROPERTY = "city";
    private static final String COUNTRY_PROPERTY = "country";
    private static final String CODE_PROPERTY = "code";
    private static final String ALTITUDE_PROPERTY = "altitude";
    private static final double FEET_TO_METERS_CONVERSION_CONSTANT = 3.2808;
    private static final DecimalFormat ALTITUDE_FORMAT = new DecimalFormat("#.##");
    public Set<SimpleLinesMarker> routes = new HashSet<>();

    public AirportMarker(Feature city) {
        super(((PointFeature) city).getLocation(), city.getProperties());
        setId(city.getId());
    }

    @Override
    public void draw(PGraphics pg, float x, float y) {
        super.draw(pg, x, y);
        if (clicked) {
            pg.fill(255, 0, 0);
            pg.ellipse(x, y, 13, 13);
        }
    }

    @Override
    public void drawMarker(PGraphics pg, float x, float y) {
        pg.fill(11);
        pg.ellipse(x, y, 5, 5);

    }

    @Override
    public void showTitle(PGraphics pg, float x, float y) {
        String title = getTitle();
        pg.pushStyle();

        pg.rectMode(PConstants.CORNER);

        pg.stroke(110);
        pg.fill(255, 255, 255);
        pg.rect(x, y + 15, pg.textWidth(title) + 6, 36, 5);

        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.fill(0);
        pg.text(title, x + 3, y + 18);


        pg.popStyle();
    }

    private String getTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" (").append(getCity()).append(" - ").append(getCountry()).append(')').append('\n');
        sb.append("Altitude: ").append(ALTITUDE_FORMAT.format(getAltitude())).append(" m.");
        String code = getCode();
        if (isNotEmpty(code)) {
            sb.append(" Code: ").append(getCode());
        }

        return sb.toString();
    }

    public String getName() {
        return getStringProperty(NAME_PROPERTY);
    }

    public String getCity() {
        return getStringProperty(CITY_PROPERTY);
    }

    public String getCountry() {
        return getStringProperty(COUNTRY_PROPERTY);
    }

    public String getCode() {
        return getStringProperty(CODE_PROPERTY);
    }

    public double getAltitude() {
        String altitudeStringInFeet = getStringProperty(ALTITUDE_PROPERTY);
        double altitudeInFeet = Double.parseDouble(altitudeStringInFeet);
        return altitudeInFeet / FEET_TO_METERS_CONVERSION_CONSTANT;
    }

    /**
     * Checks whether Airport code is not empty
     *
     * @param code code to check
     * @return true if code is not null and not equal to ""
     */
    private boolean isNotEmpty(String code) {
        return code != null && !"\"\"".equals(code);
    }

}
