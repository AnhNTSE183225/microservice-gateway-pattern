package com.mss301.msblindbox_se183225.blindbox;

import com.mss301.msblindbox_se183225.externalsecurity.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class BlindBoxController {

    private final BlindBoxService blindBoxService;

    @GetMapping("/api/public/blind-boxes")
    public ResponseEntity<?> getAllBlindBoxes() {
        return ResponseEntity.ok(blindBoxService.findAll());
    }

    @DeleteMapping("/api/blind-boxes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBlindBox(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        blindBoxService.delete(id, principal.token());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/blind-boxes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBlindBox(
            @RequestBody BlindBoxCreateRequest request,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        URI location = URI.create("/api/public/blind-boxes/" + blindBoxService.create(request, principal.token()));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/api/blind-boxes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBlindBox(
            @RequestBody BlindBoxCreateRequest request,
            @AuthenticationPrincipal String token
    ) {
        blindBoxService.update(request, token);
        return ResponseEntity.ok().build();
    }

}
