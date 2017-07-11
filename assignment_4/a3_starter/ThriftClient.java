import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * A simple wrapper around the Thrift client which stores both the Transport
 * and KeyValueService.Client
 *
 * This wrapper class allows us to close the transport through the {@code closeTransport() }
 */
public class ThriftClient {
    private KeyValueService.Client _client;
    private TTransport _transport;

    public ThriftClient(KeyValueService.Client client, TTransport transport) {
        _client = client;
        _transport = transport;
    }

    public void forward(String key, String value, int sequence) throws TException {
        _client.forward(key, value, sequence);
    }

    public String get(String key) throws TException {
        return _client.get(key);
    }

    public Map<String, String> getDataDump() throws TException {
        return _client.getDataDump();
    }

    public void setMyMap(List<String> keys, List<String> values) throws TException{
        _client.setMyMap(keys, values);
    }

    public void closeTransport() {
        _transport.close();
    }
}
