package com.example.bitcoinblockchaintograph.handler;

import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Taken from here
 * https://www.logicbig.com/how-to/java/fork-and-join-recursive-action.html
 */
@Slf4j
public class GetDataFromJsonRpcMultiThread extends RecursiveTask<List<BitcoinBlockInitialData>> {
    private final int start;
    private final int end;
    private final int threshold;
    private final BitcoinClient client;

    public GetDataFromJsonRpcMultiThread(int start, int end, int threshold, BitcoinClient client) {
        this.start = start;
        this.end = end;
        this.threshold = threshold;
        this.client = client;
    }

    @SneakyThrows
    @Override
    protected List<BitcoinBlockInitialData> compute() {
        if (end - start <= threshold) {
            List<BitcoinBlockInitialData> blocksData = new ArrayList<>();

            for (int i = start; i <= end; i++) {
                String hash = String.valueOf(client.getBlockHash(i));
                blocksData.add(new BitcoinBlockInitialData(hash, i));
            }

            return blocksData;
        } else {
            int mid = start + (end - start) / 2;
            GetDataFromJsonRpcMultiThread left = new GetDataFromJsonRpcMultiThread(start, mid, threshold, client);
            GetDataFromJsonRpcMultiThread right = new GetDataFromJsonRpcMultiThread(mid + 1, end, threshold, client);
            left.fork();
            right.fork();
            List<BitcoinBlockInitialData> result = new ArrayList<>();
            result.addAll(left.join());
            result.addAll(right.join());
            return result;
        }
    }
}