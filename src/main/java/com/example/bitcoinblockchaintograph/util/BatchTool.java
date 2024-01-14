package com.example.bitcoinblockchaintograph.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch related manipulations.
 */
public class BatchTool {
    /**
     * Convert List of T to List of List of T.
     * @param listToBatch initial list to transform
     * @param batchSize size of batch list
     * @return list of batches
     * @param <T> parameter of List
     */
    public static <T> List<List<T>> getBatchedLists(
            List<T> listToBatch, Integer batchSize) {
        List<List<T>> batchedList = new ArrayList<>();
        for (int i = 0; i < listToBatch.size(); i += batchSize) {
            batchedList.add(listToBatch.subList(i, Math.min(i + batchSize, listToBatch.size())));
        }
        return batchedList;
    }
}
