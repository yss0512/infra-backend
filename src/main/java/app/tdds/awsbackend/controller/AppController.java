package app.tdds.awsbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

  @GetMapping("/")
  public String home() {
    return "Hello, World";
  }

  @GetMapping("/health")
  public ResponseEntity<?> checkEC2Status() {
    return new ResponseEntity<String>("success Health", HttpStatus.OK);
  }
}