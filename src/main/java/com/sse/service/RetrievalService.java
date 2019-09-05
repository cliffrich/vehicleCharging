package com.sse.service;

import com.sse.domain.*;
import com.sse.exceptions.EntityNotFoundException;
import com.sse.hateos.*;
import com.sse.repository.*;
import com.sse.rest.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RetrievalService {
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

    @Autowired
    public RetrievalService(AccountRepository accountRepository, AssetRepository assetRepository,
                            UserRepository userRepository, LocationRepository locationRepository,
                            SessionRepository sessionRepository, LocationAssembler locationAssembler,
                            AssetAssembler assetAssembler, AccountAssembler accountAssembler,
                            UserAssembler userAssembler, SessionAssembler sessionAssembler) {
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionRepository = sessionRepository;
        this.locationAssembler = locationAssembler;
        this.assetAssembler = assetAssembler;
        this.accountAssembler = accountAssembler;
        this.userAssembler = userAssembler;
        this.sessionAssembler= sessionAssembler;
    }

    public ResponseEntity<?> retrieveOneEntity(EntityType type, String id){
        if(type.equals(EntityType.location))
            return retrieveOneLocation(type, id);
        else if(type.equals(EntityType.asset))
            return retrieveOneAsset(type, id);
        else if(type.equals(EntityType.account))
            return retrieveOneAccount(type, id);
        else if(type.equals(EntityType.user))
            return retrieveOneUser(type, id);
        else if(type.equals(EntityType.session))
            return retrieveOneSession(type, id);
        return null;
    }

    public ResponseEntity<?> retrieveAllEntities(EntityType type){
        if(type.equals(EntityType.location))
            return retrieveAllLocations();
        else if(type.equals(EntityType.asset))
            return retrieveAllAssets();
        else if(type.equals(EntityType.account))
            return retrieveAllAccounts();
        else if(type.equals(EntityType.user))
            return retrieveAllUsers();
        return null;
    }

    public ResponseEntity<?> retrieveAssetsForLocation(String id){
        Location location = locationRepository.findById(Long.parseLong(id)).
                orElseThrow(()-> new EntityNotFoundException("location", id));
        List<Asset> assets = assetRepository.findAssetsByLocationEquals(location)
                .orElseThrow(()-> new EntityNotFoundException("assets for location", id));
        return ResponseEntity.of(Optional.of(assets.
                stream().
                map(asset -> assetAssembler.toResource(asset)).
                collect(Collectors.toList())));
    }

    public ResponseEntity<?> retrieveAssetsByPostcode(String postcode){
        List<Asset> assets = assetRepository.findAssetsByLocation_Postcode(postcode)
                .orElseThrow(()-> new EntityNotFoundException("assets by postcode", postcode));
        return ResponseEntity.of(Optional.of(assets.
                stream().
                map(asset -> assetAssembler.toResource(asset)).
                collect(Collectors.toList())));
    }

    public ResponseEntity<?> retrieveUsersForAccount(String id){
        Account account = accountRepository.findById(id).
                orElseThrow(()-> new EntityNotFoundException("account", id));
        List<User> users = userRepository.findUserssByAccountEquals(account)
                .orElseThrow(()-> new EntityNotFoundException("users for account", id));
        return ResponseEntity.of(Optional.of(users.
                stream().
                map(user -> userAssembler.toResource(user)).
                collect(Collectors.toList())));
    }

    private ResponseEntity<?> retrieveOneLocation(EntityType type, String id){
        Location location = locationRepository.findById(Long.parseLong(id)).
                orElseThrow(()-> new EntityNotFoundException(type.name(), id) );
        return getLinks(locationAssembler.toResource(location));
    }

    private ResponseEntity<?> retrieveAllLocations(){
        return ResponseEntity.of(Optional.of(locationRepository.findAll().
                stream().
                map(location -> locationAssembler.toResource(location)).
                collect(Collectors.toList())));
    }

    private ResponseEntity<?> retrieveOneAsset(EntityType type, String id){
        Asset asset = assetRepository.findById(Long.parseLong(id)).
                orElseThrow(()-> new EntityNotFoundException(type.name(), id) );
        return getLinks(assetAssembler.toResource(asset));
    }

    private ResponseEntity<?> retrieveAllAssets(){
        return ResponseEntity.of(Optional.of(assetRepository.findAll().
                stream().
                map(asset -> assetAssembler.toResource(asset)).
                collect(Collectors.toList())));
    }

    private ResponseEntity<?> retrieveOneAccount(EntityType type, String id){
        Account account = accountRepository.findById(id).
                orElseThrow(()-> new EntityNotFoundException(type.name(), id) );
        return getLinks(accountAssembler.toResource(account));
    }

    private ResponseEntity<?> retrieveAllAccounts(){
        return ResponseEntity.of(Optional.of(accountRepository.findAll().
                stream().
                map(account -> accountAssembler.toResource(account)).
                collect(Collectors.toList())));
    }

    private ResponseEntity<?> retrieveOneUser(EntityType type, String id){
        User user = userRepository.findById(id).
                orElseThrow(()-> new EntityNotFoundException(type.name(), id) );
        return getLinks(userAssembler.toResource(user));
    }

    private ResponseEntity<?> retrieveOneSession(EntityType type, String id){
        Session session = sessionRepository.findById(Long.parseLong(id)).
                orElseThrow(()-> new EntityNotFoundException(type.name(), id) );
        return getLinks(sessionAssembler.toResource(session));
    }

    private ResponseEntity<?> retrieveAllUsers(){
        return ResponseEntity.of(Optional.of(userRepository.findAll().
                stream().
                map(user -> userAssembler.toResource(user)).
                collect(Collectors.toList())));
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
}
