package com.mss301.msblindbox_se183225.blindbox;

import java.util.List;

public interface BlindBoxService {
    List<BlindBoxDTO> findAll();

    BlindBoxDTO findById(Integer id);

    void delete(Integer id, String token);

    Integer create(BlindBoxCreateRequest request, String token);

    void update(BlindBoxCreateRequest request, String token);
}
