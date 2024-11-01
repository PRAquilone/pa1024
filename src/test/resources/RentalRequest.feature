Feature: Tool Rental Application Additional Tests

  Scenario: Testing a call to the server to request a rental
    When the client calls /tools/rental/rent tool code LADW on checkout of 2024-10-20 for 3 days with a discount of 10 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type   | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due   | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | LADW | Ladder | Werner | 3          | 2024-10-20   | 2024-10-23 | 3          | 5.373 | 1.99        | 5.97              | 10              | 0.597          | 5.373       | ACTIVE     |

  Scenario: Testing a call to the server with wrong number of rental days
    When the client calls /tools/rental/rent tool code LADW on checkout of 2024-10-20 for 0 days with a discount of 10 percent
    Then the client receives status error code of 500
    And the client receives the following error response for Rental Day Count Invalid
      | message                               |
      | Rental day count is not 1 or greater. |

  Scenario: Testing a call to the server with wrong discount code
    When the client calls /tools/rental/rent tool code LADW on checkout of 2024-10-20 for 3 days with a discount of 101 percent
    Then the client receives status error code of 500
    And the client receives the following error response for Discount Invalid
      | message                                     |
      | Discount percent is not in the range 0-100. |

  Scenario: Testing a call to the server to request a rental when the tool is already rented out
    When the client calls /tools/rental/rent tool code JAKD on checkout of 2024-10-20 for 5 days with a discount of 10 percent
    Then the client receives status code of 200
    And the client receives the following rental agreement
      | rentalId | code | type       | brand  | rentalDays | checkOutDate | dueDate    | chargeDays | due    | dailyCharge | preDiscountCharge | discountPercent | discountAmount | finalCharge | toolStatus |
      | 1        | JAKD | JackHammer | DeWalt | 5          | 2024-10-20   | 2024-10-25 | 4          | 10.764 | 2.99        | 11.96             | 10              | 1.196          | 10.764      | ACTIVE     |
    When the client calls /tools/rental/rent tool code JAKD on checkout of 2024-10-21 for 5 days with a discount of 1 percent
    Then the client receives status error code of 500
    And the client receives the following error response for tool already rented
      | message                                                          |
      | The tool is already rented and unable to be rented at this time. |

