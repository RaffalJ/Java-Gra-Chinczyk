package tests;


import org.junit.runner.RunWith;
import sample.Server;

import java.net.Socket;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ruffy on 29.01.2017.
 */
public class ServerTest {
    Socket socket=null;
    Server server=null;
    @org.junit.Before
    public void setUp() throws Exception {
        socket=null;
        server = new Server(1,socket);
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void Test_getRandom() throws Exception {
        int min =0;
        int max=9;
        int a;
        a=server.getRandom();
        assertTrue(min <= a && a <= max);
    }

    @org.junit.Test
    public void Test_getRandomRepeat() throws Exception {
        for (int i = 0; i < 10; i++) {
            Test_getRandom();
        }
    }

    @org.junit.Test
    public void Test_parseXML() throws Exception {
        server.parseXML();
        int a=9992;
        assert(a==server.getPort());
    }
}