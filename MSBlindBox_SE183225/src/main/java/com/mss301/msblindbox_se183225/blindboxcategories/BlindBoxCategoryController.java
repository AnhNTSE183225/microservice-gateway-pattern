package com.mss301.msblindbox_se183225.blindboxcategories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlindBoxCategoryController {

    private final BlindBoxCategoryService blindBoxCategoryService;

    @GetMapping("/api/public/blind-box-categories")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(blindBoxCategoryService.findAll());
    }

    @GetMapping("/api/public/blind-box-categories/{id}")
    public ResponseEntity<?> findById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(blindBoxCategoryService.findById(id));
    }
}
