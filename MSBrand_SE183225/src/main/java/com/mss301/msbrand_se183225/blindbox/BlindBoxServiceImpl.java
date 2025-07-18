package com.mss301.msbrand_se183225.blindbox;

import com.mss301.msbrand_se183225.brand.Brand;
import com.mss301.msbrand_se183225.brand.BrandRepository;
import com.mss301.msbrand_se183225.externalsecurity.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BlindBoxServiceImpl implements BlindBoxService {

    private final BlindBoxRepository blindBoxRepository;
    private final RestTemplate restTemplate;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public void delete(Integer id, String token) {
        if (blindBoxRepository.findById(id).isEmpty()) {
            throw new BadRequestException("Blind box not found with id: " + id);
        }
        blindBoxRepository.deleteById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<?> sync = restTemplate.exchange(
                "http://localhost:8080/blind-box-service/api/blind-boxes/" + id,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                ResponseEntity.class
        );
        if (!sync.getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Failed to sync with brand service");
        }
    }

    @Override
    @Transactional
    public Integer create(BlindBoxCreateRequest request, String token) {
        if (request.getId() == null) {
            request.setId(((int) blindBoxRepository.count()) + 1);
        }
        if (blindBoxRepository.findById(request.getId()).isPresent()) {
            throw new BadRequestException("Blind box already exists with id: " + request.getId());
        }
        if (request.getStock() > 100) {
            throw new BadRequestException("Stock cannot exceed 100");
        }
        if (request.getStock() < 1) {
            throw new BadRequestException("Stock must be at least 1");
        }
        if (request.getPrice() < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (request.getName().trim().length() <= 10) {
            throw new BadRequestException("Name must be at least 11 characters long");
        }
        if (!request.getReleaseDate().equals(LocalDate.now())) {
            throw new BadRequestException("Release date must be today");
        }
        BlindBox blindBox = BlindBox.builder()
                .id(request.getId())
                .name(request.getName())
                .categoryId(request.getCategoryId())
                .brand(null)
                .rarity(request.getRarity())
                .price(request.getPrice())
                .releaseDate(request.getReleaseDate())
                .stock(request.getStock())
                .build();
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new BadRequestException("Brand not found with id: " + request.getBrandId()));
        blindBox.setBrand(brand);
        blindBox = blindBoxRepository.save(blindBox);

        if (request.getId() == null) {
            request.setId(blindBox.getId());
        }

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        ResponseEntity<?> sync = restTemplate.exchange(
//                "http://localhost:8080/blind-box-service/api/blind-boxes",
//                HttpMethod.POST,
//                new HttpEntity<>(request, headers),
//                ResponseEntity.class
//        );
//        if (!sync.getStatusCode().is2xxSuccessful()) {
//            throw new BadRequestException("Failed to sync with blind-box service" + sync.getStatusCode());
//        }
        return blindBox.getId();
    }

    @Override
    public void update(BlindBoxCreateRequest request, String token) {
        if (request.getId() == null) {
            throw new BadRequestException("ID must not be null");
        }
        BlindBox blindBox = blindBoxRepository.findById(request.getId())
                .orElseThrow(() -> new BadRequestException("Blind box not found with id: " + request.getId()));
        if (request.getStock() > 100) {
            throw new BadRequestException("Stock cannot exceed 100");
        }
        if (request.getStock() < 1) {
            throw new BadRequestException("Stock must be at least 1");
        }
        if (request.getPrice() < 0) {
            throw new BadRequestException("Price cannot be negative");
        }
        if (request.getName().trim().length() <= 10) {
            throw new BadRequestException("Name must be at least 11 characters long");
        }
        if (!request.getReleaseDate().equals(LocalDate.now())) {
            throw new BadRequestException("Release date must be today");
        }
        blindBox.setId(request.getId());
        blindBox.setName(request.getName());
        blindBox.setCategoryId(request.getCategoryId());
        blindBox.setBrand(null);
        blindBox.setRarity(request.getRarity());
        blindBox.setPrice(request.getPrice());
        blindBox.setReleaseDate(request.getReleaseDate());
        blindBox.setStock(request.getStock());
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new BadRequestException("Brand not found with id: " + request.getBrandId()));
        blindBox.setBrand(brand);
        blindBox = blindBoxRepository.save(blindBox);

        if (request.getId() == null) {
            request.setId(blindBox.getId());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> sync = restTemplate.exchange(
                "http://localhost:8080/blind-box-service/api/blind-boxes",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                ResponseEntity.class
        );
        if (!sync.getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Failed to sync with blind-box service" + sync.getStatusCode());
        }
    }
}
