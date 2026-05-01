package com.university.usermanager;

import com.university.usermanager.controller.UserController;

public class Main {
    public static void main(String[] args) {
        UserController controller = new UserController();
        controller.run();
    }
}
