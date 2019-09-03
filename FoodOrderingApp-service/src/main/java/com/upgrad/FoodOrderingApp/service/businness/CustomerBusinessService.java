package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    UitilityProvider uitilityProvider;

    @Autowired
    CustomerAuthDao customerAuthDao;



    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signUpCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity existingCustomerEntity = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        if (existingCustomerEntity != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number");
        }
        if (customerEntity.getContactNumber() == null || customerEntity.getEmail() == null || customerEntity.getFirstName() == null || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        if(!uitilityProvider.isEmailValid(customerEntity.getEmail())){
            throw new SignUpRestrictedException("SGR-002","Invalid email-id format!");
        }

        if(!uitilityProvider.isContactValid(customerEntity.getContactNumber())){
            throw new SignUpRestrictedException("SGR-003","Invalid contact number!");
        }

        if(!uitilityProvider.isValidPassword(customerEntity.getPassword())){
            throw new SignUpRestrictedException("SGR-004","Weak password!");
        }

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);
        CustomerEntity createdCustomerEntity = customerDao.createCustomer(customerEntity);

        return createdCustomerEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticateCustomer(String contactNumber,String password)throws AuthenticationFailedException{
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if(customerEntity == null){
            throw new AuthenticationFailedException("ATH-001","This contact number has not been registered!");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password,customerEntity.getSalt());
        if(encryptedPassword.equals(customerEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(),now,expiresAt));
            customerAuthEntity.setLoginAt(now);
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setUuid(UUID.randomUUID().toString());

            CustomerAuthEntity createdCustomerAuthEntity = customerAuthDao.createCustomerAuth(customerAuthEntity);
            return createdCustomerAuthEntity;
        }else {
            throw new AuthenticationFailedException("ATH-002","Invalid Credentials");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity customerLogout(String accessToken)throws AuthenticationFailedException{
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if(customerAuthEntity == null){
            throw new AuthenticationFailedException("ATHR-001","Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null){
            throw new AuthenticationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt().compareTo(now) < 0){
            throw new AuthenticationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }

        customerAuthEntity.setLogoutAt(ZonedDateTime.now());

        CustomerAuthEntity upatedCustomerAuthEntity = customerAuthDao.customerLogout(customerAuthEntity);
        return upatedCustomerAuthEntity;
    }


    public CustomerEntity getCustomerDetailsByUuid(String uuid){
        CustomerEntity customerEntity = customerDao.getCustomerByUuid(uuid);
        return customerEntity;
    }
}
