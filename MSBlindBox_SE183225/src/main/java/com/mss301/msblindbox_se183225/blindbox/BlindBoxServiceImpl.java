package com.mss301.msblindbox_se183225.blindbox;

import com.mss301.msblindbox_se183225.blindboxcategories.BlindBoxCategory;
import com.mss301.msblindbox_se183225.blindboxcategories.BlindBoxCategoryRepository;
import com.mss301.msblindbox_se183225.externalsecurity.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindBoxServiceImpl implements BlindBoxService {

    private final BlindBoxRepository blindBoxRepository;

    private final RestTemplate restTemplate;
    private final BlindBoxCategoryRepository blindBoxCategoryRepository;

    @Override
    public List<BlindBoxDTO> findAll() {
        return blindBoxRepository.findAll().stream().map(b ->
                BlindBoxDTO.builder()
                        .id(b.getId())
                        .name(b.getName())
                        .categoryId(b.getCategory().getId())
                        .categoryName(b.getCategory().getName())
                        .brandId(b.getBrandId())
                        .rarity(b.getRarity())
                        .price(b.getPrice())
                        .releaseDate(b.getReleaseDate())
                        .stock(b.getStock())
                        .build()
        ).toList();
    }

    @Override
    public BlindBoxDTO findById(Integer id) {
        BlindBox b = blindBoxRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Blind box not found with id: " + id));
        return BlindBoxDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .categoryId(b.getCategory().getId())
                .categoryName(b.getCategory().getName())
                .brandId(b.getBrandId())
                .rarity(b.getRarity())
                .price(b.getPrice())
                .releaseDate(b.getReleaseDate())
                .stock(b.getStock())
                .build();
    }

    @Override
    @Transactional
    public void delete(Integer id, String token) {
        if (blindBoxRepository.findById(id).isEmpty()) {
            throw new BadRequestException("Blind box not found with id: " + id);
        }
        blindBoxRepository.deleteById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        ResponseEntity<?> sync = restTemplate.exchange(
                "http://localhost:8080/api/brand-service/blind-boxes/" + id,
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
        if (!restTemplate.exchange(
                "http://localhost:8080/brand-service/brands/" + request.getBrandId(),
                HttpMethod.GET,
                null,
                ResponseEntity.class
        ).getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Brand not found with id: " + request.getBrandId());
        }
        BlindBox blindBox = BlindBox.builder()
                .id(request.getId())
                .name(request.getName())
                .category(null)
                .brandId(request.getBrandId())
                .rarity(request.getRarity())
                .price(request.getPrice())
                .releaseDate(request.getReleaseDate())
                .stock(request.getStock())
                .build();
        BlindBoxCategory category = blindBoxCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + request.getCategoryId()));
        blindBox.setCategory(category);
        blindBox = blindBoxRepository.save(blindBox);
        if (request.getId() == null) {
            request.setId(blindBox.getId());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> sync = restTemplate.exchange(
                "http://localhost:8080/api/brand-service/blind-boxes",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ResponseEntity.class
        );
        if (!sync.getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Failed to sync with brand service");
        }
        return blindBox.getId();
    }

    @Override
    public void update(BlindBoxCreateRequest request, String token) {
        if (request.getId() == null) {
            throw new BadRequestException("ID cannot be null for update");
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
        if (!restTemplate.exchange(
                "http://localhost:8080/brand-service/brands/" + request.getBrandId(),
                HttpMethod.GET,
                null,
                ResponseEntity.class
        ).getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Brand not found with id: " + request.getBrandId());
        }
        blindBox.setName(request.getName());
        blindBox.setCategory(null);
        blindBox.setBrandId(request.getBrandId());
        blindBox.setRarity(request.getRarity());
        blindBox.setPrice(request.getPrice());
        blindBox.setReleaseDate(request.getReleaseDate());
        blindBox.setStock(request.getStock());
        BlindBoxCategory category = blindBoxCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + request.getCategoryId()));
        blindBox.setCategory(category);
        blindBox = blindBoxRepository.save(blindBox);
        if (request.getId() == null) {
            request.setId(blindBox.getId());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> sync = restTemplate.exchange(
                "http://localhost:8080/api/brand-service/blind-boxes",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                ResponseEntity.class
        );
        if (!sync.getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Failed to sync with brand service");
        }
    }
}
