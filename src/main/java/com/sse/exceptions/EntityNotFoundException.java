package com.sse.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String entity, String id){
        super(String.format("Could not find '%s' with id '%s'", entity, id));
    }
}
