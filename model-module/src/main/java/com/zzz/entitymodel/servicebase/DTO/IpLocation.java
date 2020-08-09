package com.zzz.entitymodel.servicebase.DTO;

public class IpLocation {

    private String locationName;
    private String locationHref;

    public IpLocation(String locationName, String locationHref) {
        this.locationName = locationName;
        this.locationHref = locationHref;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationHref() {
        return locationHref;
    }

    @Override
    public String toString() {
        return "[ locationName : "+locationName + " locationHref "+locationHref+" ] ";
    }
}
