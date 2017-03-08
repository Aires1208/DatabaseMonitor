package com.zte.ums.oespaas.mysql.message;

import org.junit.Test;

/**
 * Created by 10203846 on 10/24/16.
 */
public class MockCometdClientTest {

    @Test
    public void testInitClient() throws Exception {
        MockCometdClient.connect();
    }
}