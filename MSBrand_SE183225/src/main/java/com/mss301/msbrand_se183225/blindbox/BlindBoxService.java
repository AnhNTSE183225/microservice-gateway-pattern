package com.mss301.msbrand_se183225.blindbox;

public interface BlindBoxService {

    void delete(Integer id, String token);

    Integer create(BlindBoxCreateRequest request, String token);

    void update(BlindBoxCreateRequest request, String token);
}
