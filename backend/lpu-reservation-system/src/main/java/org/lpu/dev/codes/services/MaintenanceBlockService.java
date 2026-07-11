package org.lpu.dev.codes.services;

import java.util.List;
import java.util.stream.Collectors;

import org.lpu.dev.codes.model.data.MaintenanceBlock;
import org.lpu.dev.codes.model.dto.MaintenanceBlockDto;
import org.lpu.dev.codes.repository.MaintenanceBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaintenanceBlockService {

    @Autowired
    private MaintenanceBlockRepository repo;

    public List<MaintenanceBlockDto> getByFacility(String facilityType) {
        return repo.findByFacilityType(facilityType.toUpperCase())
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public MaintenanceBlockDto create(String facilityType, String blockDate, String startTime, String endTime, String reason) {
        MaintenanceBlock block = new MaintenanceBlock();
        block.setFacilityType(facilityType.toUpperCase());
        block.setBlockDate(blockDate);
        block.setStartTime(startTime);
        block.setEndTime(endTime);
        block.setReason(reason);
        repo.save(block);
        return toDto(block);
    }

    @Transactional
    public boolean delete(Long id) {
        return repo.deleteById(id);
    }

    private MaintenanceBlockDto toDto(MaintenanceBlock b) {
        return new MaintenanceBlockDto(
            b.getId(),
            b.getFacilityType(),
            b.getBlockDate(),
            b.getStartTime(),
            b.getEndTime(),
            b.getReason(),
            b.getCreatedAt() != null ? b.getCreatedAt().toString() : null
        );
    }
}
