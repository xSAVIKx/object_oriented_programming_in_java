package module6;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PGraphics;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Iurii Sergiichuk on 18.09.2016.
 */
public class RouteMarker extends SimpleLinesMarker {

    public RouteMarker(List<Location> locations, HashMap<String, Object> properties) {
        super(locations, properties);
    }

    public void draw(PGraphics pg, List<MapPosition> mapPositions) {
        if (!mapPositions.isEmpty() && !this.isHidden()) {
            pg.pushStyle();
            pg.noFill();
            if (this.isSelected()) {
                pg.stroke(this.highlightColor);
            } else {
                pg.stroke(this.color);
            }

            pg.strokeWeight((float) this.strokeWeight);
            pg.smooth();
            pg.beginShape(5);
            MapPosition currentPosition = (MapPosition) mapPositions.get(0);

            for (int i = 1; i < mapPositions.size(); ++i) {
                MapPosition nextPosition = (MapPosition) mapPositions.get(i);
                pg.vertex(currentPosition.x, currentPosition.y);
                pg.vertex(nextPosition.x, nextPosition.y);
                currentPosition = nextPosition;
            }

            pg.endShape();
            pg.popStyle();
        }
    }

}
