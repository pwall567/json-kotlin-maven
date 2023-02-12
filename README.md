# json-kotlin-maven

[![Build Status](https://travis-ci.com/pwall567/json-kotlin-maven.svg?branch=main)](https://travis-ci.com/github/pwall567/json-kotlin-maven)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v1.6.10&color=7f52ff&logo=kotlin&logoColor=7f52ff)](https://github.com/JetBrains/kotlin/releases/tag/v1.6.10)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.json/json-kotlin-maven?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.json%22%20AND%20a:%22json-kotlin-maven%22)

Maven JSON Schema code generation plugin.

## Background

The [`json-kotlin-schema-codegen`](https://github.com/pwall567/json-kotlin-schema-codegen) project provides a means of
generating Kotlin or Java classes (or TypeScript interfaces) from [JSON Schema](https://json-schema.org/) object
descriptions.
The `json-kotlin-maven` plugin simplifies the use of this code generation mechanism from
[Maven](https://maven.apache.org/).

## To Use

First, ensure you have a `pluginRepository` entry for Maven Central:
```xml
  <pluginRepositories>
    <pluginRepository>
      <id>central</id>
      <name>Maven Central</name>
      <url>https://repo1.maven.org/maven2/</url>
    </pluginRepository>
  </pluginRepositories>
```

Then, in the plugins section:
```xml
      <plugin>
        <groupId>net.pwall.json</groupId>
        <artifactId>json-kotlin-maven</artifactId>
        <version>0.87</version>
        <executions>
          <execution>
            <phase>generate-sources</phase>
            <goals>
              <goal>codegen</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```
And to have the generated source files compiled:
```xml
    <sourceDirectory>target/generated-sources/kotlin</sourceDirectory>
```
(this shows the use of the default output directory; if it is changed by the use of the `outputDir` parameter, this line
will also change.)

The plugin follows the principle of "convention over configuration".
The default location for the schema file or files to be input to the generation process is:
```
    «project root»/src/main/resources/schema
```
and the default location for the
[config file](https://github.com/pwall567/json-kotlin-schema-codegen/blob/main/CONFIG.md) (if required) is:
```
    «project root»/src/main/resources/codegen-config.json
```
If your files are in these default locations, the above additions to `pom.xml` will be all you will need.

If you wish to specify the location of your schema or config files, or if you wish to use any of the other plugin
customisation options, you may include a `configuration` section in the `plugin` declaration as follows:
```xml
      <plugin>
        <groupId>net.pwall.json</groupId>
        <artifactId>json-kotlin-maven</artifactId>
        <version>0.87</version>
        <configuration>
          <configFile>path/to/your/config.json</configFile>
          <inputFile>path/to/your/schema/file/or/files</inputFile>
          <packageName>your.package.name</packageName>
          <pointer>/$defs</pointer>
          <comment>Test comment 12345</comment>
        </configuration>
        <executions>
          <execution>
            <phase>generate-sources</phase>
            <goals>
              <goal>codegen</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
```

If any setting in this configuration block conflicts with the equivalent setting in the config file, this configuration
block (in `pom.xml`) takes precedence.

## Configuration Options

### `configFile`

If the config file is not in the default location of `src/main/resources/codegen-config.json`, the location of the file
may be specified using the `configFile` property:
```xml
          <configFile>path/to/your/config.json</configFile>
```

### `inputFile`

The input to the code generation process may be a single file or a directory tree containing a number of schema files.
By default, the plugin will process all files in the `src/main/resources/schema` directory (and subdirectories), but to
specify an alternative file or directory, use:
```xml
          <inputFile>path/to/your/schema/file/or/files</inputFile>
```

### `pointer`

One of the common uses of the code generator is to generate a set of classes from a composite file.
For example, an OpenAPI file may contain a number of schema definitions for the request and response objects of the API,
or a JSON Schema file may contain a set of definitions in the `$defs` section.

To specify this form of usage to the plugin, the configuration block must contain:
```xml
          <inputFile>path/to/your/schema/file/or/files</inputFile>
          <pointer>/pointer/to/definitions</pointer>
```

For example, to process the entire set of schema definitions in an OpenAPI file:
```xml
          <inputFile>src/main/resources/openapi/openapi.yaml</inputFile>
          <pointer>/components/schemas</pointer>
```

### `outputDir`

The default output directory is `target/generated-sources/«language»`, where `«language»` is `kotlin`, `java` or
`ts` (for TypeScript), depending on the target language.
To specify a different output directory, use:
```xml
          <outputDir>path/to/your/output/directory</outputDir>
```

### `language`

The default target language is Kotlin (naturally enough, for a project written in Kotlin and primarily intended to
target Kotlin).
To specify Java output:
```xml
          <language>java</language>
```
The value of this setting must be `kotlin`, `java` or `typescript`.

This setting may be specified in the config file, and that is the recommended practice.

### `packageName`

By default, classes will be generated without a `package` directive.
To supply a package name:
```xml
          <packageName>com.example.model</packageName>
```

This setting may be specified in the config file, and that is the recommended practice.

### `comment`

The `comment` allows the provision of a line of text to be added to the comment block output to the start of each
generated file:
```xml
          <comment>This was generated from Schema version 1.0</comment>
```

This setting may be specified in the config file, and that is the recommended practice.

Peter Wall

2023-02-12
