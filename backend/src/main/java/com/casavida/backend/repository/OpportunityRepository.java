package com.casavida.backend.repository;

import com.casavida.backend.entity.Opportunity;
import com.casavida.backend.entity.EOpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    List<Opportunity> findByStatus(EOpportunityStatus status);
}
