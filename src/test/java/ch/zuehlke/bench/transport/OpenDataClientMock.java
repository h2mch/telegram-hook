package ch.zuehlke.bench.transport;

import javax.json.JsonObject;

public class OpenDataClientMock implements OpenDataClient {
    String from;
    String to;
    int direct;
    int limit;

    @Override
    public JsonObject getConnections(String from, String to, int direct, int limit) {
        this.from = from;
        this.to = to;
        this.direct = direct;
        this.limit = limit;

        return null;
    }
}
