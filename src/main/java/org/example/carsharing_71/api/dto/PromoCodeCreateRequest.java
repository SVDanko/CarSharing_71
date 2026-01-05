package org.example.carsharing_71.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Входной DTO для создания промокода.
 * Аннотации проверяют обязательные поля. AssertTrue - инварианты интервала и процента
 */
public class PromoCodeCreateRequest {
    @NotBlank
    @Pattern(regexp = "[A-Z0-9_\\-]{3,64}")
    private String code;
    @NotNull
    private String discountType;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal discountValue;
    @Positive
    private Integer maxUsageCount;
    private String currency;
    @NotNull @Future
    private Instant startAt;
    @NotNull @Future
    private Instant endAt;

    @AssertTrue(message = "startAt must be before endAt")
    public boolean isTimeRangeValid() {
        return startAt != null && endAt != null && startAt.isBefore(endAt);
    }

    @AssertTrue(message = "percent value must be between 1 and 100 for PERCENT")
    public boolean isPercentValid() {
        if (discountType == null || discountValue == null) return true;
        if ("PERCENT".equals(discountType)) {
            return discountValue.compareTo(new BigDecimal("1")) >= 0 &&
                    discountValue.compareTo(new BigDecimal("100")) <= 100;
        }
        return true;
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

    public Integer getMaxUsageCount() {
        return maxUsageCount;
    }

    public void setMaxUsageCount(Integer maxUsageCount) {
        this.maxUsageCount = maxUsageCount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
}
