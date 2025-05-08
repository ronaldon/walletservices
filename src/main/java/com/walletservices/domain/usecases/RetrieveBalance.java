package com.walletservices.domain.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RetrieveBalance {
    BigDecimal getBalance(Long walletId);
    BigDecimal getHistoricalBalance(Long walletId, LocalDateTime timestamp);
}
