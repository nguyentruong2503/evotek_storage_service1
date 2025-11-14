package com.example.iam2.builder;

import java.util.Date;

public class UserExportBuilder {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Date birthday;
    private String street;
    private String ward;
    private String district;
    private String province;
    private Integer yearsOfEx;
    private Boolean locked;
    private Boolean deleted;

    private UserExportBuilder(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phone = builder.phone;
        this.birthday = builder.birthday;
        this.street = builder.street;
        this.ward = builder.ward;
        this.district = builder.district;
        this.province = builder.province;
        this.yearsOfEx = builder.yearsOfEx;
        this.locked = builder.locked;
        this.deleted = builder.deleted;
    }

    // Getter methods
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public Date getBirthday() { return birthday; }
    public String getStreet() { return street; }
    public String getWard() { return ward; }
    public String getDistrict() { return district; }
    public String getProvince() { return province; }
    public Integer getYearsOfEx() { return yearsOfEx; }
    public Boolean getLocked() { return locked; }
    public Boolean getDeleted() { return deleted; }

    // Inner static Builder class
    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private Date birthday;
        private String street;
        private String ward;
        private String district;
        private String province;
        private Integer yearsOfEx;
        private Boolean locked;
        private Boolean deleted;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setBirthday(Date birthday) {
            this.birthday = birthday;
            return this;
        }

        public Builder setStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder setWard(String ward) {
            this.ward = ward;
            return this;
        }

        public Builder setDistrict(String district) {
            this.district = district;
            return this;
        }

        public Builder setProvince(String province) {
            this.province = province;
            return this;
        }

        public Builder setYearsOfEx(Integer yearsOfEx) {
            this.yearsOfEx = yearsOfEx;
            return this;
        }

        public Builder setLocked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder setDeleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public UserExportBuilder build() {
            return new UserExportBuilder(this);
        }
    }
}
