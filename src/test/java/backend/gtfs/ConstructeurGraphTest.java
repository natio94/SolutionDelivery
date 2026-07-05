package backend.gtfs;

import backend.Service;
import backend.models.*;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstructeurGraphTest {
	@Test
	void checkLatLon() {
		System.out.println("check Latitude Longitude test");
		Service service = Service.getInstance();
		Quai republique = service.getGraphe().getStationParNom("République").getQuais().get(0);
		assertEquals(republique.getLongitude(), 2.3631211111325285);
		assertEquals(republique.getLatitude(), 48.86739447922248);
	}
}