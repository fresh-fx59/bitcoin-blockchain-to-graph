package com.example.bitcoinblockchaintograph.handler;

import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import com.example.bitcoinblockchaintograph.repository.postgres.BitcoinBlockInitialDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * Taken from here
 * https://www.logicbig.com/how-to/java/fork-and-join-recursive-action.html
 */
@Slf4j
@RequiredArgsConstructor
public class ProcessDataFromJsonRpcMultiThread extends RecursiveAction {
    private final int start;
    private final int end;
    private final int threshold;
    private final BitcoinClient client;
    private final BitcoinBlockInitialDataRepository repository;
    private final TransactionTemplate transactionTemplate;

    @SneakyThrows
    @Override
    protected void compute() {
        if (end - start <= threshold) {
            List<BitcoinBlockInitialData> blocksData = new ArrayList<>();

            for (int i = start; i <= end; i++) {
                String hash = String.valueOf(client.getBlockHash(i));
                blocksData.add(new BitcoinBlockInitialData(hash, i));
            }
            transactionTemplate.execute(
                    (status) -> repository.saveAll(blocksData)
            );
//            repository.saveAll(blocksData);
//            repository.flush();
        } else {
            List<ProcessDataFromJsonRpcMultiThread> subtasks = new ArrayList<>();
            int mid = start + (end - start) / 2;
            ProcessDataFromJsonRpcMultiThread left = new ProcessDataFromJsonRpcMultiThread(start, mid, threshold, client, repository, transactionTemplate);
            ProcessDataFromJsonRpcMultiThread right = new ProcessDataFromJsonRpcMultiThread(mid + 1, end, threshold, client, repository, transactionTemplate);
            subtasks.add(left);
            subtasks.add(right);
            ForkJoinTask.invokeAll(subtasks);
        }
    }
}