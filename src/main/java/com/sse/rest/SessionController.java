package com.sse.rest;

import com.sse.service.IngesterService;
import com.sse.service.RetrievalService;
import com.sse.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class SessionController {

    @Autowired
    IngesterService ingesterService;
    @Autowired
    RetrievalService retrievalService;
    @Autowired
    UpdateService updateService;

    @PostMapping(value = "/v1/api/session/start", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody CompletableFuture<ResponseEntity<?>> createSession(@RequestBody String content,
                                                         @PathParam("mock") boolean mock){
        if(mock)
            return CompletableFuture.completedFuture(ResponseEntity.of(Optional.of("{response:success}")));
        return CompletableFuture.completedFuture(ingesterService.persistSessionStart(EntityType.session, content));
    }

    @PutMapping("/v1/api/session/end/{id}")
    @ResponseBody CompletableFuture<ResponseEntity<?>> update(@PathVariable("id") Long id,
                                                              @RequestBody String content,
                                                              @PathParam("mock") boolean mock) {
        if(mock)
            return CompletableFuture.completedFuture(ResponseEntity.of(Optional.of("{response:success}")));
        return CompletableFuture.completedFuture(updateService.updateSession(id, content));
    }
}
