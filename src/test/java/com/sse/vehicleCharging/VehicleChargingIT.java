package com.sse.vehicleCharging;

import com.sse.repository.AssetRepository;
import com.sse.repository.LocationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VehicleChargingIT {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private MockMvc mockMvc;

	@Value("classpath:location.json")
	private Resource location;

	@Value("classpath:asset.json")
	private Resource asset;

	@Value("classpath:locationUpdate.json")
	private Resource locationUpdate;

	@Value("classpath:assetUpdate.json")
	private Resource assetUpdate;

	@Value("classpath:account.json")
	private Resource account;

	@Value("classpath:user.json")
	private Resource user;

	@Value("classpath:accountUpdate.json")
	private Resource accountUpdate;

	@Value("classpath:userUpdate.json")
	private Resource userUpdate;

    @Value("classpath:sessionStart.json")
    private Resource sessionStart;

    @Value("classpath:sessionEnd.json")
    private Resource sessionEnd;

	@Test
	public void contextLoads() {
	}

	@Test
    public void session() throws Exception {
        saveEntityAndAssert(location, "/v1/api/location");
        saveEntityAndAssert(asset, "/v1/api/asset");
        saveEntityAndAssert(account, "/v1/api/account");
        saveEntityAndAssert(user, "/v1/api/user");
        // check if asset status is 'AVAILABLE'
        assertTrue("Asset status is not set to 'AVAILABLE'", retrieveEntity("/v1/api/asset/3", status().isOk())
                .getResponse().getContentAsString().contains("\"status\":\"AVAILABLE\""));
        // Send session start
        saveEntityAndAssert(sessionStart, "/v1/api/session/start");
        assertTrue("Session start time doesn't match", retrieveEntity("/v1/api/session/5", status().isOk())
                .getResponse().getContentAsString().contains("\"startTime\":\"2018-12-17 15:23:01\""));
        // check if asset status has changed to 'CHARGING'
        assertTrue("Asset status not set to 'CHARGING'", retrieveEntity("/v1/api/asset/3", status().isOk())
                .getResponse().getContentAsString().contains("\"status\":\"CHARGING\""));
        // Send session end
        updateEntity(sessionEnd, "/v1/api/session/end/5");
        // check if asset status is reset to 'AVAILABLE'
        assertTrue("Asset status is not reset to 'AVAILABLE'", retrieveEntity("/v1/api/asset/3", status().isOk())
                .getResponse().getContentAsString().contains("\"status\":\"AVAILABLE\""));
        String result = retrieveEntity("/v1/api/session/5", status().isOk()).getResponse().getContentAsString();
        assertTrue("Session end time doesn't match", result.contains("\"endTime\":\"2018-12-17 17:23:01\""));
        assertTrue("Session duration doesn't match", result.contains("\"durationHours\":2"));
        assertTrue("Session cost doesn't match", result.contains("\"cost\":4.00"));
    }

	@Test
	public void locationsAndAssets() throws Exception {
		// persist
		saveEntityAndAssert(location, "/v1/api/location");
		saveEntityAndAssert(asset, "/v1/api/asset");
		// retrieve
		assertTrue("Location names doesn't match", retrieveEntity("/v1/api/location/1", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"ABC Services\""));

		assertTrue("Asset names doesn't match", retrieveEntity("/v1/api/asset/3", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"Insta volt\""));
		// retrieve asset by location
		assertTrue("Asset names by location doesn't match", retrieveEntity("/v1/api/locationAssets?locationid=1", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"Insta volt\""));
		// retrieve asset by postcode
		assertTrue("Asset names by postcode doesn't match", retrieveEntity("/v1/api/postcodeAssets?postcode=BB11BB", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"Insta volt\""));
		// update
		updateEntity(locationUpdate, "/v1/api/location/1");
		MvcResult updatedLocationResult = retrieveEntity("/v1/api/location/1", status().isOk());
		assertTrue("Updated location names doesn't match", updatedLocationResult
				.getResponse().getContentAsString().contains("\"name\":\"ABC Services Updated\""));

		updateEntity(assetUpdate, "/v1/api/asset/3");
		assertTrue("Updated asset names doesn't match", retrieveEntity("/v1/api/asset/3", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"Insta volt Updated\""));
		// delete
		deleteEntity("/v1/api/asset/3");
		assertEquals("Assert not deleted", "Could not find 'asset' with id '3'",
				retrieveEntity("/v1/api/asset/3", status().isNotFound()).getResponse().getContentAsString());
		deleteEntity("/v1/api/location/1");
		assertEquals("Location not deleted", "Could not find 'location' with id '1'",
				retrieveEntity("/v1/api/location/1", status().isNotFound()).getResponse().getContentAsString());
		assertTrue("Didn't expect location 2 to be deleted", retrieveEntity("/v1/api/location/2", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"DEF Car park\""));
	}

	@Test
	public void accountAndUsers() throws Exception {
		// persist
		saveEntityAndAssert(account, "/v1/api/account");
		saveEntityAndAssert(user, "/v1/api/user");
		// retrieve
		assertTrue("Account names doesn't match", retrieveEntity("/v1/api/account/test_fleets", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"test Fleets Pvt. Ltd.,\""));

		assertTrue("User names doesn't match", retrieveEntity("/v1/api/user/tf-1", status().isOk())
				.getResponse().getContentAsString().contains("\"firstName\":\"test firstName 1\""));

		assertTrue("User names by account doesn't match", retrieveEntity("/v1/api/accountUsers?accountid=test_fleets", status().isOk())
				.getResponse().getContentAsString().contains("\"firstName\":\"test firstName 1\""));

		// update
		updateEntity(accountUpdate, "/v1/api/account/test_fleets");
		assertTrue("Updated account names doesn't match", retrieveEntity("/v1/api/account/test_fleets", status().isOk())
				.getResponse().getContentAsString().contains("\"name\":\"test Fleets Pvt. Ltd., updated\""));

		updateEntity(userUpdate, "/v1/api/user/tf-1");
		assertTrue("Updated user names doesn't match", retrieveEntity("/v1/api/user/tf-1", status().isOk())
				.getResponse().getContentAsString().contains("\"firstName\":\"test firstName 1 updated\""));
		// delete
		deleteEntity("/v1/api/user/tf-1");
		assertEquals("User not deleted", "Could not find 'user' with id 'tf-1'",
				retrieveEntity("/v1/api/user/tf-1", status().isNotFound()).getResponse().getContentAsString());
		deleteEntity("/v1/api/user/tf-2");
		assertEquals("User not deleted", "Could not find 'user' with id 'tf-2'",
				retrieveEntity("/v1/api/user/tf-2", status().isNotFound()).getResponse().getContentAsString());
		deleteEntity("/v1/api/account/test_fleets");
		assertEquals("Account not deleted", "Could not find 'account' with id 'test_fleets'",
				retrieveEntity("/v1/api/account/test_fleets", status().isNotFound()).getResponse().getContentAsString());
	}

	private void saveEntityAndAssert(Resource resource, String url) throws Exception {
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loadContent(resource)))
				.andExpect(status().isCreated());
	}

	private MvcResult retrieveEntity(String url, ResultMatcher status) throws Exception {
		return mockMvc.perform(get(url))
				.andExpect(status)
				.andReturn();
	}
	private void updateEntity(Resource resource, String url) throws Exception {
		mockMvc.perform(put(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(loadContent(resource)))
				.andExpect(status().isOk());
	}

	private void deleteEntity(String url) throws Exception {
		mockMvc.perform(delete(url))
				.andExpect(status().isOk());
	}

	private String loadContent(Resource resource){
		try {
			return Files.readAllLines(Paths.get(resource.getURI()), StandardCharsets.UTF_8)
					.stream()
					.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

