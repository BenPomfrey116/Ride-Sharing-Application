import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {
    Location location1;
    Location location2;
    Location location3;

    @BeforeEach
    void setUp() {
        location1 = new Location("Location 1");
        location2 = new Location("Location 2");
        location3 = new Location("Location 3");
    }

    @Test
    void addRoute() {
        RouteLeg leg = new RouteLeg(20, 0.8, 60);

        HashMap<String, RouteLeg> expectedRoutes = new HashMap<>();
        expectedRoutes.put("Location 2", leg);

        location1.addRoute("Location 2", leg);

        assertEquals(expectedRoutes, location1.getOutgoingRoutes());
    }

    @Test
    void getRouteTo() {
        RouteLeg leg = new RouteLeg(20, 0.8, 60);
        location1.addRoute("Location 2", leg);
        assertEquals(leg, location1.getRouteTo("Location 2"));
    }


}