package com.csdg1t3.ryverbankapi.transfer;

import java.util.List;

/**
 * Convenience interface for a service class allowing TransferController to read and modify data
 */
public interface TransferService {
    List<Transfer> listTransfers(Long accountId);
    Transfer getTransfer(Long transferId, Long accountId);
        
    /**
     * Return newly added transfer
     */
    Transfer addTransfer(Transfer transfer);
}