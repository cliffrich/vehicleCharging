package com.sse.hateos;

import com.sse.domain.Session;
import com.sse.rest.AdminController;
import com.sse.rest.EntityType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class SessionAssembler implements ResourceAssembler<Session, Resource<Session>> {

    @Override
    public Resource<Session> toResource(Session session) {
        return new Resource<>(session,
                linkTo(methodOn(AdminController.class).
                        one(EntityType.session, String.valueOf(session.getId()), false)).withSelfRel(),
                linkTo(methodOn(AdminController.class).
                        one(EntityType.user, String.valueOf(session.getUser().getId()), false)).withRel("user"),
                linkTo(methodOn(AdminController.class).
                        one(EntityType.asset, String.valueOf(session.getAsset().getId()), false)).withRel("asset"));
//                linkTo(methodOn(AdminController.class).
//                        one(EntityType.location, String.valueOf(session.getAsset().getLocation().getId()), false)).withRel("location"));
    }
}
