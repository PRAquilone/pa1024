package com.toolsrus.rentals.component;

import com.toolsrus.rentals.db.models.Holiday;
import com.toolsrus.rentals.db.models.HolidayDaysOfWeek;
import com.toolsrus.rentals.db.models.HolidayFrequency;
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
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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

    private List<HolidayDaysOfWeek> days;
    private List<HolidayFrequency> frequencies;
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
    }

    /**
     * Populate the data fields
     * Note: Must be done after databases are connected
     */
    public void populateDataFields() {
        if (CollectionUtils.isEmpty(days) ||
                CollectionUtils.isEmpty(frequencies) ||
                CollectionUtils.isEmpty(holidays) ||
                CollectionUtils.isEmpty(tools) ||
                CollectionUtils.isEmpty(toolsCharges) ||
                CollectionUtils.isEmpty(statuses) ||
                CollectionUtils.isEmpty(types) ||
                CollectionUtils.isEmpty(vendors)) {
            days = daysRepository.findAll();
            frequencies = frequencyRepository.findAll();
            holidays = holidayRepository.findAll();
            tools = toolsRepository.findAll();
            toolsCharges = toolsChargesRepository.findAll();
            statuses = toolStatusRepository.findAll();
            types = toolTypeRepository.findAll();
            vendors = vendorRepository.findAll();
        }

        System.out.println("days " + Arrays.toString(days.toArray()));
        System.out.println("frequencies " + Arrays.toString(days.toArray()));
        System.out.println("holidays " + Arrays.toString(holidays.toArray()));
        System.out.println("tools " + Arrays.toString(tools.toArray()));
        System.out.println("toolsCharges " + Arrays.toString(toolsCharges.toArray()));
        System.out.println("statuses " + Arrays.toString(statuses.toArray()));
        System.out.println("types " + Arrays.toString(types.toArray()));
        System.out.println("vendors " + Arrays.toString(vendors.toArray()));
    }

    /**
     * Get the tool from the given code
     *
     * @param code The code to check
     * @return The tool if found or null
     */
    public Tools getToolsFromCode(String code) {
        Tools found = null;
        for (Tools tool : tools) {
            if (tool.getCode().equalsIgnoreCase(code)) {
                found = tool;
                break;
            }
        }
        return found;
    }

    /**
     * Get the charge from the given type
     *
     * @param type The type to find
     * @return The tool if found or null
     */
    public ToolsCharges getToolsChargesFromType(String type) {
        ToolsCharges found = null;
        for (ToolsCharges charge : toolsCharges) {
            if (charge.getType().equalsIgnoreCase(type)) {
                found = charge;
                break;
            }
        }
        return found;
    }

}
