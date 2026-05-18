[![Bygg og deploy](https://github.com/navikt/fp-graphql/actions/workflows/build.yml/badge.svg)](https://github.com/navikt/fp-graphql/actions/workflows/build.yml)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fp-graphql)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navikt_fp-graphql)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=navikt_fp-graphql)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=navikt_fp-graphql)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=navikt_fp-graphql)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-graphql&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_fp-graphql)

![GitHub release (latest by date)](https://img.shields.io/github/v/release/navikt/fp-graphql)
![GitHub](https://img.shields.io/github/license/navikt/fp-graphql)

# FP GraphQL Codegen

Nav foreldrepenger GraphQL Codegen genererer Java klientkode fra GraphQL-skjemaer med fokus på å støtte etablerte skjema i Nav.

Koden er basert på [graphql-java-codegen](https://github.com/kobylynskyi/graphql-java-codegen) (MIT License). 

Hovedendringer ved etablering i april 2026:
* Målsetning: Støtte for Jackson 3 og å generere records.
* Tilpasset til bruk sammen med klienter og Jackson-støtte fra [fp-felles](https://github.com/navikt/fp-felles)
* Minimalisert eksterne avhengigheter og konfigurasjonsmuligheter.
* Refaktorert avhengigheter til Jackson - overlatt helt . 
* Begrenset til Java, Maven og klient-side-POJOs uten interfaces.
* Planlagt bedre støtte for java.time ifm customTypesMapping.


## Ta i bruk

Legges til i prosjekt eller moduler som har et GraphQL-skjema og som vil generere Java POJOs til bruk i klienter.


### Definer i Maven dependencyManagement
```
<dependency>
    <groupId>no.nav.foreldrepenger.graphql</groupId>
    <artifactId>graphql-runtime</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Set opp maven plugin
Vanlig bruk. Genererte filer legges i outputDir og kan brukes i modulen / prosjektet.
```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>no.nav.foreldrepenger.graphql</groupId>
            <artifactId>graphql-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
		    <configuration>
                        <graphqlSchemaPaths>${project.basedir}/src/main/resources/myschema/myschema.graphqls</graphqlSchemaPaths>
                        <outputDir>${project.build.directory}/generated-sources/client-myschema</outputDir>
                        <modelPackageName>no.nav.myschema</modelPackageName>
                        <customTypesMapping>
                            <DateTime>java.util.Date</DateTime>
                        </customTypesMapping>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```


## Codegen Options

|             Option              |     Data Type      | Default value | Description                                                                                                                         |
|:-------------------------------:|:------------------:|:-------------:|-------------------------------------------------------------------------------------------------------------------------------------|
|      `graphqlSchemaPaths`       |    List(String)    |     Empty     | GraphQL schema locations. You can supply multiple paths to GraphQL schemas using <graphqlSchemaPath> for each.                      |
|           `outputDir`           |       String       |     None      | The output target directory into which code will be generated.                                                                      |
|          `packageName`          |       String       |     Empty     | Java package for generated classes.                                                                                                 |
|       `modelPackageName`        |       String       |     Empty     | Java package for generated model classes (type, input, interface, enum, union).                                                     |
|        `generateBuilder`        |      Boolean       |     True      | Specifies whether generated model classes should have builder.                                                                      |
|   `generateEqualsAndHashCode`   |      Boolean       |     False     | Specifies whether generated model classes should have equals and hashCode methods defined.                                          |
| `generateJacksonTypeIdResolver` |      Boolean       |     False     | Specifies whether generated union interfaces should be annotated with a custom Jackson type id resolver generated in model package. |
|   `modelValidationAnnotation`   |       String       |   `NotNull`   | Annotation for mandatory (NonNull) fields. Can be null/empty. Default: `@jakarta.validation.constraints.NotNull`                    |
|      `customTypesMapping`       | Map(String,String) |     Empty     | *See [CustomTypesMapping](#option-customtypesmapping)*                                                                              |
| `fieldsToExcludeFromGeneration` |    Set(String)     |     Empty     | Fields to exclude from generation should be defined here in format: `TypeName.fieldName`.                                           |
|             `skip`              |      Boolean       |     False     | If true, then code generation will not happen                                                                                       |

### Option `customTypesMapping`

Can be used to supply custom mappings for scalars. These are otherwise mapped as String.

* Map of (GraphQLObjectName.fieldName) to (JavaType). E.g.: `Event.dateTime = java.time.LocalDateTime`
* Map of (GraphQLType) to (JavaType). E.g.: `DateTime = java.time.LocalDateTime`


### Licenses and attribution
*For updated information, always see LICENSE first!*
