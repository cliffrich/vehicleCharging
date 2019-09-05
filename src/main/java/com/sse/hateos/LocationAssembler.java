package com.sse.hateos;

import com.sse.domain.Location;
import com.sse.rest.AdminController;
import com.sse.rest.EntityType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class LocationAssembler implements ResourceAssembler<Location, Resource<Location>> {

    @Override
    public Resource<Location> toResource(Location location) {
//        location.getAssets().forEach(asset -> linkTo(methodOn(AdminController.class).one(EntityType.asset, String.valueOf(asset.getId()), false)).withRel("assets"));
//        List<ControllerLinkBuilder> assetBuilders = location.getAssets().stream().map(asset -> linkTo(methodOn(AdminController.class).one(EntityType.asset, String.valueOf(asset.getId()))).withRel("assets")).collect(Collectors.toList());
        return new Resource<>(location,
                linkTo(methodOn(AdminController.class).one(EntityType.location, String.valueOf(location.getId()), false)).withSelfRel(),
//                linkTo(methodOn(AdminController.class).one(EntityType.asset, String.valueOf(location.getAssets().get(0).getId()))).withRel("assets"),
                linkTo(methodOn(AdminController.class).all(EntityType.location)).withRel("locations"));
    }
}
