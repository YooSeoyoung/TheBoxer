package com.dw.NAMANSOLOJAVA.Controller;

import com.dw.NAMANSOLOJAVA.DTO.CategoryPlaceDTO;
import com.dw.NAMANSOLOJAVA.DTO.RecommendPlaceAdmDTO;
import com.dw.NAMANSOLOJAVA.Service.CategoryPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categoryplace")
public class CategoryPlaceController {
    @Autowired
    CategoryPlaceService categoryPlaceService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/category/{name}")
    public ResponseEntity<List<RecommendPlaceAdmDTO>> getPlacesByCategory(@PathVariable String name) {
        return ResponseEntity.ok(categoryPlaceService.getPlacesByCategory(name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Void> addMapping(@RequestBody CategoryPlaceDTO dto) {
        categoryPlaceService.addMapping(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long id) {
        categoryPlaceService.deleteMapping(id);
        return ResponseEntity.ok().build();
    }
}