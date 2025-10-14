package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Services.LogService;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LogDTO>> retrieveLogs() {
        log.info("[logController] pinged retrieveLogs");
        List<LogDTO> logs = logService.retrieveAllLogsFromDB();
        return ResponseEntity.status(HttpStatus.OK).body(logs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userid}")
    public ResponseEntity<List<LogDTO>> retrieveLogsByUserId(@PathVariable("userid") UUID userid) {
        log.info("[logController] pinged retrieveLogsByUserId");
        List<LogDTO> logsByUserId = logService.retrieveAllLogsByUserId(userid);
        return ResponseEntity.status(HttpStatus.OK).body(logsByUserId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/logId/{logId}")
    public ResponseEntity<LogDTO> retrieveLogByLogId(@PathVariable("logId") UUID logId) {
        log.info("[logController] pinged retrieveLogByLogId");
        LogDTO logDTO = logService.retrieveLogByLogId(logId);
        return ResponseEntity.status(HttpStatus.OK).body(logDTO);
    }
}
