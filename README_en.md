# RandomSeatGenerator

[简体中文](README.md) ｜ English

## Conclusion

The Java implementation of the RandomSeatGenerator application.

See also: [RandomSeat](https://github.com/edp2021c1/RandomSeat)

## Environment requirement

|        | Windows | Mac OS        | Linux and Others     |
|--------|:--------|:--------------|:---------------------|
| x86-64 | ✅️      | ✅️            | Requires JRE+JavaFX️ |
| ARM64  | ✅       | ✅ (Rosetta 2) | Requires JRE+JavaFX️ |
| Others | -       | -             | Requires JRE+JavaFX️ |

## Command line parameters

### General parameters

| Argument  | Function                          |
|-----------|:----------------------------------|
| --help    | Prints the help info and quits    |
| --license | Prints the license info and quits |
| --version | Prints the version info and quits |
| --debug   | Turns on the console debug output |

### Console-mode-only parameters

| Argument             | Function                                                                   |
|----------------------|:---------------------------------------------------------------------------|
| --nogui              | Enters the console mode without launching GUI                              |
| --config-path=<path> | Sets the path of the config used for generating seat table (only for once) |
| --seed=<value>       | Sets the seed used to generate the seat table, default to a random string  |
| --output-path=<path> | Sets the output file or directory path (only for once)                     |
| --open-result        | Opens the output file after exporting                                      |

## Seat table config format

```json5
{
  // Row count
  "row_count": 7,
  // Column count
  "column_count": 7,
  // Random rotated between rows
  "random_between_rows": 2,
  // Unavailable last row seats (from 1 to column count)
  "last_row_pos_cannot_be_chosen": "1 2 7",
  // Person names sorted by height
  "person_sort_by_height": "31 28 22 3 37 24 34 1 6 44 38 7 2 4 16 19 13 40 12 36 8 21 18 10 41 14 20 43 35 15 26 32 17 42 27 29 9 5 25 30 11 23 39 33",
  // Group leader list
  "group_leader_list": "2 4 10 16 19 20 24 25 26 27 28 29 30 31 32 33 34 39 44",
  // Separated pairs, one in each row
  "separate_list": "25 30\n8 34",
  // Whether a “lucky person” will be chosen
  "lucky_option": true,
}
```
