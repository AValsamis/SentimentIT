package gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData;

import com.squareup.otto.Bus;

/**
 * Created by Sevle on 3/31/2015.
 */
// Provided by Square under the Apache License
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}