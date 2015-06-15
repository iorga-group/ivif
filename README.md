# Description #
IVIF is an XML framework used to generate application.

Currently it only supports generating JEE6 + AngularJS web application.

# JEE6 + AngularJS IVIF Project #
## What is generated? ##
This generator generates a JEE6 + AngularJS (later called "JA" in this documentation) web application.

More precisely, here is what will be generated:

 * JPA entities (one for each IVIF entity)
 * A basic CRUD service per entity which uses `EntityManager` as a DAO, which uses JTA `@TransactionAttribute` for database modification methods, and which uses [QueryDSL](http://querydsl.com/) QEntity in search methods (one search method per declared IVIF grid)
 * A JAX-RS REST webservices for every IVIF grid

## Recommended project structure ##
Here is the recommended project structure:

 * `<project base>`
   * `src`
     * `main`
       * `ivif`: all your IVIF xml source files
         * `conf`
           * `ivif-ja-config.xml`: your IVIF JA configuration file containing the `<configurations>` root
         * `entities`: folder which contains all your IVIF `<entities>`, one file for each entity
         * `views`: folder which contains all your IVIF `<views><grid/></views>`

## Maven configuration ##
To create a new IVIF project, create 2 maven projects:

 * A "parent" project (`<packaging>pom</packaging>`)
 * The main webapp project

Here is the minimum parent `pom.xml` configuration:

```xml
<project>
    <packaging>pom</packaging>

    <dependencyManagement>
        <dependencies>
            <!-- scope import -->
            <dependency>
                <groupId>org.jboss.bom</groupId>
                <artifactId>jboss-javaee-6.0-with-all</artifactId>
                <version>${jboss-javaee-6.0.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- scope compile -->
            <dependency>
                <groupId>com.iorga</groupId>
                <artifactId>ivif-jee6-angularjs-re</artifactId>
                <version>1.0.0</version>
            </dependency>
            <dependency>
                <groupId>com.iorga</groupId>
                <artifactId>ivif-api</artifactId>
                <version>1.0.0</version>
            </dependency>
            <dependency>
                <groupId>com.mysema.querydsl</groupId>
                <artifactId>querydsl-jpa</artifactId>
                <version>3.6.2</version>
            </dependency>
            <!-- scope provided -->
            <dependency>
                <groupId>com.mysema.querydsl</groupId>
                <artifactId>querydsl-apt</artifactId>
                <version>3.6.2</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>com.iorga</groupId>
                <artifactId>ivif-jee6-angularjs-impl</artifactId>
                <version>1.0.0</version>
                <scope>provided</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

Here is the minimum main webapp project `pom.xml` configuration:

```xml
   <dependencies>
        <dependency>
            <groupId>com.iorga</groupId>
            <artifactId>ivif-jee6-angularjs-re</artifactId>
        </dependency>
        <dependency>
            <groupId>com.iorga</groupId>
            <artifactId>ivif-api</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysema.querydsl</groupId>
            <artifactId>querydsl-jpa</artifactId>
        </dependency>
        <!-- scope provided -->
        <dependency>
            <groupId>org.hibernate.javax.persistence</groupId>
            <artifactId>hibernate-jpa-2.0-api</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss.spec.javax.ejb</groupId>
            <artifactId>jboss-ejb-api_3.1_spec</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss.spec.javax.ws.rs</groupId>
            <artifactId>jboss-jaxrs-api_1.1_spec</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>javax.enterprise</groupId>
            <artifactId>cdi-api</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.mysema.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.iorga</groupId>
            <artifactId>ivif-jee6-angularjs-impl</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.codehaus.jackson</groupId>
            <artifactId>jackson-core-asl</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxrs</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>1.9.1</version>
                <executions>
                    <execution>
                        <id>add-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>src/main/ivif-generated-sources/java</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <artifactId>maven-clean-plugin</artifactId>
                <version>2.6.1</version>
                <configuration>
                    <filesets>
                        <fileset>
                            <directory>src/main/ivif-generated-sources</directory>
                            <includes>
                                <include>**/*</include>
                            </includes>
                        </fileset>
                    </filesets>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-war-plugin</artifactId>
                <version>2.5</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                    <webResources>
                        <resource>
                            <directory>src/main/ivif-generated-sources/webapp</directory>
                            <targetPath>/</targetPath>
                        </resource>
                    </webResources>
                </configuration>
            </plugin>
            <plugin>
                <groupId>com.iorga</groupId>
                <artifactId>ivif-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <generatorClass>com.iorga.ivif.ja.tag.JAGenerator</generatorClass>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/generated-sources/java</outputDirectory>
                            <processor>com.mysema.query.apt.jpa.JPAAnnotationProcessor</processor>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

## Maven generation ##
In order to generate the application using JA generation, use this command:

```bash
mvn clean && mvn generate-sources generate-resources
```