package com.casavida.backend.repository;

import com.casavida.backend.entity.Lead;
import com.casavida.backend.entity.ELeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(ELeadStatus status);
}
