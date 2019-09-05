package com.sse.vehicleCharging;

import com.sse.domain.Asset;
import com.sse.domain.ChargeType;
import com.sse.domain.Location;
import com.sse.repository.AssetRepository;
import com.sse.repository.LocationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VehicleChargingApplicationTests {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private AssetRepository assetRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void createLocationWithAsset(){
		createLocationAndAsset(2, 10);
		List<Location> locations = locationRepository.findAll();
		assertThat(locations.size() == 2);
		List<Asset> assets = assetRepository.findAll();
		assertTrue(assets.size() == 20);
	}

	// Location related tests

	@Test
	public void retrieveOneLocationUsingIdWillRetrieveLocationWithAssets() {
		createLocationAndAsset(1, 10);
		// One location retrieval
		Location locationRetrieved = locationRepository.findById(1L).get();
		List<Asset> assets = assetRepository.findAssetsByLocationEquals(locationRetrieved).get();
		assertTrue(assets.size() == 10);
	}

	@Test
	public void retrieveOneLocationUsingPostcodeWillRetrieveLocationWithAssets(){
		createLocationAndAsset(1, 10);

	}
	// Asset related tests
	@Test
	public void findOneByIdWillNotRetrieveLocation(){
		createLocationAndAsset(1, 10);
		Asset singleAsset = assetRepository.findById(5L).get();
		assertEquals(new Long(1), singleAsset.getLocation().getId());
	}

	@Test
	public void findAssetByIdWithLocationWillRetrieveLocation(){
		createLocationAndAsset(1, 3);
		Asset singleAsset = assetRepository.findById(5L).get();
		assertEquals(new Long(1), singleAsset.getLocation().getId());
	}

	private void createLocationAndAsset(int noOfLocations, int noOfAssetsForEachLocation){
		List<Location> locations = IntStream.rangeClosed(1, noOfLocations)
				.mapToObj(i -> new Location("name-"+i, "POST-"+i))
				.collect(Collectors.toList());
		locations.forEach(location -> locationRepository.save(location));
		locations.forEach(location -> location.setAssets(
				IntStream.rangeClosed(1, noOfAssetsForEachLocation)
						.mapToObj(i -> assetRepository.save(new Asset("Name - "+i, ChargeType.FAST, location)))
						.collect(Collectors.toList())));
	}

}

