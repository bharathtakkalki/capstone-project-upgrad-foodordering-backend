package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UitilityProvider {

    public  Boolean isValidRestaurantName(String restaurantName)throws RestaurantNotFoundException {
        if(restaurantName == null || restaurantName ==""){
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        }
        return true;
    }

    public boolean isValidPassword(String password){
        Boolean lowerCase = false;
        Boolean upperCase = false;
        Boolean number = false;
        Boolean specialCharacter = false;

        if(password.length() < 8){
            return false;
        }

        if(password.matches("(?=.*[0-9]).*")){
            number = true;
        }

        if(password.matches("(?=.*[a-z]).*")){
            lowerCase = true;
        }
        if(password.matches("(?=.*[A-Z]).*")){
            upperCase = true;
        }
        if(password.matches("(?=.*[#@$%&*!^]).*")){
            specialCharacter = true;
        }

        if(lowerCase && upperCase){
            if(specialCharacter && number){
                return true;
            }
        }else{
            return false;
        }
        return false;
    }

    public boolean isContactValid(String contactNumber){
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher m = p.matcher(contactNumber);
        return (m.find() && m.group().equals(contactNumber));
    }

    public boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean isPincodeValid(String pincode){
        Pattern p = Pattern.compile("\\d{6}\\b");
        Matcher m = p.matcher(pincode);
        return (m.find() && m.group().equals(pincode));
    }

    public boolean isValidSignupRequest (CustomerEntity customerEntity)throws SignUpRestrictedException{
        if (customerEntity.getFirstName() == null || customerEntity.getFirstName() == ""){
             throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if(customerEntity.getPassword() == null||customerEntity.getPassword() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getEmail() == null||customerEntity.getEmail() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        if (customerEntity.getContactNumber() == null||customerEntity.getContactNumber() == ""){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        return true;
    }

    public boolean isValidAuthorizationFormat(String authorization)throws AuthenticationFailedException{
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedAuth = new String(decoded);
            String[] decodedArray = decodedAuth.split(":");
            String username = decodedArray[0];
            String password = decodedArray[1];
            return true;
        }catch (ArrayIndexOutOfBoundsException exc){
            throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
    }

    public boolean isValidUpdateCustomerRequest (String firstName)throws UpdateCustomerException {
        if (firstName == null || firstName == "") {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return true;
    }

    public boolean isValidUpdatePasswordRequest(String oldPassword,String newPassword) throws UpdateCustomerException{
        if (oldPassword == null || oldPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        if (newPassword == null || newPassword == "") {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        return true;
    }
}

