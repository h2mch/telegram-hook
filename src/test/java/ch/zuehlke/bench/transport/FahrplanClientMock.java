package ch.zuehlke.bench.transport;


import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@Alternative()
@Priority(1)
@ApplicationScoped
@RestClient
public class FahrplanClientMock implements FahrplanClient {

    String productsFilter;
    String boardType;
    int disableEquivs;
    int limit;
    int start;
    String styleSheet;
    int showOldDepature;
    String stationIdFrom;
    String stationIdDirection;

    private String response;

    @Override
    public String getConnections(String productsFilter, String boardType, int disableEquivs, int limit, int start, String styleSheet, int showOldDepature, String stationIdFrom, String stationIdDirection) {
        this.productsFilter = productsFilter;
        this.boardType = boardType;
        this.disableEquivs = disableEquivs;
        this.limit = limit;
        this.start = start;
        this.styleSheet = styleSheet;
        this.showOldDepature = showOldDepature;
        this.stationIdFrom = stationIdFrom;
        this.stationIdDirection = stationIdDirection;

        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
