package com.hotelsystems.ai.bookingmanagement.supplier.adapter;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for accessing supplier adapters by supplier code.
 */
@Component
public class SupplierAdapterRegistry {

    private final Map<SupplierCode, SupplierOfferSearchAdapter> offerSearchAdapters;
    private final Map<SupplierCode, SupplierRecheckAdapter> recheckAdapters;
    private final Map<SupplierCode, SupplierBookingAdapter> bookingAdapters;

    public SupplierAdapterRegistry(List<SupplierOfferSearchAdapter> offerSearchAdapters,
                                  List<SupplierRecheckAdapter> recheckAdapters,
                                  List<SupplierBookingAdapter> bookingAdapters) {
        this.offerSearchAdapters = new HashMap<>();
        this.recheckAdapters = new HashMap<>();
        this.bookingAdapters = new HashMap<>();
        
        // Build maps from implementations
        for (SupplierOfferSearchAdapter adapter : offerSearchAdapters) {
            SupplierCode code = determineSupplierCode(adapter);
            if (code != null) {
                this.offerSearchAdapters.put(code, adapter);
            }
        }
        
        for (SupplierRecheckAdapter adapter : recheckAdapters) {
            SupplierCode code = determineSupplierCode(adapter);
            if (code != null) {
                this.recheckAdapters.put(code, adapter);
            }
        }
        
        for (SupplierBookingAdapter adapter : bookingAdapters) {
            SupplierCode code = determineSupplierCode(adapter);
            if (code != null) {
                this.bookingAdapters.put(code, adapter);
            }
        }
    }

    public SupplierOfferSearchAdapter getOfferSearchAdapter(SupplierCode supplierCode) {
        SupplierOfferSearchAdapter adapter = offerSearchAdapters.get(supplierCode);
        if (adapter == null) {
            throw new IllegalArgumentException("No offer search adapter found for supplier: " + supplierCode);
        }
        return adapter;
    }

    public SupplierRecheckAdapter getRecheckAdapter(SupplierCode supplierCode) {
        SupplierRecheckAdapter adapter = recheckAdapters.get(supplierCode);
        if (adapter == null) {
            throw new IllegalArgumentException("No recheck adapter found for supplier: " + supplierCode);
        }
        return adapter;
    }

    public SupplierBookingAdapter getBookingAdapter(SupplierCode supplierCode) {
        SupplierBookingAdapter adapter = bookingAdapters.get(supplierCode);
        if (adapter == null) {
            throw new IllegalArgumentException("No booking adapter found for supplier: " + supplierCode);
        }
        return adapter;
    }

    private SupplierCode determineSupplierCode(Object adapter) {
        String className = adapter.getClass().getSimpleName();
        if (className.contains("Hotelbeds") || className.contains("HotelBeds")) {
            return SupplierCode.HOTELBEDS;
        } else if (className.contains("Travellanda") || className.contains("TravelLanda")) {
            return SupplierCode.TRAVELLANDA;
        }
        return null;
    }
}

