package com.mad.contactsbuddy.Models;

public class Contact {
    String id, name, phone_no, email, image, created_on, last_updated_on;

    public Contact(String id, String name, String phone_no, String email, String image, String created_on, String last_updated_on) {
        this.id = id;
        this.name = name;
        this.phone_no = phone_no;
        this.email = email;
        this.image = image;
        this.created_on = created_on;
        this.last_updated_on = last_updated_on;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getLast_updated_on() {
        return last_updated_on;
    }

    public void setLast_updated_on(String last_updated_on) {
        this.last_updated_on = last_updated_on;
    }
}
