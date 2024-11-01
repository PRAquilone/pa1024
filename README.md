# Rental Tool Demo Example

### Purpose
This code was written to solely demonstrate an ability to code a REST API endpoint in Java.

### Specifications
The demonstration is to code and test a simple tool rental application.
* The application is a point-of-sale tool for a store, like Home Depot, that rents big tools.
* Customers rent a tool for a specified number of days.
* When a customer checks out a tool, a Rental Agreement is produced.
* The store charges a daily rental fee, whose amount is different for each tool type.
* Some tools are free of charge on weekends or holidays.
* Clerks may give customers a discount that is applied to the total daily charges to reduce the final
charge.

### Assumptions, Choices and Notes
This is a list of assumptions or choices made with respect to the requirments
* Assumption to create only a back end REST API since no UI was required .
* Decision to use an in memory H2 Database which if was a real application would be replaced with a hosted DB.  However it will be coded so that the connector implementation could simply be replaced with another database connection and not interrupt the rest of the code.
* Decision to have all important static DB data (data not changing outside of initial data load) be pre-loaded when the first request comes in.  This does sacrifice memory for faster performance.  If this was a real POS this would have to be replaced with appropriate data calls to load the data that is specifically needed.  For example, the ToolsCharges table would only load the row for the tool to be rented whereas here the entire table is loaded. 
* Decision to use gradle to build the project because I find it more flexible and readable than maven
* Decision to create custom one time use exceptions for the errors we are throwing.
* Decision to wrap the Rental Agreement (object returned) in a Rental Response that includes httpstatus and a message.  This will allow for use of the same rental object when sending an error that has no rental agreement.
* Decision proof of working tests from requirements below are in the ToolRentalProofTest.feature cucumber test file

### Holidays
These are the only holidays that are used.
* Independence Day, July 4th - If falls on weekend, it is observed on the closest weekday (if Sat, then Friday before, if Sunday, then Monday after)
* Labor Day - First Monday in September

### Request Data
This is the list of request data expected for rental
* Tool code - See tool table above
* Rental day count - The number of days for which the customer wants to rent the tool. (e.g. 4
days)
* Discount percent - As a whole number, 0-100 (e.g. 20 = 20%)
* Check out date

Sample Request:
```declarative
{
    "code": "LADW",
    "rentalDayCount" : 3,
    "discount": 10,
    "checkOutDate": "2024-10-20"
}
```

### Response - Rental Agreement
If the tool can be rented then a rental agreement object is created and returned.  The rental agreement contains the following information:
* Tool code - Specified at checkout
* Tool type - From tool info
* Tool brand - From tool info
* Rental days - Specified at checkout
* Check out date - Specified at checkout
* Due date - Calculated from checkout date and rental days.
* Daily rental charge - Amount per day, specified by the tool type.
* Charge days - Count of chargeable days, from day after checkout through and including due
date, excluding “no charge” days as specified by the tool type.
* Pre-discount charge - Calculated as charge days X daily charge. Resulting total rounded half up
to cents.
* Discount percent - Specified at checkout.
* Discount amount - calculated from discount % and pre-discount charge. Resulting amount
rounded half up to cents.
* Final charge - Calculated as pre-discount charge - discount amount.

Sample Response (for the sample request above)
```declarative
{
    "agreement": {
        "rentalId": 1,
        "code": "LADW",
        "type": "Ladder",
        "brand": "Werner",
        "rentalDays": 3,
        "checkOutDate": "2024-10-20",
        "dueDate": "2024-10-23",
        "chargeDays": 3,
        "due": 5.373,
        "dailyCharge": 1.99,
        "preDiscountCharge": 5.97,
        "discountPercent": 10,
        "discountAmount": 0.597,
        "finalCharge": 5.373,
        "toolStatus": "ACTIVE"
    },
    "message": "SUCCESS",
    "status": "OK"
}
```

### Required Exceptions
Below is the list of required exceptions
* Rental day count is not 1 or greater
* Discount percent is not in the range 0-100

### Proof Test Cases
As stated these tests are in the ToolRentalProofTests.feature cucumber test file

| Request Fields | Test 1 | Test 2 | Test 3 | Test 4 | Test 5 | Test 6 |
| ---------------| ------ | ------ | ------ | ------ | ------ | ------ |
| Tool Code | JAKR | LADW | CHNS | JAKD | JAKR | JAKR |
| Check Out Date | 9/3/15 | 7/2/20| 7/2/15 | 9/3/15 | 7/2/15 | 7/2/20 |
| Rental Days | 5 | 3 | 5 | 6| 9 | 4 |
| Discount | 101% | 10% | 25% | 0% | 0% | 50% |

Expected results are in the feature file