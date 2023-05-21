package com.example.appsundo;

public class History {

    private String date;
    private String driverUID;
    private String studentUID;

    private String driverFName;
    private String driverLName;
    private String driverCNum;
    private ToHome.PickUp homePickup;
    private ToHome.DropOff homeDropOff;
    private ToSchool.PickUp schoolPickup;
    private ToSchool.DropOff schoolDropOff;



    public String getDriverFName() {
        return driverFName;
    }

    public void setDriverFName(String driverFName) {
        this.driverFName = driverFName;
    }

    public String getDriverLName() {
        return driverLName;
    }

    public void setDriverLName(String driverLName) {
        this.driverLName = driverLName;
    }

    public String getDriverCNum() {
        return driverCNum;
    }

    public void setDriverCNum(String driverCNum) {
        this.driverCNum = driverCNum;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDriverUID(String driverUID) {
        this.driverUID = driverUID;
    }

    public void setStudentUID(String studentUID) {
        this.studentUID = studentUID;
    }

    public void setHomePickup(ToHome.PickUp homePickup) {
        this.homePickup = homePickup;
    }

    public void setHomeDropOff(ToHome.DropOff homeDropOff) {
        this.homeDropOff = homeDropOff;
    }

    public void setSchoolPickup(ToSchool.PickUp schoolPickup) {
        this.schoolPickup = schoolPickup;
    }

    public void setSchoolDropOff(ToSchool.DropOff schoolDropOff) {
        this.schoolDropOff = schoolDropOff;
    }

    public String getDate() {
        return date;
    }

    public String getDriverUID() {
        return driverUID;
    }

    public String getStudentUID() {
        return studentUID;
    }

    public ToHome.PickUp getHomePickup() {
        return homePickup;
    }

    public ToHome.DropOff getHomeDropOff() {
        return homeDropOff;
    }

    public ToSchool.PickUp getSchoolPickup() {
        return schoolPickup;
    }

    public ToSchool.DropOff getSchoolDropOff() {
        return schoolDropOff;
    }

    public static class ToHome {

        public static class PickUp {

            private String pickupTime;
            private String pickupLatitude;
            private String pickupLongitude;

            public void setPickupTime(String pickupTime) {
                this.pickupTime = pickupTime;
            }

            public void setPickupLatitude(String pickupLatitude) {
                this.pickupLatitude = pickupLatitude;
            }

            public void setPickupLongitude(String pickupLongitude) {
                this.pickupLongitude = pickupLongitude;
            }

            public String getPickupTime() {
                return pickupTime;
            }

            public String getPickupLatitude() {
                return pickupLatitude;
            }

            public String getPickupLongitude() {
                return pickupLongitude;
            }

        }

        public static class DropOff {

            private String dropoffTime;
            private String dropoffLatitude;
            private String dropoffLongitude;

            public void setDropoffTime(String dropoffTime) {
                this.dropoffTime = dropoffTime;
            }

            public void setDropoffLatitude(String dropoffLatitude) {
                this.dropoffLatitude = dropoffLatitude;
            }

            public void setDropoffLongitude(String dropoffLongitude) {
                this.dropoffLongitude = dropoffLongitude;
            }

            public String getDropoffTime() {
                return dropoffTime;
            }

            public String getDropoffLatitude() {
                return dropoffLatitude;
            }

            public String getDropoffLongitude() {
                return dropoffLongitude;
            }
            
        }
    }

    public static class ToSchool {

        public static class PickUp {

            private String pickupTime;
            private String pickupLatitude;
            private String pickupLongitude;


            public void setPickupTime(String pickupTime) {
                this.pickupTime = pickupTime;
            }

            public void setPickupLatitude(String pickupLatitude) {
                this.pickupLatitude = pickupLatitude;
            }

            public void setPickupLongitude(String pickupLongitude) {
                this.pickupLongitude = pickupLongitude;
            }

            public String getPickupTime() {
                return pickupTime;
            }

            public String getPickupLatitude() {
                return pickupLatitude;
            }

            public String getPickupLongitude() {
                return pickupLongitude;
            }

        }

        public static class DropOff {

            private String dropoffTime;
            private String dropoffLatitude;
            private String dropoffLongitude;

            public void setDropoffTime(String dropoffTime) {
                this.dropoffTime = dropoffTime;
            }

            public void setDropoffLatitude(String dropoffLatitude) {
                this.dropoffLatitude = dropoffLatitude;
            }

            public void setDropoffLongitude(String dropoffLongitude) {
                this.dropoffLongitude = dropoffLongitude;
            }

            public String getDropoffTime() {
                return dropoffTime;
            }

            public String getDropoffLatitude() {
                return dropoffLatitude;
            }

            public String getDropoffLongitude() {
                return dropoffLongitude;
            }

        }
    }

}
