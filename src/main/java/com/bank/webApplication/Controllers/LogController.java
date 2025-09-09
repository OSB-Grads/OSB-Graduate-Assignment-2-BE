package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

  private LogService logService;

  @Autowired
  public LogController(LogService logService){
      this.logService=logService;
  }

  @GetMapping
  public ResponseEntity<List<LogDTO>> retrieveLogs(){
      List<LogDTO> logs=logService.retrieveAllLogsFromDB();
      return ResponseEntity.status(HttpStatus.OK).body(logs);
  }

  @GetMapping("/{userid}")
    public ResponseEntity<List<LogDTO>> retrieveLogsByUserId(@PathVariable("userid") UUID userid){
      List<LogDTO> logsByUserId=logService.retrieveAllLogsByUserId(userid);
      return  ResponseEntity.status(HttpStatus.OK).body(logsByUserId);
  }

  @GetMapping("/logId/{logId}")
    public ResponseEntity<LogDTO> retrieveLogByLogId(@PathVariable("logId") UUID logId){
      LogDTO logDTO=logService.retrieveLogByLogId(logId);
      return ResponseEntity.status(HttpStatus.OK).body(logDTO);
  }
}
