package org.lpu.dev.codes.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lpu.dev.codes.model.data.Driver;
import org.lpu.dev.codes.model.dto.CreateDriverRequest;
import org.lpu.dev.codes.model.dto.PopulateDriverList;
import org.lpu.dev.codes.model.dto.UpdateDriverRequest;
import org.lpu.dev.codes.repository.DriverRepository;
import org.lpu.dev.codes.repository.VanReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {

    private static final Logger logger = LogManager.getLogger(DriverService.class);

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VanReservationRepository vanReservationRepository;

    public List<PopulateDriverList> getAllDrivers() {
        return mapList(driverRepository.findAll());
    }

    public List<PopulateDriverList> getActiveDrivers() {
        return mapList(driverRepository.findActive());
    }

    @Transactional
    public boolean createDriver(CreateDriverRequest req) {
        try {
            Driver driver = new Driver();
            driver.setFullName(req.getFullName().trim());
            driver.setContactNumber(req.getContactNumber());
            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                driver.setStatus(req.getStatus());
            }
            driverRepository.save(driver);
            return true;
        } catch (Exception e) {
            logger.error("Failed to create driver", e);
            return false;
        }
    }

    @Transactional
    public boolean updateDriver(UpdateDriverRequest req) {
        try {
            var opt = driverRepository.findById(req.getId());
            if (opt.isEmpty()) return false;
            Driver driver = opt.get();
            driver.setFullName(req.getFullName().trim());
            driver.setContactNumber(req.getContactNumber());
            if (req.getStatus() != null && !req.getStatus().isBlank()) {
                driver.setStatus(req.getStatus());
            }
            driverRepository.save(driver);
            return true;
        } catch (Exception e) {
            logger.error("Failed to update driver {}", req.getId(), e);
            return false;
        }
    }

    @Transactional
    public boolean toggleStatus(Long id) {
        var opt = driverRepository.findById(id);
        if (opt.isEmpty()) return false;
        Driver driver = opt.get();
        String newStatus = "ACTIVE".equalsIgnoreCase(driver.getStatus()) ? "INACTIVE" : "ACTIVE";
        return driverRepository.updateStatus(id, newStatus);
    }

    @Transactional
    public boolean deleteDriver(Long id) {
        if (driverRepository.findById(id).isEmpty()) {
            return false;
        }
        vanReservationRepository.clearDriverReferences(id);
        return driverRepository.deleteById(id);
    }

    private List<PopulateDriverList> mapList(List<Driver> drivers) {
        List<PopulateDriverList> result = new ArrayList<>();
        for (Driver d : drivers) {
            PopulateDriverList dto = new PopulateDriverList();
            dto.setId(d.getId());
            dto.setFullName(d.getFullName());
            dto.setContactNumber(d.getContactNumber());
            dto.setStatus(d.getStatus());
            result.add(dto);
        }
        return result;
    }
}
