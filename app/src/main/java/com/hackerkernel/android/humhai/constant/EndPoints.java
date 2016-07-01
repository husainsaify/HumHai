package com.hackerkernel.android.humhai.constant;

/**
 * Class to hold all the
 */
public class EndPoints {
    private static final String SERVER_URL = "http://api.hackerkernel.com/humhai/";
    private static final String VERSION = "v1/";
    private static final String BASE_URL = SERVER_URL + VERSION;
    public static final String REGISTER = BASE_URL + "register.php",
            LOGIN = BASE_URL + "login.php",
    VERIFY_OTP = BASE_URL + "verifyOtp.php";
}