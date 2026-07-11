package org.lpu.dev.codes.repository;

import java.util.List;
import java.util.Optional;

import org.lpu.dev.codes.model.data.GymnasiumReservation;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class GymnasiumReservationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(GymnasiumReservation reservation) {
        entityManager.persist(reservation);
        entityManager.flush();
    }

    public List<GymnasiumReservation> findAllApproved() {
        return entityManager
                .createQuery("FROM GymnasiumReservation r WHERE r.status IN ('APPROVED', 'COMPLETED') ORDER BY r.createdAt DESC", GymnasiumReservation.class)
                .getResultList();
    }

    public Optional<GymnasiumReservation> findById(Long id) {
        GymnasiumReservation result = entityManager.find(GymnasiumReservation.class, id);
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findAllNative() {
        return entityManager.createNativeQuery(
                "SELECT id, event_title, department, organization, number_of_attendees, " +
                "contact_person, contact_email, contact_number, " +
                "reserved_dates::text, requested_equipment::text, " +
                "status, created_at, " +
                "coordination_date, coordination_start_time, coordination_end_time, " +
                "satisfaction_rating, additional_instructions, approved_at, approved_by " +
                "FROM gymnasium_reservations ORDER BY " +
                "CASE WHEN status = 'PENDING' THEN 0 ELSE 1 END, " +
                "CASE WHEN status = 'PENDING' THEN created_at END ASC, " +
                "created_at DESC")
                .getResultList();
    }

    public List<GymnasiumReservation> findAllForConflictCheck() {
        return entityManager
                .createQuery(
                        "FROM GymnasiumReservation r WHERE r.status IN ('PENDING', 'APPROVED', 'COMPLETED')",
                        GymnasiumReservation.class)
                .getResultList();
    }

    public List<GymnasiumReservation> findByStatus(String status) {
        return entityManager
                .createQuery("FROM GymnasiumReservation r WHERE r.status = :status", GymnasiumReservation.class)
                .setParameter("status", status)
                .getResultList();
    }

    public void approve(Long id, String approvedBy) {
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET status = 'APPROVED', approved_at = CURRENT_TIMESTAMP, approved_by = :approvedBy WHERE id = :id")
                .setParameter("approvedBy", approvedBy)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void updateStatus(Long id, String status) {
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET status = :status WHERE id = :id")
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void updateStatusBatch(List<Long> ids, String status) {
        if (ids == null || ids.isEmpty()) return;
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET status = :status WHERE id IN (:ids)")
                .setParameter("status", status)
                .setParameter("ids", ids)
                .executeUpdate();
    }

    public void updateCoordination(Long id, String date, String startTime, String endTime) {
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET coordination_date = :date, " +
                "coordination_start_time = :startTime, coordination_end_time = :endTime " +
                "WHERE id = :id")
                .setParameter("date", date)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void reschedule(Long id, String reservedDatesJson) {
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET reserved_dates = CAST(:json AS jsonb) WHERE id = :id")
                .setParameter("json", reservedDatesJson)
                .setParameter("id", id)
                .executeUpdate();
    }

    public void updateRating(Long id, int rating) {
        entityManager.createNativeQuery(
                "UPDATE gymnasium_reservations SET satisfaction_rating = :rating WHERE id = :id")
                .setParameter("rating", rating)
                .setParameter("id", id)
                .executeUpdate();
    }
}
