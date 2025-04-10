package com.dw.NAMANSOLOJAVA.Controller;

import com.dw.NAMANSOLOJAVA.DTO.OfficialEventDTO;
import com.dw.NAMANSOLOJAVA.Service.OfficialEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officialevent")
public class OfficialEventController {
    @Autowired
    OfficialEventService officialEventService;

    @GetMapping("/all")
    public ResponseEntity<List<OfficialEventDTO>> getAllOfficialEvent() {
        return new ResponseEntity<>(officialEventService.getAllOfficialEvent(), HttpStatus.OK);
    }

    @GetMapping("/single/{id}")
    public ResponseEntity<OfficialEventDTO> getSingleOfficialEvent(@PathVariable Long id) {
        return new ResponseEntity<>(officialEventService.getSingleOfficialEvent(id), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<OfficialEventDTO> saveOfficialEventToUserTodo(@RequestBody OfficialEventDTO officialEventDTO) {
        return new ResponseEntity<>(officialEventService.saveOfficialEvent(officialEventDTO), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OfficialEventDTO> updateOfficialEvent(@PathVariable Long id,@RequestBody OfficialEventDTO officialEventDTO) {
        return new ResponseEntity<>(officialEventService.updateOfficialEvent(id, officialEventDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOfficialEvent(@PathVariable Long id) {
        return new ResponseEntity<>(officialEventService.deleteOfficialEvent(id), HttpStatus.OK);
    }
}
