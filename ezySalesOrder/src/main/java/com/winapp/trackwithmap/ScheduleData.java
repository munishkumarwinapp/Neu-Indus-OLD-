package com.winapp.trackwithmap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 22-May-17.
 */

public class ScheduleData implements Parcelable{
    private boolean isChecked;
    String companyCode, department_SlNo, bookingNo, bookingDate, pickupDate, isHospitalBooking, contact_SlNo, customerName,
            customerICNo,
            patientName,
            patientDOB,
            patientICNo,
            contactPerson,
            contactNo,
            pickupUnitNo,
            pickupAddress1,
            pickupAddress2,
            pickupAddress3,
            pickupPostalCode,
            designationUnitNo,
            designationAddress1,
            designationAddress2,
            designationAddress3,
            designationPostalCode,
            wardNo,
            bedNo,
            haveLift,
            carryAssistance_SlNo,
            remarks,
            amount,
            discount,
            subTotal,
            tax,
            netTotal,
            status,
            pickupDateTime,
            paidAmount,
            creditAmount,
            balanceAmount,
            employee_SlNo,
            vehicle_SlNo,
            bookingRequestNo,
            bookedTime,
            pickupTime,
            driver_SlNo,
           dateOfPayment,
           registerNumber;

    public ScheduleData() {
    }
   /* public ScheduleData(String bookingNo,String status, String pickUpDate,String PickUpTime,String patientName, String pickUpAddress,String pickUpPostalCode) {
        this.bookingNo = bookingNo;
        this.status = status;
        this.pickUpDate = pickUpDate;
        this.PickUpTime = PickUpTime;
        this.patientName = patientName;
        this.pickUpAddress = pickUpAddress;
        this.pickUpPostalCode = pickUpPostalCode;
    }*/

