# RandomSeatGenerator

简体中文 | [English](README_en.md)

Copyright (C) 2025  EDP2021C1

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

## 概述

Java版随机排座位程序

参见：[RandomSeat](https://github.com/edp2021c1/RandomSeat)

## 环境要求

JDK: 21+
JavaFX: 21+

## 命令行参数

| 参数             | 作用                    |
|----------------|:----------------------|
| --nogui        | 不启动GUI界面，进入命令行模式      |
| --seed=<value> | 设置生成座位表的种子，默认为随机数/字符串 |
| --open-result  | 导出完成后打开文档             |

## 默认配置：

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

## 库

- SLF4J Api v2.0.17 [官方](https://www.slf4j.org/) [GitHub](https://github.com/qos-ch/slf4j)
- Log4J2 v2.25.1 [官方](https://logging.apache.org/log4j/2.x/index.html) [GitHub](https://github.com/apache/logging-log4j2)
- Guava v33.4.8 [GitHub](https://github.com/google/guava)
- Gson v2.13.1 [GitHub](https://github.com/google/gson)
- Apache POI v5.4.1 [官方](https://poi.apache.org/) [GitHub](https://github.com/apache/poi)
- JavaFX [官方](https://openjfx.io/) [GitHub](https://github.com/openjdk/jfx)

