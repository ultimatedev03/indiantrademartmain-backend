package com.itech.itech_backend.modules.core.repository;

import com.itech.itech_backend.modules.core.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByIsActiveTrue();

    List<Location> findByStateIgnoreCaseAndIsActiveTrue(String state);

    List<Location> findByCityIgnoreCaseAndIsActiveTrue(String city);

    List<Location> findByStateIgnoreCaseAndCityIgnoreCaseAndIsActiveTrue(String state, String city);

    Optional<Location> findByPincodeAndIsActiveTrue(String pincode);

    List<Location> findByPincodeInAndIsActiveTrue(List<String> pincodes);

    @Query("SELECT DISTINCT l.state FROM Location l WHERE l.isActive = true ORDER BY l.state")
    List<String> findDistinctActiveStates();

    @Query("SELECT DISTINCT l.city FROM Location l WHERE l.state = :state AND l.isActive = true ORDER BY l.city")
    List<String> findDistinctActiveCitiesByState(@Param("state") String state);

    @Query("SELECT l FROM Location l WHERE " +
           "(:country IS NULL OR LOWER(l.country) LIKE LOWER(CONCAT('%', :country, '%'))) AND " +
           "(:state IS NULL OR LOWER(l.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "l.isActive = true")
    List<Location> searchLocations(@Param("country") String country,
                                   @Param("state") String state,
                                   @Param("city") String city);

    boolean existsByStateAndCityAndPincode(String state, String city, String pincode);

    long countByState(String state);

    @Query("SELECT COUNT(DISTINCT l.city) FROM Location l WHERE l.state = :state AND l.isActive = true")
    long countDistinctCitiesByState(@Param("state") String state);
}
