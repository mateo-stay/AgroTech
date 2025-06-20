package com.agrotech.Agrotech.controller;

import com.agrotech.Agrotech.assembler.SensorDataModelAssembler;
import com.agrotech.Agrotech.model.SensorData;
import com.agrotech.Agrotech.service.SensorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    description = "Operaciones para recibir y listar datos de sensores"
)
@RestController
@RequestMapping("/api/sensores")
public class SensorDataController {

    private final SensorDataService service;
    private final SensorDataModelAssembler assembler;

    public SensorDataController(SensorDataService service,
                                SensorDataModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @Operation(summary = "Recibir datos de sensor",
               description = "Guarda un nuevo registro; si no viene timestamp, se usa el actual.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos guardados correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<EntityModel<SensorData>> receiveData(
            @Valid @RequestBody SensorData data
    ) {
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        SensorData saved = service.save(data);
        return ResponseEntity.ok(assembler.toModel(saved));
    }

    @Operation(summary = "Listar todos los datos",
               description = "Devuelve el histórico completo de registros")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito")
    })
    @GetMapping
    public CollectionModel<EntityModel<SensorData>> getAll() {
        List<EntityModel<SensorData>> sensores = service.findAll().stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());
        return CollectionModel.of(sensores,
            linkTo(methodOn(SensorDataController.class).getAll()).withSelfRel()
        );
    }

    @Operation(summary = "Obtener dato por ID",
               description = "Devuelve un único registro por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro encontrado"),
        @ApiResponse(responseCode = "404", description = "No se encontró el registro")
    })
    @GetMapping("/{id}")
    public EntityModel<SensorData> getById(@PathVariable Long id) {
        SensorData dato = service.findById(id);
        return assembler.toModel(dato);
    }

    @Operation(summary = "Actualizar dato de sensor",
               description = "Actualiza los campos permitidos de un registro existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el registro"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SensorData>> update(
            @PathVariable Long id,
            @Valid @RequestBody SensorData data
    ) {
        SensorData updated = service.update(id, data);
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @Operation(summary = "Borrar dato de sensor",
               description = "Elimina un registro por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el registro")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
