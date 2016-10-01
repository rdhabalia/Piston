Piston
=================

Piston can parse any complex xsd and loads it in tree data-structure. Using in memory data-structure it can transfor xsd-template to any templates such Excel, CSV, etc.

[Test-Case](https://github.com/rdhabalia/Piston/blob/master/src/test/java/com/plugin/excel/auto/test/ExcelGenerationManagerTest.java) demonstrates [Netflix-Xsd](https://github.com/rdhabalia/Piston/tree/master/src/main/resources/xsds/V1_0) has two kinds of xsd

- [Root-xsd](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/root.xsd) : that contains netflix-user information
- [Child-xsd](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/Album.xsd) : that contains types of media which netflix serves
	- [Movie-xsd](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/Album.xsd) : which has movie related attributes
	- [Album-xsd](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/Movie.xsd): which has album relates attributes

[Test-Case](https://github.com/rdhabalia/Piston/blob/master/src/test/java/com/plugin/excel/auto/test/ExcelGenerationManagerTest.java) generates [Movie](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/Movie.xsd) and [Album](https://github.com/rdhabalia/Piston/blob/master/src/main/resources/xsds/V1_0/Album.xsd) Excel templates from [Xsd]() at [location](https://github.com/rdhabalia/Piston/tree/master/src/main/resources/excel/1.0).
