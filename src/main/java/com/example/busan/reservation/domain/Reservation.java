package com.example.busan.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

    private static final int MAXIMUM_RESERVED_HOUR = 3;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdTime;
    @CreatedBy
    @Column(nullable = false)
    private String reservationEmail;

    protected Reservation() {
    }

    public Reservation(final Long id,
                       final Status status,
                       final LocalDateTime startTime,
                       final LocalDateTime endTime) {
        Assert.notNull(status, "예약 상태가 필요합니다.");
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("시작 시각보다 종료 시각이 이전일 수 없습니다.");
        }
        if (startTime.plusHours(MAXIMUM_RESERVED_HOUR).isBefore(endTime)) {
            throw new IllegalArgumentException("예약 시각은 최대 3시간까지만 가능합니다.");
        }
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Reservation(final LocalDateTime startTime, final LocalDateTime endTime) {
        this(null, Status.RESERVED, startTime, endTime);
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public String getReservationEmail() {
        return reservationEmail;
    }
}