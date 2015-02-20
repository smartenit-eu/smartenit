package eu.smartenit.enduser.app.energyreport;

public class Measurements
{
    private long timestamp;

    private String totalcpucycle;

    private interfaces[] interfaces;

    private String networktraffic;

    private String usercpucycle;

    public long getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTotalcpucycle ()
    {
        return totalcpucycle;
    }

    public void setTotalcpucycle (String totalcpucycle)
    {
        this.totalcpucycle = totalcpucycle;
    }

    public interfaces[] getInterfaces ()
    {
        return interfaces;
    }

    public void setInterfaces (interfaces[] interfaces)
    {
        this.interfaces = interfaces;
    }

    public String getNetworktraffic ()
    {
        return networktraffic;
    }

    public void setNetworktraffic (String networktraffic)
    {
        this.networktraffic = networktraffic;
    }

    public String getUsercpucycle ()
    {
        return usercpucycle;
    }

    public void setUsercpucycle (String usercpucycle)
    {
        this.usercpucycle = usercpucycle;
    }
}
