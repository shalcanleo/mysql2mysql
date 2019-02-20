package org.ning.controllers;

import io.swagger.annotations.Api;
import org.ning.modules.SyncerRequest;
import org.ning.services.SyncerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/syncer")
@Api(value = "syncer", protocols = "JSON")
public class SyncerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SyncerService service;

    @PostMapping()
    public ResponseEntity<String> createSyncTask(@RequestBody SyncerRequest request) {
        return ResponseEntity.ok(service.createSyncJob(request).toString());
    }
}
