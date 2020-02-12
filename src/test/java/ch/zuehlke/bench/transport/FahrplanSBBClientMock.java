package ch.zuehlke.bench.transport;

public class FahrplanSBBClientMock implements FahrplanSBBClient {

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
