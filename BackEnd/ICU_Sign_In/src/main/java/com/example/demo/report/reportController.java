package com.example.demo.report;


import com.example.demo.model.FileHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class reportController {

    @Autowired
    ReportRepository reportRepository;

    @GetMapping("/getReports")
    public ResponseEntity<List<FileHistory>> getReports(){
        List<FileHistory> fileHistory = reportRepository.findAll();
        return ResponseEntity.ok(fileHistory);
    }
}
