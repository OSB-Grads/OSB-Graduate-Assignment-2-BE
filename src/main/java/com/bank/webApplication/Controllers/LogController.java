package com.bank.webApplication.Controllers;

import com.bank.webApplication.Dto.LogDTO;
import com.bank.webApplication.Services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
