Feature: Rental Request

  Scenario: Testing a call to the server to request a rental
    When the client calls /tools/rental/rent tool code LADW on checkout of 2024-10-20 for 3 days with a discount of 10 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type   | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due   | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | LADW | Ladder | Werner | 3          | 2024-10-20   | 2024-10-23 | 3          | 5.373 | 1.99        | 5.97              | 10              | 0.597          | 5.373       | ACTIVE     |