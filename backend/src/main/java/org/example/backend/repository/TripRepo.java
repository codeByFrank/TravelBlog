package org.example.backend.repository;

import org.example.backend.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepo extends MongoRepository<Trip, String> {
    List<Trip> findAllByUserIdOrderByStartDateDesc(String userId);

    Optional<Trip> findByIdAndUserId(String id, String userId);
}
