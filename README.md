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

### 通用参数

| 参数        | 作用           |
|-----------|:-------------|
| --help    | 打印帮助信息并退出    |
| --license | 打印许可证并退出     |
| --version | 打印版本信息并退出    |
| --debug   | 在命令行界面输出调试信息 |

### 仅命令行模式参数

| 参数                   | 作用                    |
|----------------------|:----------------------|
| --nogui              | 不启动GUI界面，进入命令行模式      |
| --config-path=<path> | 指定配置文件位置（只生效一次）       |
| --seed=<value>       | 设置生成座位表的种子，默认为随机数/字符串 |
| --output-path=<path> | 指定目标导出文件或目录（只生效一次）    |
| --open-result        | 导出完成后打开文档             |

## 配置文件格式：

```json5
{
  // 行数
  "row_count": 7,
  // 列数
  "column_count": 7,
  // 随机轮换的行数
  "random_between_rows": 2,
  // 最后一排不可选的位置（从1到列数，其余值将会忽略）
  "last_row_pos_cannot_be_chosen": "1 2 7",
  // 按身高排序的人名列表
  "person_sort_by_height": "31 28 22 3 37 24 34 1 6 44 38 7 2 4 16 19 13 40 12 36 8 21 18 10 41 14 20 43 35 15 26 32 17 42 27 29 9 5 25 30 11 23 39 33",
  // 组长列表
  "group_leader_list": "2 4 10 16 19 20 24 25 26 27 28 29 30 31 32 33 34 39 44",
  // 拆分列表，每行一组
  "separate_list": "25 30\n8 34",
  // 是否随机挑选一名“左护法”
  "lucky_option": true,
}
```
