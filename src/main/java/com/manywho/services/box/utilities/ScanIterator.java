package com.manywho.services.box.utilities;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.List;

public class ScanIterator implements Iterator<List<String>> {
    private final Jedis jedis;
    private final ScanParams scanParams;

    private ScanResult<String> scanResult;

    public ScanIterator(Jedis jedis, ScanParams scanParams) {
        this.jedis = jedis;
        this.scanParams = scanParams;
    }

    @Override
    public boolean hasNext() {
        // If we have no scan result yet, then we haven't done the first scan so force it
        if (scanResult == null) {
            return true;
        }

        // The last iteration should return 0, so check for that
        return !scanResult.getStringCursor().equals("0");
    }

    @Override
    public List<String> next() {
        if (scanResult == null) {
            // If we don't have anything yet, do an initial scan
            scanResult = jedis.scan("0", scanParams);
        }

        // Pre-get the scan result
        List<String> result = scanResult.getResult();

        // Fetch the next set of results using the cursor
        scanResult = jedis.scan(scanResult.getStringCursor(), scanParams);

        return result;
    }
}