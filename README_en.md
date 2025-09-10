# RandomSeatGenerator

[简体中文](README.md) ｜ English

## Conclusion

The Java implementation of the RandomSeatGenerator application.

See also: [RandomSeat](https://github.com/edp2021c1/RandomSeat)

## Environment requirement

JDK: 21+
JavaFX: 21+

## Command line parameters

| Argument             | Function                                                                   |
|----------------------|:---------------------------------------------------------------------------|
| --nogui              | Enters the console mode without launching GUI                              |
| --seed=<value>       | Sets the seed used to generate the seat table, default to a random string  |
| --open-result        | Opens the output file after exporting                                      |

## Default config

```json5
{
  "language": "zh_cn",
  "darkMode": false,
  "seatConfig": {
    "row_count": 6,
    "column_count": 8,
    "shuffledRows": 3,
    "disabledLastRowPos": "",
    "person_sort_by_height": "43 4 3 1 7 9 6 34 18 40 25 21 32 14 44 41 11 15 47 45 16 23 22 28 2 8 5 29 38 10 20 12 36 19 33 42 24 26 37 13 39 27 46 17 31 35",
    "group_leader_list": "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47",
    "separate_list": "",
    "lucky_option": false,
    "findLeaders": true
  }
}
```

## Libs

- SLF4J Api v2.0.17 [Official](https://www.slf4j.org/) [GitHub](https://github.com/qos-ch/slf4j)
- Log4J2 v2.25.1 [Official](https://logging.apache.org/log4j/2.x/index.html) [GitHub](https://github.com/apache/logging-log4j2)
- Guava v33.4.8 [GitHub](https://github.com/google/guava)
- Gson v2.13.1 [GitHub](https://github.com/google/gson)
- Apache POI v5.4.1 [Official](https://poi.apache.org/) [GitHub](https://github.com/apache/poi)
- JavaFX [Official](https://openjfx.io/) [GitHub](https://github.com/openjdk/jfx)
