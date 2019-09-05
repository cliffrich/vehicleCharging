package com.sse.hateos;

import com.sse.domain.User;
import com.sse.rest.AdminController;
import com.sse.rest.EntityType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserAssembler implements ResourceAssembler<User, Resource<User>> {

    @Override
    public Resource<User> toResource(User user) {
        return new Resource<>(user,
                linkTo(methodOn(AdminController.class).
                        one(EntityType.user, user.getId(), false)).withSelfRel(),
                linkTo(methodOn(AdminController.class).
                        one(EntityType.account, user.getAccount().getId(), false)).withRel("account"),
                linkTo(methodOn(AdminController.class).
                        all(EntityType.user)).withRel("users"));
    }
}
