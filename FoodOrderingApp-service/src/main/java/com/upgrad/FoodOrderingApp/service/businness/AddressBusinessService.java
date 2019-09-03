package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class AddressBusinessService {
    @Autowired
    AddressDao addressDao;

    @Autowired
    CustomerAuthDao customerAuthDao;

    @Autowired
    UitilityProvider uitilityProvider;

    @Autowired
    StateDao stateDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(String accessToken,AddressEntity addressEntity,String stateUuid)throws AuthorizationFailedException,SaveAddressException,AddressNotFoundException{
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        if (addressEntity.getCity() == null || addressEntity.getFlatBuilNumber() == null || addressEntity.getPincode() == null || addressEntity.getLocality() == null){
            throw new SaveAddressException("SAR-001","No field can be empty");
        }
        if(!uitilityProvider.isPincodeValid(addressEntity.getPincode())){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);

        if(stateEntity == null){
            throw new AddressNotFoundException("ANF-002","No state by this id");
        }

        addressEntity.setStateName(stateEntity);

        AddressEntity savedAddress = addressDao.saveAddress(addressEntity);

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
            customerAddressEntity.setCustomer(customerAuthEntity.getCustomer());
            customerAddressEntity.setAddress(savedAddress);
            CustomerAddressEntity createdCustomerAddressEntity = customerAddressDao.saveCustomerAddress(customerAddressEntity);
        return savedAddress;

    }

    public List<AddressEntity> getAllSavedAddress(String accessToken)throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByAccessToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        List<AddressEntity> addressEntities = new LinkedList<>();
        List<CustomerAddressEntity> customerAddressEntities  = customerAddressDao.getAllCustomerAddressByCustomer(customerAuthEntity.getCustomer());
        if(customerAddressEntities != null) {
            customerAddressEntities.forEach(customerAddressEntity -> {
                addressEntities.add(customerAddressEntity.getAddress());
            });
        }

        return addressEntities;

    }

}
