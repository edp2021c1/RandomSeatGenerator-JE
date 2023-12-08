package com.edp2021c1.randomseatgenerator.util;

import com.edp2021c1.randomseatgenerator.core.IllegalConfigException;
import com.edp2021c1.randomseatgenerator.core.SeatConfig;

/**
 * Stores config of the application.
 */
public class AppConfig extends SeatConfig {
    /**
     * If seat table is exported writable.
     */
    public Boolean export_writable;
    /**
     * The previous directory seat table is exported to.
     */
    public String last_export_dir;
    /**
     * The previous directory config is loaded from.
     */
    public String last_import_dir;

    @Override
    public void checkFormat() {
        super.checkFormat();
        if (export_writable == null) {
            throw new IllegalConfigException("Export writable cannot be null");
        }
    }

    /**
     * Pulls {@code this} and {@code value} together.
     * Fields of {@code value} that are not null will override the field in {@code this}.
     *
     * @param value the value to set to {@code this}
     */
    public void set(final AppConfig value) {
        if (value.row_count != null) {
            row_count = value.row_count;
        }
        if (value.column_count != null) {
            column_count = value.column_count;
        }
        if (value.random_between_rows != null) {
            random_between_rows = value.random_between_rows;
        }
        if (value.last_row_pos_cannot_be_chosen != null) {
            last_row_pos_cannot_be_chosen = value.last_row_pos_cannot_be_chosen;
        }
        if (value.person_sort_by_height != null) {
            person_sort_by_height = value.person_sort_by_height;
        }
        if (value.group_leader_list != null) {
            group_leader_list = value.group_leader_list;
        }
        if (value.separate_list != null) {
            separate_list = value.separate_list;
        }
        if (value.lucky_option != null) {
            lucky_option = value.lucky_option;
        }
        if (value.export_writable != null) {
            export_writable = value.export_writable;
        }
        if (value.last_export_dir != null) {
            last_export_dir = value.last_export_dir;
        }
        if (value.last_import_dir != null) {
            last_import_dir = value.last_import_dir;
        }
    }
}
