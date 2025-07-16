package com.mss301.msbrand_se183225.blindbox;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BlindBoxController {

    private final BlindBoxService blindBoxService;

    @DeleteMapping("/api/blind-boxes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBlindBox(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token
    ) {
        blindBoxService.delete(id, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/blind-boxes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBlindBox(
            @RequestBody BlindBoxCreateRequest request,
            @RequestHeader("Authorization") String token
    ) {
        URI location = URI.create("/api/public/blind-boxes/" + blindBoxService.create(request, token));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/api/blind-boxes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBlindBox(
            @RequestBody BlindBoxCreateRequest request,
            @RequestHeader("Authorization") String token
    ) {
        blindBoxService.update(request, token);
        return ResponseEntity.ok().build();
    }

}
