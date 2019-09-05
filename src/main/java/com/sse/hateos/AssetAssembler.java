package com.sse.hateos;

import com.sse.domain.Asset;
import com.sse.rest.AdminController;
import com.sse.rest.EntityType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class AssetAssembler implements ResourceAssembler<Asset, Resource<Asset>> {

    @Override
    public Resource<Asset> toResource(Asset asset) {
        return new Resource<>(asset,
                linkTo(methodOn(AdminController.class).
                        one(EntityType.asset, String.valueOf(asset.getId()), false)).withSelfRel(),
//                linkTo(methodOn(AdminController.class).
//                        one(EntityType.location, String.valueOf(asset.getLocation().getId()), false)).withRel("location"),
                linkTo(methodOn(AdminController.class).
                        all(EntityType.asset)).withRel("assets"));
    }
}
