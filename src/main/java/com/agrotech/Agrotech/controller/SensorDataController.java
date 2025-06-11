package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.service.SensorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(
    name = "Sensor Data",
    description = "Operaciones para recibir y listar datos de sensores "
)
@RestController
@RequestMapping("/api/sensores")
public class SensorDataController {

    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    @Operation(
        summary = "Recibir datos de sensor",
        description = "Guarda un nuevo registro de SensorData; si no viene timestamp, se usa el actual."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Datos guardados correctamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<EntityModel<SensorData>> receiveData(@RequestBody SensorData data) {
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        SensorData saved = service.save(data);

        EntityModel<SensorData> resource = EntityModel.of(saved,
            linkTo(methodOn(SensorDataController.class).getById(saved.getId())).withSelfRel(),
            linkTo(methodOn(SensorDataController.class).getAll()).withRel("todosSensores")
        );

        return ResponseEntity.ok(resource);
    }

    @Operation(
        summary = "Listar todos los datos de los sensores",
        description = "Devuelve el histórico completo de registros de SensorData"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista devuelta con éxito"
        )
    })
    @GetMapping
    public CollectionModel<EntityModel<SensorData>> getAll() {
        List<EntityModel<SensorData>> sensores = service.findAll().stream()
            .map(dato -> EntityModel.of(dato,
                linkTo(methodOn(SensorDataController.class).getById(dato.getId())).withSelfRel(),
                linkTo(methodOn(SensorDataController.class).getAll()).withRel("todosSensores")
            ))
            .collect(Collectors.toList());

        return CollectionModel.of(sensores,
            linkTo(methodOn(SensorDataController.class).getAll()).withSelfRel()
        );
    }

    @Operation(
        summary = "Obtener dato de sensor por ID",
        description = "Devuelve un único registro de SensorData por su identificador"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Registro encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró el registro",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public EntityModel<SensorData> getById(@PathVariable Long id) {
        SensorData dato = service.findById(id);

        return EntityModel.of(dato,
            linkTo(methodOn(SensorDataController.class).getById(id)).withSelfRel(),
            linkTo(methodOn(SensorDataController.class).getAll()).withRel("todosSensores.")
        );
    }
}