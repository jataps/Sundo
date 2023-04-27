package com.example.appsundo;

public class User implements Comparable<User> {

    private String lastName;
    private String firstName;
    private String emergencyNumber;
    private String emergencyName;
    private String contactNumber;
    private String email;

    private String referenceID;

    private String uid;
    private Address ADDRESS;

    private String accountCode;

    //Driver details;
    private Vehicle VEHICLE;

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public String getReferenceID() {return referenceID; }

    public String getUid() {
        return uid;
    }
    public String getAccountCode() {
        return accountCode;
    }
    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Address getADDRESS() {
        return ADDRESS;
    }

    public Vehicle getVEHICLE() {
        return VEHICLE;
    }

    @Override
    public int compareTo(User user) {
        return this.lastName.compareTo(user.getLastName());
    }


    public static class Vehicle {

        private String plateNumber;
        private String capacity;
        private String status;

        public String getPlateNumber() {
            return plateNumber;
        }

        public String getCapacity() {
            return capacity;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class Address {
        private String province;
        private String city;
        private String barangay;
        private String streetAddress;

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getBarangay() {
            return barangay;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

    }

}
