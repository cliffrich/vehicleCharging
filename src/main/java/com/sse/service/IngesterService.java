package com.sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.sse.domain.AssetStatus;
import com.sse.domain.Session;
import com.sse.exceptions.ParseException;
import com.sse.exceptions.PersistenceException;
import com.sse.hateos.*;
import com.sse.repository.*;
import com.sse.rest.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngesterService {
    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SessionRepository sessionRepository;
    private final ObjectMapper mapper;
    private final Map<EntityType, JpaRepository> repositoryMap = new HashMap<>();
    private final Map<EntityType, ResourceAssembler> assemblerMap = new HashMap<>();
    private final LocationAssembler locationAssembler;
    private final AssetAssembler assetAssembler;
    private final AccountAssembler accountAssembler;
    private final UserAssembler userAssembler;
    private final SessionAssembler sessionAssembler;

    @Autowired
    public IngesterService(AccountRepository accountRepository, AssetRepository assetRepository,
                           UserRepository userRepository, LocationRepository locationRepository,
                           SessionRepository sessionRepository, ObjectMapper mapper,
                           LocationAssembler locationAssembler, AssetAssembler assetAssembler,
                           AccountAssembler accountAssembler, UserAssembler userAssembler,
                           SessionAssembler sessionAssembler) {
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.sessionRepository = sessionRepository;
        this.mapper = mapper;
        this.locationAssembler = locationAssembler;
        this.assetAssembler = assetAssembler;
        this.accountAssembler = accountAssembler;
        this.userAssembler = userAssembler;
        this.sessionAssembler = sessionAssembler;
        buildRepositoryAndAssemblerMap();
    }

    private void buildRepositoryAndAssemblerMap(){
        repositoryMap.put(EntityType.location, locationRepository);
        repositoryMap.put(EntityType.asset, assetRepository);
        repositoryMap.put(EntityType.account, accountRepository);
        repositoryMap.put(EntityType.user, userRepository);
        repositoryMap.put(EntityType.session, sessionRepository);

        assemblerMap.put(EntityType.location, locationAssembler);
        assemblerMap.put(EntityType.asset, assetAssembler);
        assemblerMap.put(EntityType.account, accountAssembler);
        assemblerMap.put(EntityType.user, userAssembler);
        assemblerMap.put(EntityType.session, sessionAssembler);
    }
    public ResponseEntity<?> persistContent(EntityType type, String content){
        List<Object> createdObject = convertAndPersist(repositoryMap.get(type), fromJson(type, content));
        if(createdObject != null)
            try {
                return ResponseEntity
                        .created(new URI(createdObject
                            .stream()
                            .map(obj -> assemblerMap.get(type)
                                    .toResource(obj)
                                    .getId()
                                    .expand()
                                    .getHref())
                            .collect(Collectors.joining(","))))
                        .body(createdObject);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        return ResponseEntity.of(Optional.empty());
    }

    public ResponseEntity<?> persistSessionStart (EntityType type, String content){
        Session createdSession = sessionRepository.save((Session) fromJsonForSingleEntity(type, content));
        if(createdSession != null){
            createdSession.getAsset().setStatus(AssetStatus.CHARGING);
            assetRepository.updateStatus(createdSession.getAsset().getId(), AssetStatus.CHARGING, Instant.now());
            try {
                return ResponseEntity
                        .created(new URI(assemblerMap.get(type)
                                .toResource(createdSession)
                                .getId()
                                .expand()
                                .getHref()))
                        .body(createdSession);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.of(Optional.empty());
    }

    public ResponseEntity<?> deleteContent(EntityType type, String id){
        if(type.equals(EntityType.location) || type.equals(EntityType.asset))
            deleteLocationOrAsset(repositoryMap.get(type), Long.parseLong(id));
        else
            delete(repositoryMap.get(type), id);
        return ResponseEntity.of(Optional.of(String.format("Successfully deleted '%s' with id '%s'", type.name(), id)));
    }

    private List<Object> convertAndPersist(JpaRepository repository, List<Object> objectsToPersist){
        List<Object> result = new ArrayList<>();
        objectsToPersist.forEach(o -> result.add(persist(repository, o)));
        return result;
    }

    private <S> S persist(JpaRepository<S,Long> repository, S s){
        try {
            return repository.save(s);
        }catch (Exception se){
            if(se instanceof DataAccessException){
                log.error("Exception while persisting the content. Error message [{}]", se.getMessage());
                throw new PersistenceException(String.format("Unable to persist content. Error message [{%s}]", se.getMessage()), se.getCause());
            }
        }
        return null;
    }
    private <S> void delete(JpaRepository<S,Long> repository, Long id){
        try {
            repository.deleteById(id);
        }catch (Exception se){
            if(se instanceof DataAccessException){
                log.error("Exception while deleting entity. Error message [{}]", se.getMessage());
                throw new PersistenceException(String.format("Unable to delete entity. Error message [{%s}]", se.getMessage()), se.getCause());
            }
        }
    }

    private <S> void deleteLocationOrAsset(JpaRepository<S,Long> repository, Long id){
        try {
            repository.deleteById(id);
        }catch (Exception se){
            if(se instanceof DataAccessException){
                log.error("Exception while deleting entity. Error message [{}]", se.getMessage());
                throw new PersistenceException(String.format("Unable to delete entity. Error message [{%s}]", se.getMessage()), se.getCause());
            }
        }
    }

    private <S> void delete(JpaRepository<S,String> repository, String id){
        try {
            repository.deleteById(id);
        }catch (Exception se){
            if(se instanceof DataAccessException){
                log.error("Exception while deleting entity. Error message [{}]", se.getMessage());
                throw new PersistenceException(String.format("Unable to delete entity. Error message [{%s}]", se.getMessage()), se.getCause());
            }
        }
    }
    private List<Object> fromJson(EntityType type, String input) throws ParseException{
        try {
            CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, type.getClazz());
            return mapper.readValue(input, javaType);
        } catch (IOException e) {
            log.error("Exception while parsing the content for '{}'. Error message [{}]", type, e.getMessage());
            throw new ParseException(String.format("Unable to parse content for %s", type), e.getCause());
        }
    }

    private Object fromJsonForSingleEntity(EntityType type, String input) throws ParseException{
        try {
            return mapper.readValue(input, type.getClazz());
        } catch (IOException e) {
            log.error("Exception while parsing the content for '{}'. Error message [{}]", type, e.getMessage());
            throw new ParseException(String.format("Unable to parse content for %s", type), e.getCause());
        }
    }
}
