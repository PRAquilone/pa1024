package com.toolsrus.rentals.component;

import com.toolsrus.rentals.db.models.Days;
import com.toolsrus.rentals.db.models.Frequency;
import com.toolsrus.rentals.db.models.Holiday;
import com.toolsrus.rentals.db.models.ToolStatus;
import com.toolsrus.rentals.db.models.ToolType;
import com.toolsrus.rentals.db.models.Tools;
import com.toolsrus.rentals.db.models.ToolsCharges;
import com.toolsrus.rentals.db.models.Vendors;
import com.toolsrus.rentals.db.repository.DaysRepository;
import com.toolsrus.rentals.db.repository.FrequencyRepository;
import com.toolsrus.rentals.db.repository.HolidayRepository;
import com.toolsrus.rentals.db.repository.ToolStatusRepository;
import com.toolsrus.rentals.db.repository.ToolTypeRepository;
import com.toolsrus.rentals.db.repository.ToolsChargesRepository;
import com.toolsrus.rentals.db.repository.ToolsRepository;
import com.toolsrus.rentals.db.repository.VendorRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class is for pre-loading set database data for use in renting
 * This assumes that you can not add to the holidays, tools, types, vendors, etc via the API
 * This is also sacrificing memory for performance not having to call the database each time for these fields before allowing a rental
 */
@Data
@Component
public class ToolRentalData {

    private final DaysRepository daysRepository;
    private final FrequencyRepository frequencyRepository;
    private final HolidayRepository holidayRepository;
    private final ToolsRepository toolsRepository;
    private final ToolsChargesRepository toolsChargesRepository;
    private final ToolStatusRepository toolStatusRepository;
    private final ToolTypeRepository toolTypeRepository;
    private final VendorRepository vendorRepository;

    private List<Days> days;
    private List<Frequency> frequencies;
    private List<Holiday> holidays;
    private List<Tools> tools;
    private List<ToolsCharges> toolsCharges;
    private List<ToolStatus> statuses;
    private List<ToolType> types;
    private List<Vendors> vendors;

    public ToolRentalData(DaysRepository daysRepository,
                          FrequencyRepository frequencyRepository,
                          HolidayRepository holidayRepository,
                          ToolsRepository toolsRepository,
                          ToolsChargesRepository toolsChargesRepository,
                          ToolStatusRepository toolStatusRepository,
                          ToolTypeRepository toolTypeRepository,
                          VendorRepository vendorRepository) {
        this.daysRepository = daysRepository;
        this.frequencyRepository = frequencyRepository;
        this.holidayRepository = holidayRepository;
        this.toolsRepository = toolsRepository;
        this.toolsChargesRepository = toolsChargesRepository;
        this.toolStatusRepository = toolStatusRepository;
        this.toolTypeRepository = toolTypeRepository;
        this.vendorRepository = vendorRepository;
        populateDataFields();
    }

    /**
     * Populate the data fields
     * Note: Must be done after databases are connected
     */
    private void populateDataFields() {
        days = daysRepository.findAll();
        frequencies = frequencyRepository.findAll();
        holidays = holidayRepository.findAll();
        tools = toolsRepository.findAll();
        toolsCharges = toolsChargesRepository.findAll();
        statuses = toolStatusRepository.findAll();
        types = toolTypeRepository.findAll();
        vendors = vendorRepository.findAll();
    }


}
