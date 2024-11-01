Feature: Tool Rental Application Proof Tests

  Scenario: TEST1
    When the client calls /tools/rental/rent tool code JAKR on checkout of 2015-9-3 for 5 days with a discount of 101 percent
    Then the client receives status error code of 500
    And the client receives the following error response for Discount Invalid
      | message                                     |
      | Discount percent is not in the range 0-100. |

  Scenario: TEST2
    When the client calls /tools/rental/rent tool code LADW on checkout of 2020-07-02 for 3 days with a discount of 10 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type   | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due   | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | LADW | Ladder | Werner | 3          | 2020-07-02   | 2020-07-05 | 2          | 3.582 | 1.99        | 3.98              | 10              | 0.398          | 3.582       | ACTIVE     |

  Scenario: TEST3
    When the client calls /tools/rental/rent tool code CHNS on checkout of 2015-07-02 for 5 days with a discount of 25 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type     | brand | rentalDays | checkOutDate | dueDate    | chargeDays | due    | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | CHNS | ChainSaw | Stihl | 5          | 2015-07-02   | 2015-07-07 | 4          | 4.4700 | 1.49        | 5.96              | 25              | 1.4900         | 4.47        | ACTIVE     |

  Scenario: TEST4
    When the client calls /tools/rental/rent tool code JAKD on checkout of 2015-9-03 for 6 days with a discount of 0 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type       | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due  | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | JAKD | JackHammer | DeWalt | 6          | 2015-09-03   | 2015-09-09 | 3          | 8.97 | 2.99        | 8.97              | 0               | 0              | 8.97        | ACTIVE     |

  Scenario: TEST5
    When the client calls /tools/rental/rent tool code JAKR on checkout of 2015-07-02 for 9 days with a discount of 0 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type       | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due   | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | JAKR | JackHammer | Ridgid | 9          | 2015-07-02   | 2015-07-11 | 7          | 20.93 | 2.99        | 20.93             | 0               | 0              | 20.93       | ACTIVE     |

  Scenario: TEST6
    When the client calls /tools/rental/rent tool code JAKR on checkout of 2020-07-02 for 4 days with a discount of 50 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type       | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due  | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | JAKR | JackHammer | Ridgid | 4          | 2020-07-02   | 2020-07-06 | 2          | 2.99 | 2.99        | 5.98              | 50              | 2.990          | 2.990       | ACTIVE     |
