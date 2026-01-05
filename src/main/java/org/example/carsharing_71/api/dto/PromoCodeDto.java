package org.example.carsharing_71.api.dto;

import org.example.carsharing_71.domain.PromoCode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Выходной DTO для промокода
 * Маппинг из доменной сущности в плоскую структуру, готовую для JSON
 */
public class PromoCodeDto {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private String currency;
    private Integer usedCount = 0;
    private Integer maxUsageCount;
    private Instant startAt;
    private Instant endAt;
    private String status;
    private Instant createdAt;

    public static PromoCodeDto fromEntity(PromoCode p) {
        PromoCodeDto dto = new PromoCodeDto();
        dto.id = p.getId();
        dto.code = p.getCode();
        dto.discountType = p.getDiscountType().name();
        dto.discountValue = p.getDiscountValue();
        dto.currency = p.getCurrency();
        dto.usedCount = p.getUsedCount();
        dto.maxUsageCount = p.getMaxUsageCount();
        dto.startAt = p.getStartAt();
        dto.endAt = p.getEndAt();
        dto.status = p.getStatus().name();
        dto.createdAt = p.getCreatedAt();

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getMaxUsageCount() {
        return maxUsageCount;
    }

    public void setMaxUsageCount(Integer maxUsageCount) {
        this.maxUsageCount = maxUsageCount;
    }
}
