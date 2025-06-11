package com.agrotech.Agrotech.assembler;

import com.agrotech.Agrotech.controller.SensorDataController;
import com.agrotech.Agrotech.model.SensorData;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;       
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SensorDataModelAssembler
    implements RepresentationModelAssembler<SensorData, EntityModel<SensorData>> {

    @Override
    public @NonNull EntityModel<SensorData> toModel(@NonNull SensorData data) {
        return EntityModel.of(data,
            linkTo(methodOn(SensorDataController.class)
                .getById(data.getId())).withSelfRel(),
            linkTo(methodOn(SensorDataController.class)
                .getAll()).withRel("todosSensores")
        );
    }
}