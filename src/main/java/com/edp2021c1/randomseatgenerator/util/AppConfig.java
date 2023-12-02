package com.edp2021c1.randomseatgenerator.util;

import com.edp2021c1.randomseatgenerator.core.SeatConfig;

import java.util.Objects;

public class AppConfig extends SeatConfig {
    public Boolean export_writable;
    public String lastExportDir;

    /**
     * Check if another instance equals to this one.
     *
     * @param another another instance to compare with.
     * @return if these two instances are equal.
     */
    public boolean equals(final AppConfig another) {
        return Objects.equals(row_count, another.row_count)
                && Objects.equals(column_count, another.column_count)
                && Objects.equals(random_between_rows, another.random_between_rows)
                && Objects.equals(last_row_pos_cannot_be_chosen, another.last_row_pos_cannot_be_chosen)
                && Objects.equals(person_sort_by_height, another.person_sort_by_height)
                && Objects.equals(group_leader_list, another.group_leader_list)
                && Objects.equals(separate_list, another.separate_list)
                && Objects.equals(lucky_option, another.lucky_option)
                && Objects.equals(export_writable, another.export_writable)
                && Objects.equals(lastExportDir, another.lastExportDir);
    }

    public void set(final AppConfig value) {
        row_count = value.row_count == null ? row_count : value.row_count;
        column_count = value.column_count == null ? column_count : value.column_count;
        random_between_rows = value.random_between_rows == null ? random_between_rows : value.random_between_rows;
        last_row_pos_cannot_be_chosen = value.last_row_pos_cannot_be_chosen == null ? last_row_pos_cannot_be_chosen : value.last_row_pos_cannot_be_chosen;
        person_sort_by_height = value.person_sort_by_height == null ? person_sort_by_height : value.person_sort_by_height;
        group_leader_list = value.group_leader_list == null ? group_leader_list : value.group_leader_list;
        separate_list = value.separate_list == null ? separate_list : value.separate_list;
        lucky_option = value.lucky_option == null ? lucky_option : value.lucky_option;
        export_writable = value.export_writable == null ? export_writable : value.export_writable;
        lastExportDir = value.lastExportDir == null ? lastExportDir : value.lastExportDir;
    }
}
