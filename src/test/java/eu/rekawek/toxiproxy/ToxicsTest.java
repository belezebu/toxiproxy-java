package eu.rekawek.toxiproxy;

import static eu.rekawek.toxiproxy.model.ToxicDirection.DOWNSTREAM;
import static eu.rekawek.toxiproxy.model.ToxicDirection.UPSTREAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import eu.rekawek.toxiproxy.model.ToxicList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import eu.rekawek.toxiproxy.model.Proxy;
import eu.rekawek.toxiproxy.model.toxic.Bandwidth;

public class ToxicsTest {

    private final ToxiproxyClient tp = new ToxiproxyClient();

    private Proxy proxy;

    private ToxicList toxics;

    @BeforeClass
    public static void toxiproxyEnabled() {
        ToxiproxyClientTest.toxiproxyEnabled();
    }

    @Before
    public void createProxy() throws IOException {
        proxy = tp.createProxy("test-proxy", "127.0.0.1:26379", "localhost:6379");
        toxics = proxy.toxics();
    }

    @After
    public void cleanup() throws IOException {
        proxy.delete();
    }

    @Test
    public void testToxic() throws IOException {
        Toxic toxic = toxics.bandwidth("my-toxic", DOWNSTREAM, 100);
        assertEquals("my-toxic", toxic.getName());
        assertEquals(1.0, toxic.getToxicity(), 0.01);
        assertEquals(DOWNSTREAM, toxic.getStream());

        toxic.setToxicity(0.1f);
        assertEquals(0.1f, toxic.getToxicity(), 0.01);
    }

    @Test
    public void testGetAllToxics() throws IOException {
        toxics.bandwidth("my-toxic", DOWNSTREAM, 100);
        toxics.bandwidth("my-toxic-2", UPSTREAM, 200);

        List<? extends Toxic> list = toxics.getAll();
        assertEquals(2, list.size());

        Toxic t1, t2;
        if ("my-toxic".equals(list.get(0).getName())) {
            t1 = list.get(0);
            t2 = list.get(1);
        } else {
            t1 = list.get(1);
            t2 = list.get(0);
        }
        assertEquals("my-toxic", t1.getName());
        assertEquals(1.0, t1.getToxicity(), 0.01);
        assertEquals(DOWNSTREAM, t1.getStream());
        assertTrue(t1 instanceof Bandwidth);
        assertEquals(100, ((Bandwidth) t1).getRate());

        assertEquals("my-toxic-2", t2.getName());
        assertEquals(1.0, t2.getToxicity(), 0.01);
        assertEquals(UPSTREAM, t2.getStream());
        assertTrue(t2 instanceof Bandwidth);
        assertEquals(200, ((Bandwidth) t2).getRate());
    }

    @Test
    public void testGetToxic() throws IOException {
        toxics.bandwidth("my-toxic", DOWNSTREAM, 100);

        Toxic t = toxics.get("my-toxic");

        assertEquals("my-toxic", t.getName());
        assertEquals(1.0, t.getToxicity(), 0.01);
        assertEquals(DOWNSTREAM, t.getStream());
        assertTrue(t instanceof Bandwidth);
        assertEquals(100, ((Bandwidth) t).getRate());
    }

    @Test
    public void testRemoveToxic() throws IOException {
        Toxic t = toxics.bandwidth("my-toxic", DOWNSTREAM, 100);
        assertEquals(1, toxics.getAll().size());
        t.remove();
        assertTrue(toxics.getAll().isEmpty());
    }

    @Test
    public void testBandwidth() throws IOException {
        Bandwidth bandwidth = toxics.bandwidth("my-toxic", UPSTREAM, 100);

        assertEquals(100, bandwidth.getRate());
        bandwidth.setRate(200);
        assertEquals(200, bandwidth.getRate());
    }
/*
    @Test
    public void testLatency() throws IOException {
        Latency latency = toxics.latency();

        latency.enable().setLatency(100).setJitter(15);
        assertTrue(latency.isEnabled());
        assertEquals(100, latency.getLatency());
        assertEquals(15, latency.getJitter());

        latency.disable();
        assertFalse(latency.isEnabled());
    }

    @Test
    public void testSlicer() throws IOException {
        Slicer slicer = toxics.slicer();

        slicer.enable().setAverageSize(128).setSizeVariation(64).setDelay(100);
        assertTrue(slicer.isEnabled());
        assertEquals(128, slicer.getAverageSize());
        assertEquals(64, slicer.getSizeVariation());
        assertEquals(100, slicer.getDelay());

        slicer.disable();
        assertFalse(slicer.isEnabled());
    }

    @Test
    public void testSlowClose() throws IOException {
        SlowClose slowClose = toxics.slowClose();

        slowClose.enable().setDelay(100);
        assertTrue(slowClose.isEnabled());
        assertEquals(100, slowClose.getDelay());

        slowClose.disable();
        assertFalse(slowClose.isEnabled());
    }

    @Test
    public void testTimeout() throws IOException {
        Timeout timeout = toxics.timeout();

        timeout.enable().setTimeout(100);
        assertTrue(timeout.isEnabled());
        assertEquals(100, timeout.getTimeout());

        timeout.disable();
        assertFalse(timeout.isEnabled());
    }*/
}
