package com.sse.hateos;

import com.sse.domain.Account;
import com.sse.rest.AdminController;
import com.sse.rest.EntityType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class AccountAssembler implements ResourceAssembler<Account, Resource<Account>> {

    @Override
    public Resource<Account> toResource(Account account) {
        return new Resource<>(account,
                linkTo(methodOn(AdminController.class).one(EntityType.account, account.getId(), false)).withSelfRel(),
                linkTo(methodOn(AdminController.class).all(EntityType.account)).withRel("accounts"));
    }
}
