package org.gots.authentication;

public class User {
    // {"picture":"https:\/\/lh4.googleusercontent.com\/-C23of948LvY\/AAAAAAAAAAI\/AAAAAAAAF1c\/TUU9HK73SHk\/photo.jpg",
    // "id":"107037097182917553549","locale":"fr","link":"https:\/\/plus.google.com\/+SebastienFleury","name":"Sebastien
    // Fleury","gender":"male","family_name":"Fleury","given_name":"Sebastien"}
    private String pictureURL;

    private String id;

    private String locale;

    private String name;

    private String family_name;

    private String given_name;

    private String gender;

    private String email;

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
