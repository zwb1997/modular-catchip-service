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

    @Override
    public int hashCode() {
        int code = 0;
        char[] chars = locationHref.toCharArray();
        for(char c : chars){
            code += c;
        }
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IpLocation)){
            return false;
        }else{
            return this.locationHref.equals(((IpLocation) obj).getLocationHref());
        }
    }
}
