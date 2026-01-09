package com.hotelsystems.ai.bookingmanagement.supplier.adapter;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierRecheckResultDto;

/**
 * Adapter interface for rechecking supplier offers.
 */
public interface SupplierRecheckAdapter {
    
    /**
     * Recheck an offer to verify availability and price.
     * 
     * @param offerPayloadJson the original offer payload as JSON
     * @return recheck result
     */
    SupplierRecheckResultDto recheck(String offerPayloadJson);
}

