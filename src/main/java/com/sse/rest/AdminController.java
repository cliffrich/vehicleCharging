package com.sse.rest;

import com.sse.service.IngesterService;
import com.sse.service.MockUtil;
import com.sse.service.RetrievalService;

import com.sse.service.UpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@Api(value="admin", description="Operations pertaining to SSE EV charging")
public class AdminController {

    @Autowired
    IngesterService ingesterService;
    @Autowired
    RetrievalService retrievalService;
    @Autowired
    UpdateService updateService;

    @ApiOperation(value = "Create a new entity of given type", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "An internal server error")
    })
    @PostMapping(value = "/v1/api/{type}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody CompletableFuture<ResponseEntity<?>> insert(@PathVariable("type") EntityType type, @RequestBody String content,
                                    @PathParam("mock") boolean mock) {
        if(mock)
            return CompletableFuture.completedFuture(ResponseEntity.of(Optional.of(MockUtil.getMock(type))));
        return CompletableFuture.completedFuture(ingesterService.persistContent(type, content));
    }

    @PutMapping("/v1/api/{type}/{id}")
    @ResponseBody CompletableFuture<ResponseEntity<?>> update(@PathVariable("type") EntityType type,
                                                              @PathVariable("id") String id,
                                                              @RequestBody String content,
                                                              @PathParam("mock") boolean mock) {
        if(mock)
            return CompletableFuture.completedFuture(ResponseEntity.of(Optional.of(MockUtil.getMock(type))));
        return CompletableFuture.completedFuture(updateService.updateEntity(type, id, content));
    }

    @GetMapping(value = "/v1/api/{type}/{id}")
    public ResponseEntity<?> one(@PathVariable("type") EntityType type, @PathVariable String id, @PathParam("mock") boolean mock) {
        if(mock)
            return ResponseEntity.of(Optional.of(MockUtil.getMock(type)));
        return retrievalService.retrieveOneEntity(type, id);
    }

    @GetMapping(value = "/v1/api/locationAssets")
    public ResponseEntity<?> allAssetsForLocation(@PathParam("locationid") String locationid) {
        return retrievalService.retrieveAssetsForLocation(locationid);
    }

    @GetMapping(value = "/v1/api/postcodeAssets")
    public ResponseEntity<?> allAssetsInPostcode(@PathParam("postcode") String postcode) {
        return retrievalService.retrieveAssetsByPostcode(postcode);
    }

    @GetMapping(value = "/v1/api/accountUsers")
    public ResponseEntity<?> allUserssOfAccount(@PathParam("accountid") String accountid) {
        return retrievalService.retrieveUsersForAccount(accountid);
    }

    @GetMapping("/v1/api/{type}s")
    public ResponseEntity<?> all(@PathVariable("type") EntityType type) {
        return retrievalService.retrieveAllEntities(type);
    }

    @DeleteMapping("/v1/api/{type}/{id}")
    CompletableFuture<ResponseEntity<?>> delete(@PathVariable("type") EntityType type,
            @PathVariable("id") String id,
            @PathParam("mock") boolean mock) {
        if(mock)
            return CompletableFuture.completedFuture(ResponseEntity.of(Optional.of("{response:success}")));
        return CompletableFuture.completedFuture(ingesterService.deleteContent(type, id));
    }
}
