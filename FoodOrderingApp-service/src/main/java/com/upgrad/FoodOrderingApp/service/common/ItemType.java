package com.upgrad.FoodOrderingApp.service.common;



public enum ItemType {

    VEG("VEG"),

    NON_VEG("NON_VEG");


    private String value;

    ItemType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }


    public static ItemType fromValue(String text) {
        for (ItemType b : ItemType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}


//public enum  ItemType {
//    VEG(0),
//    NON_VEG(1);
//
//    private  int value;
//
//    ItemType(int i) {
//        this.value = i;
//    }
//
//    public String toSting(){
//        return String.valueOf(value);
//    }
//}