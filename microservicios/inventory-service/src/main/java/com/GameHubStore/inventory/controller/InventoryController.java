package com.GameHubStore.inventory.controller;


import com.GameHubStore.inventory.model.dto.InventoryRequest;
import com.GameHubStore.inventory.model.entities.Inventory;
import com.GameHubStore.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@AllArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<Inventory> addInventory(@Valid @RequestBody InventoryRequest request) {
        Inventory newInventory = inventoryService.addInventory(request);
        return new ResponseEntity<>(newInventory, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories(){
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }
}
