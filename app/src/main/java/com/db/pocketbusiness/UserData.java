package com.db.pocketbusiness;

public class UserData {

    //Initialisation of user data
    public String name, email, password, phone;
    public boolean flag;

    public UserData(){}

    public UserData(String name, String email, String password, String phone , boolean flag){
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.flag = flag;
        System.out.println(this.name);
        System.out.println(this.email);
        System.out.println(this.password);
        System.out.println(this.phone);
    }
}
