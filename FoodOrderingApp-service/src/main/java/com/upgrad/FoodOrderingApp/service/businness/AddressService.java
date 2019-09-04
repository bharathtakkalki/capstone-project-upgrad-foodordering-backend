package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
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
public class AddressService {
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
    public AddressEntity saveAddress(AddressEntity addressEntity,StateEntity stateEntity)throws SaveAddressException{

        if (addressEntity.getCity() == null || addressEntity.getFlatBuilNo() == null || addressEntity.getPincode() == null || addressEntity.getLocality() == null){
            throw new SaveAddressException("SAR-001","No field can be empty");
        }
        if(!uitilityProvider.isPincodeValid(addressEntity.getPincode())){
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        addressEntity.setState(stateEntity);

        AddressEntity savedAddress = addressDao.saveAddress(addressEntity);


        return savedAddress;

    }

    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {

        List<AddressEntity> addressEntities = new LinkedList<>();
        List<CustomerAddressEntity> customerAddressEntities  = customerAddressDao.getAllCustomerAddressByCustomer(customerEntity);
        if(customerAddressEntities != null) {
            customerAddressEntities.forEach(customerAddressEntity -> {
                addressEntities.add(customerAddressEntity.getAddress());
            });
        }

        return addressEntities;

    }

    public StateEntity getStateByUUID (String uuid)throws AddressNotFoundException{
        StateEntity stateEntity = stateDao.getStateByUuid(uuid);
        if(stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return  stateEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddressEntity(CustomerEntity customerEntity,AddressEntity addressEntity){
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customerEntity);
        customerAddressEntity.setAddress(addressEntity);
        CustomerAddressEntity createdCustomerAddressEntity = customerAddressDao.saveCustomerAddress(customerAddressEntity);
        return createdCustomerAddressEntity;

    }

    public AddressEntity getAddressByUUID(String addressUuid,CustomerEntity customerEntity)throws AuthorizationFailedException,AddressNotFoundException{
        if(addressUuid == null){
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        }
        AddressEntity addressEntity = addressDao.getAddressByUuid(addressUuid);
        if (addressEntity == null){
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }

        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByAddress(addressEntity);

        if(customerAddressEntity.getCustomer().getUuid() == customerEntity.getUuid()){
            return addressEntity;
        }else{
            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        AddressEntity deletedAddressEntity = addressDao.deleteAddress(addressEntity);
        return deletedAddressEntity;
    }
}
