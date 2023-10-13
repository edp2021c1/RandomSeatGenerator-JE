package com.edp2021c1.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseUnsignedInt;

/**
 * Stores config used to generate a seat table.
 * @since 1.2.0
 */
public final class SeatConfig {
    /**
     * Row count (int).
     */
    public String row_count;
    /**
     * Column count (int).
     */
    public String column_count;
    /**
     * Count of the rows rotated randomly together as group (int).
     */
    public String random_between_rows;
    /**
     * Positions in the last row that can be chosen, in case something blocks the last row ({@code space} between two numbers).
     */
    public String last_row_pos_can_be_choosed;
    /**
     * Name list sorted by height ({@code space} between two people).
     */
    public String person_sort_by_height;
    /**
     * A list of people who can be a leader of a column ({@code space} between two people).
     */
    public String group_leader_list;
    /**
     * A list of people pairs separated ({@code \n} between two pairs, and {@code space} between two names of a pair).
     */
    public String separate_list;
    /**
     * Whether there will be a lucky person specially chosen from the last rows.
     */
    public boolean lucky_option;

    public SeatConfig() {
        super();
    }

    /**
     * @return {@link SeatConfig#row_count} in the format of an integer.
     * @throws NumberFormatException if the {@code String} does not contain a parsable unsigned integer.
     */
    public int getRowCount() throws NumberFormatException {
        return parseUnsignedInt(row_count);
    }

    /**
     * @return {@link SeatConfig#column_count} in the format of an integer.
     * @throws NumberFormatException if the {@code String} does not contain a parsable unsigned integer.
     */
    public int getColumnCount() throws NumberFormatException {
        return parseUnsignedInt(column_count);
    }

    /**
     * @return {@link SeatConfig#random_between_rows} in the format of an integer.
     * @throws NumberFormatException if the {@code String} does not contain a parsable unsigned integer.
     */
    public int getRandomBetweenRows() throws NumberFormatException {
        return parseUnsignedInt(random_between_rows);
    }

    /**
     * @return {@link SeatConfig#last_row_pos_can_be_choosed} in the format of a list of {@code int}.
     * @throws NumberFormatException if the {@code String}s does not contain a parsable unsigned integer.
     */
    public ArrayList<Integer> getLastRowPos() throws NumberFormatException {
        String[] t = last_row_pos_can_be_choosed.split(" ");
        ArrayList<Integer> i = new ArrayList<>(t.length);
        for (String s : t) {
            i.add(parseUnsignedInt(s));
        }
        return i;
    }

    /**
     * @return {@link SeatConfig#person_sort_by_height} in the format of a list of {@code String}.
     */
    public List<String> getNameList() {
        return Arrays.asList(person_sort_by_height.split(" "));
    }

    /**
     * @return {@link SeatConfig#group_leader_list} in the format of a list of {@code String}.
     */
    public List<String> getGroupLeaderList() {
        return Arrays.asList(group_leader_list.split(" "));
    }

    /**
     * @return {@link SeatConfig#separate_list} in the format of a list of {@code Separate}.
     */
    public List<Separate> getSeparatedList() throws Exception {
        String[] t = separate_list.split("\n");
        ArrayList<Separate> s = new ArrayList<>(t.length);

        for (String m : t) {
            if (!m.isBlank()) {
                s.add(new Separate(m));
            }
        }

        return s;
    }

}
