package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Async variant of {@link ElasticWriter} that sends bulk requests to
 * Elasticsearch concurrently using a fixed thread pool.
 *
 * <p>The number of parallel in-flight requests is controlled by the
 * {@code asyncThreads} pipeline property (default: 2). Backpressure is
 * applied automatically: when all threads are busy, the pipeline blocks
 * until a slot becomes available.</p>
 *
 * <p>All other configuration properties are inherited from
 * {@link ElasticWriter}.</p>
 */
public class AsyncElasticWriter extends ElasticWriter {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncElasticWriter.class);

    private int asyncThreads;
    private ExecutorService executorService;
    private final List<Future<?>> pendingFutures = new ArrayList<>();

    @Override
    public void init() {
        asyncThreads = getPropertyAsInt("asyncThreads", 2);
        super.init();
        executorService = Executors.newFixedThreadPool(asyncThreads);
        LOG.info("AsyncElasticWriter initialized with {} async threads", asyncThreads);
    }

    /**
     * Submit the bulk payload to the thread pool instead of sending it
     * synchronously.
     */
    @Override
    protected void sendBulkPayload(String bulkPayload) {
        Future<?> future = executorService.submit(() -> {
            try {
                super.sendBulkPayload(bulkPayload);
            } catch (Exception e) {
                if (failOnError) {
                    throw new RuntimeException(e);
                } else {
                    LOG.error("Async bulk request failed: ", e);
                    LOG.info("Continue processing ...");
                }
            }
        });
        pendingFutures.add(future);
    }

    @Override
    public void document(Document document) {
        // Check completed futures for errors early
        checkPendingFutures();

        // Apply backpressure: if all threads are busy, wait for the oldest to finish
        if (pendingFutures.size() >= asyncThreads) {
            LOG.debug("All async threads busy, waiting for a bulk request to complete...");
            awaitOldestFuture();
        }

        super.document(document);
    }

    @Override
    public void end() {
        // Flush the remaining buffer (async submit via sendBulkPayload)
        procesBuffer();
        buffer = new ArrayList<>();

        // Wait for all in-flight requests to complete
        awaitAllFutures();

        // Shut down the thread pool
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                LOG.warn("Executor did not terminate within 60 seconds, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Delegate to ElasticWriter.end() for housekeeping, alias switching, etc.
        // processBuffer() will be a no-op since we already cleared the buffer.
        super.end();
    }

    // ------------------------------------------------------------------
    // Future management helpers
    // ------------------------------------------------------------------

    /**
     * Check completed futures for errors and remove them from the pending list.
     */
    private void checkPendingFutures() {
        Iterator<Future<?>> it = pendingFutures.iterator();
        while (it.hasNext()) {
            Future<?> future = it.next();
            if (future.isDone()) {
                it.remove();
                propagateError(future);
            }
        }
    }

    /**
     * Wait for the oldest pending future to finish, freeing up a thread slot.
     */
    private void awaitOldestFuture() {
        if (pendingFutures.isEmpty()) {
            return;
        }
        Future<?> oldest = pendingFutures.remove(0);
        propagateError(oldest);
    }

    /**
     * Block until every pending future has completed.
     */
    private void awaitAllFutures() {
        for (Future<?> future : pendingFutures) {
            propagateError(future);
        }
        pendingFutures.clear();
    }

    /**
     * Get the result of a future, propagating any exception according to
     * the {@code failOnError} setting.
     */
    private void propagateError(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for async bulk request", e);
        } catch (ExecutionException e) {
            if (failOnError) {
                throw new RuntimeException("Async bulk request failed", e.getCause());
            } else {
                LOG.error("Async bulk request failed: ", e.getCause());
            }
        }
    }
}
