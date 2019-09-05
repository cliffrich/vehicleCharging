package com.sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sse.domain.*;
import com.sse.exceptions.ParseException;
import com.sse.hateos.*;
import com.sse.repository.*;
import com.sse.rest.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class UpdateService {
    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SessionRepository sessionRepository;
    private final LocationAssembler locationAssembler;
    private final AssetAssembler assetAssembler;
    private final AccountAssembler accountAssembler;
    private final UserAssembler userAssembler;
    private final SessionAssembler sessionAssembler;
    private final ObjectMapper mapper;

    @Autowired
    public UpdateService(AccountRepository accountRepository, AssetRepository assetRepository,
                         UserRepository userRepository, LocationRepository locationRepository,
                         SessionRepository sessionRepository, LocationAssembler locationAssembler,
                         AssetAssembler assetAssembler, AccountAssembler accountAssembler,
                         UserAssembler userAssembler, SessionAssembler sessionAssembler, ObjectMapper mapper) {
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionRepository = sessionRepository;
        this.locationAssembler = locationAssembler;
        this.assetAssembler = assetAssembler;
        this.accountAssembler = accountAssembler;
        this.userAssembler = userAssembler;
        this.sessionAssembler = sessionAssembler;
        this.mapper = mapper;
    }

    public ResponseEntity<?> updateEntity(EntityType type, String id, String content){
        Optional<ResponseEntity> responseEntity = Optional.empty();
        if(type.equals(EntityType.location)){
            Location newLocation = (Location) fromJson(type, content);
            responseEntity = locationRepository.findById(Long.parseLong(id))
                    .map(location -> {
                        location.setName(newLocation.getName());
                        location.setDateUpdated(Instant.now());
                        location.setPostcode(newLocation.getPostcode());
                        return getLinks(locationAssembler.toResource(locationRepository.save(location)));
                    });
        } else if(type.equals(EntityType.asset)){
            Asset newAsset = (Asset) fromJson(type, content);
            responseEntity = assetRepository.findById(Long.parseLong(id))
                    .map(asset -> {
                        asset.setLocation(newAsset.getLocation());
                        asset.setChargeType(newAsset.getChargeType());
                        asset.setName(newAsset.getName());
                        asset.setDateUpdated(Instant.now());
                        asset.setStatus(newAsset.getStatus()!=null?newAsset.getStatus(): AssetStatus.AVAILABLE);
                        return getLinks(assetAssembler.toResource(assetRepository.save(asset)));
                    });
        } else if(type.equals(EntityType.account)){
            Account newAccount = (Account) fromJson(type, content);
            responseEntity = accountRepository.findById(id)
                    .map(account -> {
                        account.setName(newAccount.getName());
                        account.setDateUpdated(Instant.now());
                        account.setEmail(newAccount.getEmail());
                        return getLinks(accountAssembler.toResource(accountRepository.save(account)));
                    });
        } else if(type.equals(EntityType.user)){
            User newUser = (User) fromJson(type, content);
            responseEntity = userRepository.findById(id)
                    .map(user -> {
                        user.setFirstName(newUser.getFirstName());
                        user.setLastName(newUser.getLastName());
                        user.setPassword(newUser.getPassword());
                        user.setDateUpdated(Instant.now());
                        user.setEmail(newUser.getEmail());
                        user.setPaymentLimit(newUser.getPaymentLimit());
                        user.setPaymentMethod(newUser.getPaymentMethod());
                        user.setVehicleDepot(newUser.getVehicleDepot());
                        user.setVehicleMake(newUser.getVehicleMake());
                        user.setVehicleModel(newUser.getVehicleModel());
                        user.setVehicleType(newUser.getVehicleType());
                        return getLinks(userAssembler.toResource(userRepository.save(user)));
                    });
        }
        return responseEntity.orElse(null);
    }

    public ResponseEntity<?> updateSession(Long id, String content) {
        Optional<ResponseEntity> responseEntity = Optional.empty();
        Session endSession = (Session) fromJson(EntityType.session, content);
        responseEntity = sessionRepository.findById(id)
                .map(session -> {
                    session.setEndTime(endSession.getEndTime());
                    session.setDateUpdated(Instant.now());
                    Long chargedHours = Duration.between(session.getStartTime(), endSession.getEndTime()).toHours();
                    session.setDurationHours(chargedHours);
                    session.setCost(session.getAsset().getPricePerHour().multiply(new BigDecimal(chargedHours)));
                    assetRepository.updateStatus(session.getAsset().getId(), AssetStatus.AVAILABLE, Instant.now());
                    return getLinks(sessionAssembler.toResource(sessionRepository.save(session)));
                });
        return responseEntity.orElse(null);
    }

    private ResponseEntity<?> getLinks(Resource<?> resource){
        try{
            return ResponseEntity
                    .ok(resource);
        } catch (Exception ex){
            log.error(ex.getMessage());
        }
        return null;
    }

    private Object fromJson(EntityType type, String input) throws ParseException{
        try {
            return mapper.readValue(input, type.getClazz());
        } catch (IOException e) {
            log.error("Exception while parsing the content for '{}'. Error message [{}]", type, e.getMessage());
            throw new ParseException(String.format("Unable to parse content for %s", type), e.getCause());
        }
    }
}