    protected ScheduleData(Parcel in) {

        companyCode = in.readString(); department_SlNo = in.readString(); bookingNo= in.readString();
        bookingDate= in.readString();
        pickupDate= in.readString();
        isHospitalBooking = in.readString();
        contact_SlNo = in.readString();
        customerName = in.readString();
                customerICNo = in.readString();
                patientName = in.readString();
                patientICNo = in.readString();
        patientDOB   = in.readString();
                contactPerson= in.readString();
                contactNo = in.readString();
                pickupUnitNo = in.readString();
                pickupAddress1 = in.readString();
                pickupAddress2 = in.readString();
                pickupAddress3 = in.readString();
                pickupPostalCode = in.readString();
                designationUnitNo = in.readString();
                designationAddress1 = in.readString();
                designationAddress2 = in.readString();
                designationAddress3 = in.readString();
                designationPostalCode = in.readString();
                wardNo = in.readString();
                bedNo = in.readString();
                haveLift = in.readString();
                carryAssistance_SlNo = in.readString();
                remarks = in.readString();
                amount = in.readString();
                discount = in.readString();
                subTotal = in.readString();
                tax = in.readString();
                netTotal = in.readString();
                status = in.readString();
                pickupDateTime = in.readString();
                paidAmount = in.readString();
                creditAmount = in.readString();
                balanceAmount = in.readString();
                employee_SlNo = in.readString();
                vehicle_SlNo = in.readString();
                bookingRequestNo = in.readString();
                bookedTime = in.readString();
                pickupTime = in.readString();
                driver_SlNo = in.readString();
               dateOfPayment = in.readString();
               registerNumber = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(companyCode);
        dest.writeString(department_SlNo);
        dest.writeString(bookingNo);
        dest.writeString( bookingDate);
        dest.writeString(pickupDate);
        dest.writeString(isHospitalBooking);
        dest.writeString( contact_SlNo);
        dest.writeString(customerName);
        dest.writeString( customerICNo );
        dest.writeString( patientName);
        dest.writeString( patientDOB);

        dest.writeString(patientICNo);
        dest.writeString(contactPerson);
        dest.writeString( contactNo );
        dest.writeString(pickupUnitNo);
        dest.writeString( pickupAddress1);
        dest.writeString(pickupAddress2);
        dest.writeString(pickupAddress3);
        dest.writeString( pickupPostalCode);
        dest.writeString( designationUnitNo);
        dest.writeString( designationAddress1);
        dest.writeString( designationAddress2);
        dest.writeString(designationAddress3);
        dest.writeString( designationPostalCode);
        dest.writeString( wardNo );
        dest.writeString( bedNo);
        dest.writeString( haveLift);
        dest.writeString( carryAssistance_SlNo);
        dest.writeString(remarks);
        dest.writeString(amount );
        dest.writeString( discount);
        dest.writeString(subTotal);
        dest.writeString(tax);
        dest.writeString(netTotal);
        dest.writeString(status);
        dest.writeString(pickupDateTime);
        dest.writeString( paidAmount);
        dest.writeString( creditAmount);
        dest.writeString(balanceAmount);
        dest.writeString( employee_SlNo);
        dest.writeString( vehicle_SlNo);
        dest.writeString(bookingRequestNo);
        dest.writeString( bookedTime );
        dest.writeString( pickupTime);
        dest.writeString( driver_SlNo);
        dest.writeString( dateOfPayment);
        dest.writeString( registerNumber);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScheduleData> CREATOR = new Creator<ScheduleData>() {
        @Override
        public ScheduleData createFromParcel(Parcel in) {
            return new ScheduleData(in);
        }

        @Override
        public ScheduleData[] newArray(int size) {
            return new ScheduleData[size];
        }
    };

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public String getPatientDOB() {
        return patientDOB;
    }

    public void setPatientDOB(String patientDOB) {
        this.patientDOB = patientDOB;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getDepartment_SlNo() {
        return department_SlNo;
    }

    public void setDepartment_SlNo(String department_SlNo) {
        this.department_SlNo = department_SlNo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getIsHospitalBooking() {
        return isHospitalBooking;
    }

    public void setIsHospitalBooking(String isHospitalBooking) {
        this.isHospitalBooking = isHospitalBooking;
    }

    public String getContact_SlNo() {
        return contact_SlNo;
    }

    public void setContact_SlNo(String contact_SlNo) {
        this.contact_SlNo = contact_SlNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerICNo() {
        return customerICNo;
    }

    public void setCustomerICNo(String customerICNo) {
        this.customerICNo = customerICNo;
    }

    public String getPatientICNo() {
        return patientICNo;
    }

    public void setPatientICNo(String patientICNo) {
        this.patientICNo = patientICNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getPickupUnitNo() {
        return pickupUnitNo;
    }

    public void setPickupUnitNo(String pickupUnitNo) {
        this.pickupUnitNo = pickupUnitNo;
    }

    public String getPickupAddress1() {
        return pickupAddress1;
    }

    public void setPickupAddress1(String pickupAddress1) {
        this.pickupAddress1 = pickupAddress1;
    }

    public String getPickupAddress2() {
        return pickupAddress2;
    }

    public void setPickupAddress2(String pickupAddress2) {
        this.pickupAddress2 = pickupAddress2;
    }

    public String getPickupAddress3() {
        return pickupAddress3;
    }

    public void setPickupAddress3(String pickupAddress3) {
        this.pickupAddress3 = pickupAddress3;
    }

    public String getPickupPostalCode() {
        return pickupPostalCode;
    }

    public void setPickupPostalCode(String pickupPostalCode) {
        this.pickupPostalCode = pickupPostalCode;
    }

    public String getDesignationUnitNo() {
        return designationUnitNo;
    }

    public void setDesignationUnitNo(String designationUnitNo) {
        this.designationUnitNo = designationUnitNo;
    }

    public String getDesignationAddress1() {
        return designationAddress1;
    }

    public void setDesignationAddress1(String designationAddress1) {
        this.designationAddress1 = designationAddress1;
    }

    public String getDesignationAddress2() {
        return designationAddress2;
    }

    public void setDesignationAddress2(String designationAddress2) {
        this.designationAddress2 = designationAddress2;
    }

    public String getDesignationAddress3() {
        return designationAddress3;
    }

    public void setDesignationAddress3(String designationAddress3) {
        this.designationAddress3 = designationAddress3;
    }

    public String getDesignationPostalCode() {
        return designationPostalCode;
    }

    public void setDesignationPostalCode(String designationPostalCode) {
        this.designationPostalCode = designationPostalCode;
    }

    public String getWardNo() {
        return wardNo;
    }

    public void setWardNo(String wardNo) {
        this.wardNo = wardNo;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public String getHaveLift() {
        return haveLift;
    }

    public void setHaveLift(String haveLift) {
        this.haveLift = haveLift;
    }

    public String getCarryAssistance_SlNo() {
        return carryAssistance_SlNo;
    }

    public void setCarryAssistance_SlNo(String carryAssistance_SlNo) {
        this.carryAssistance_SlNo = carryAssistance_SlNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(String netTotal) {
        this.netTotal = netTotal;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getEmployee_SlNo() {
        return employee_SlNo;
    }

    public void setEmployee_SlNo(String employee_SlNo) {
        this.employee_SlNo = employee_SlNo;
    }

    public String getVehicle_SlNo() {
        return vehicle_SlNo;
    }

    public void setVehicle_SlNo(String vehicle_SlNo) {
        this.vehicle_SlNo = vehicle_SlNo;
    }

    public String getBookingRequestNo() {
        return bookingRequestNo;
    }

    public void setBookingRequestNo(String bookingRequestNo) {
        this.bookingRequestNo = bookingRequestNo;
    }

    public String getBookedTime() {
        return bookedTime;
    }

    public void setBookedTime(String bookedTime) {
        this.bookedTime = bookedTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDriver_SlNo() {
        return driver_SlNo;
    }

    public void setDriver_SlNo(String driver_SlNo) {
        this.driver_SlNo = driver_SlNo;
    }

    public String getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(String dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
