# RandomSeatGenerator

简体中文 | [English](README_en.md)

## 概述

Java版随机排座位程序

参见：[RandomSeat](https://github.com/edp2021c1/RandomSeat)

## 环境要求

|        | Windows  | Mac OS  | 其他   |
|--------|:---------|:--------|:-----|
| x86-64 | msi️/jar | jar️    | jar  |
| ARM64  | msi/jar  | dmg/jar | jar  |
| 其他     | -        | -       | jar️ |

jar：需要JRE+JavaFX运行时

## 命令行参数

| 参数             | 作用                    |
|----------------|:----------------------|
| --nogui        | 不启动GUI界面，进入命令行模式      |
| --seed=<value> | 设置生成座位表的种子，默认为随机数/字符串 |
| --open-result  | 导出完成后打开文档             |

## 配置文件格式：

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
