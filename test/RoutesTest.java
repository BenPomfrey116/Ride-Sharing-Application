import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutesTest {
    Routes routesData;

    @BeforeEach
    void setup() {
        routesData = new Routes();
    }

    @Test
    void getDistance() {
        assertEquals(67, routesData.getDistance("Canterbury", "Gatwick"));
    }

    @Test
    void getAverageSpeedLimit() {
        assertEquals(60, routesData.getAverageSpeedLimit("Canterbury", "Gatwick"));
    }

    @Test
    void getTraffic() {
        assertEquals(0.7, routesData.getTraffic("Canterbury", "Gatwick"));
    }

    @Test
    void setTraffic() {
        routesData.setTraffic("Canterbury", "Gatwick", 0.3);
        assertEquals(0.3, routesData.getTraffic("Canterbury", "Gatwick"));
    }
}