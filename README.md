# Description #
IVIF is an XML framework used to generate applications.

Currently it only supports generating JEE6 + AngularJS web applications.

# How can I develop with IVIF? #
With IVIF, you design your **entities** (one by data table) and the application **grids** (that is to say sortable, filterable and editable datatables).

## Entities ##
An entity represents a table of your data.

Here is a basic entity (`User.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<entities xmlns="http://www.iorga.com/xml/ns/ivif-entities"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.iorga.com/xml/ns/ivif-entities">

    <entity name="User" table="USER">
        <integer name="id" id="true">
            <sequence name="USER_ID_SEQ"/>
        </integer>
        <string name="name" column="LAST_NAME" required="true" display-name="true"/>
        <string name="firstName" column="FIRST_NAME" required="true" display-name="true"/>
        <many-to-one name="profile" column="PROFILE_ID" ref="Profile">
            <join-column column="PROFILE_ID" ref-column="id"/>
        </many-to-one>
        <enum name="status" ref="UserStatusType"/>
        <datetime name="lastModification"/>
    </entity>

    <selection name="UserStatusType" from-type="string">
        <option name="ACTIVE"/>
        <option name="DISABLED" value="DIS"/>
        <option name="UNKNOWN" value="?" title="Not known"/>
    </selection>

</entities>
```

As you can see, an `<entity>` contains multiple entries which define more or less the fields of the entity.

### Entity types ###
Here are the attributes you can set on a `<entity>` tag:

 * `name`: name of this entity (must start with a capital letter)
 * `table`: name of the table this entity is associated to
 * `implements`: interfaces this entity should implement. Example: `implements="com.iorga.ivif.test.Versionable&lt;java.lang.Long&gt;"`

Here are the different entity types (XML tags inside your entity) you can use:

 * `character`: a character field
 * `string`: a string field
   * `default-editor` (value: [`inputText`|`textArea`]): you can define on a `string` which default visual editor will be used when presenting the field in your editable grids
 * `long`: a long field (supports `version` attribute)
 * `boolean`: a boolean field
   * `from-type` (value: any type): if this boolean field is not boolean in your database, you can use this attribute to define the real database type of this field
   * `true-value`: database value which corresponds to "true"
   * `false-value`: database value which corresponds to "false"
 * `date`: a date field
 * `datetime`: a date + time field (supports `version` attribute)
 * `integer`: an integer field (supports `version` attribute)
 * `enum`: a field which can have a limited set of possible value.
   * `ref`: the name of the `<selection>` this field is using
 * `many-to-one`: a field which represents a foreign key to another table
   * `ref`: the name of the `<entity>` this field points to
   * `<join-column>`: multiple instances of this tag can be inserted into the `<many-to-one>` tag. Defines multiple join columns. This will generate multiple `@JoinColumn` in a `@JoinColumns` on this field
     * `ref-column`: name of the field to join to on the other part of the `many-to-one`.
     * `column`: name of the column in the database table
     * `insertable` (value: [`true`|`false`] ; default: `false`): whether this field will be used when persisting this entity. Used to generate `@JoinColumn(insertable = ?)`
     * `updatable` (value: [`true`|`false`] ; default: `false`): whether this field will be used when updating this entity. Used to generate `@JoinColumn(updatable = ?)`

All those types accept the following attributes:

 * `name` (required): name of the field
 * `column`: name of the column in the database table
 * `title`: name of this column which will be used as title for grid column
 * `id` (value: [`true`|`false`] ; default: `false`): whether this field is a part of the `@Id` of this entity. It is mandatory to have at least one `id="true"` field in an entity
   * `<sequence name="?" allocation-size="?">`: this tag can be added into an `id="true"` field in order to add a `@SequenceGenerator` and `@GeneratedValue(strategy = GenerationType.SEQUENCE)` on this field
 * `version` (value: [`true`|`false`] ; default: `false`): whether this field is the `@Version` of this entity
 * `required` (value: [`true`|`false`] ; default: `false`): whether this field is required or not. Used to add `@NotNull` on this entity field.
 * `display-name` (value: [`true`|`false`] ; default: `false`): whether this field takes part of the display name of this entity (will be integrated in the generated `displayName()` method on the entity)
 * `transient` (value: [`true`|`false`] ; default: `false`): whether this field is transient = does not represent a real column of your database. Used to generate `@Transient` annotation.
 * `formula`: an SQL query that returns this field value. This will be used to generate a hibernate `@Formula` annotation.
 * `insertable` (value: [`true`|`false`] ; default: `true`): whether this field will be used when persisting this entity. Used to generate `@Column(insertable = ?)`
 * `updatable` (value: [`true`|`false`] ; default: `true`): whether this field will be used when updating this entity. Used to generate `@Column(updatable = ?)`

### Selections ###
A `<selection>` represents a type that can have specific fixed values. A selection will generate an `enum`.

Here are the attributes available on `<selection>`:

 * `name`: name of this selection (which can be referenced by `ref` attribute on a `<enum>` entity field). Must start with a capital letter.
 * `from-type` (value: [`integer`|`string`]): real database type

Inside this tag, put some `<option>` tags which accept those attributes:

 * `name` (required): name of this option. It is a best practice to use only capital letters for that name.
 * `value`: value of this option. By default, takes the value of `name` attribute.
 * `title`: value to display to the user when using this in lists. By default takes the `value` or `name` attribute.

## Views ##
In a `<views>` tag, you can define `grid`s or `action-open-view`.

A grid represents a datatable presentation of your data.

An "action open view" represents a transition between two grids.

Here is a sample grid:

```xml
<?xml version="1.0" encoding="utf-8"?>
<views xmlns="http://www.iorga.com/xml/ns/ivif-views"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.iorga.com/xml/ns/ivif-views">

    <grid name="EditableUserGrid" title="That's the users" entity="User" on-open="$action(openProfileGridFromUser)({profileId: $line.profile.id})" editable="true" roles-allowed="admin manager">
        <column ref="firstName"/>
        <column ref="name" editable-if="$record.profile.name === 'editableName'"/>
        <column ref="status" editable="true"/>
        <code><![CDATA[
            <td data-title="'Profile description'">
                Desc : {{$line.profile.description}}
            </td>
        ]]></code>
        <column ref="commentTemp" editable="true" required-if="$isDirty($line)"/>
        <column ref="enabled" editable="true"/>
        <column ref="bigComment" editable="true"/>
        <column ref="pass" editable="true"/>
        <column ref="lastModification" editable="true"/>
    </grid>

    <action-open-view name="openProfileGridFromUser" grid-name="ProfileGrid" roles-allowed="admin">
        <query>
            <where>$record.id = :profileId</where>
        </query>
    </action-open-view>
</views>
```

### Grid ###
First, here are the attributes you can set on a `<grid>` tag:

 * `name` (required): name of this grid. Must start with a capital letter
 * `entity` (required): name of the entity (`name` attribute of an `<entity>`) this grid will list
 * `title`: title of this grid. Will be displayed in the header of the grid
 * `tab-title`: title of the tab in which this grid is contained
 * `selection` (value: [`single`|`none`] ; default: `none`): enables the selection capacity on this grid
 * `editable` (value: [`true`|`false`] ; default: `false`): whether this grid is editable or not
 * `editable-if`: javascript expression which returns a boolean representing whether this grid is editable or not
 * `on-open`: javascript expression which is executed when you click on a line of the grid
 * `on-select`: javascript expression which is executed when you select a line of the grid (with `selection` set to `single`)
 * `service-save-method`: reference of the java method to call when clicking on "Save" button (with `editable` set to `true` and modified data). This method should have this signature: `save(List<E> entitiesToSave)` with `E` the class of the entity referenced by `entity` attribute.
 * `service-search-method`: reference of the java method to call to retrieve data. This method should have this signature: `com.mysema.query.SearchResults<R> search(S searchParam)` with `R` the class of this generated grid search result (`UserGridSearchResult` for example) and `S` the class of the generated grid search param (`UserGridSearchParam` for example).
 * `roles-allowed`: list of roles (separated by space) which are allowed to access this grid

Inside a `<grid>` you can then add those tags (in this order):

 * `<toolbar>`: defines what is in the toolbar
   * `<button>`: a button
     * `title` (required): the title of the button
     * `action`: javascript expression launched when clicking on the button
     * `disabled-if`: javascript expression returning a boolean which defines whether this button is disabled or not
   * `<code>`: free angular HTML code
 * `<highlight>`: defines how to highlight specific lines
   * `if` (required): javascript expression returning a boolean which defines whether a line is targeted or not
   * `color-class` (required): css class to apply to targeted lines
 * `<column>`: defines a column of the grid
   * `ref` (required): see bellow
   * `from` (default: `$record`): see bellow
   * `title`: title of this column. Defaults to the `title` of this field entity
   * `editable` (value: [`true`|`false`] ; default: `false`): whether this column is editable or not
   * `editable-if`: javascript expression which returns a boolean representing whether this column is editable or not
   * `required-if`: javascript expression which returns a boolean representing whether this column is editable or not
 * `<code>`: free angular HTML code
 * `<column-hidden-edit>`: hidden column (not displayed) but editable (the web service will take into account this field modifications)
   * `ref` (required): see bellow
   * `from` (default: `$record`): see bellow
 * `<column-filter>`: hidden column (not displayed) but filterable (the web service will take into account this field value for filtering)
   * `ref` (required): see bellow
   * `from` (default: `$record`): see bellow
 * `<column-filter-param>`: hidden parameter which takes a part into the filter (should be used with `service-search-method` to redefine a specific search method which takes this parameter into account). This will be used to generate an additional parameter in the grid search param
   * `name` (required): name of the filter
   * `type` (required): type of this parameter (use one of the `<entity>` type)
 * `<column-sort>`: hidden column (not displayed) but sortable (the web service will take into account this field value for sorting)
   * `ref` (required): see bellow
   * `from` (default: `$record`): see bellow
 * `<query>`: the base JPA query of the grid (the following tags must be written in this order)
   * `<from>`: a string which represents the "from" part of the query. Used to left join parts. Must always start with `$record`. For example: `<from>$record left join $record.profile profile</from>`
   * `<where>`: a string which represents the "where" part of the query. Use the special variable `$record` as the "current entity". For example `<where>$record.field1 = 'test'</where>`
   * `<default-order-by>`: default order by for the query. Use the special variable `$record` as the "current entity". For example `<default-order-by>$record.name DESC, $record.user.name</default-order-by>`
   * `<parameter>`: a parameter used in the query. The value is either static or a piece of java code.
     * `name` (required): the name of this parameter. This will be used in the query like this `$record.field = :param1` if this parameter name is `param1`.
     * `value` (required): the value for this parameter. Either a static value or a piece of java code. For example: `<parameter name="currentUserId" value="$inject(com.iorga.ivif.ja.test.ConnectedUser).getUserId()"/>`

For tags which accept `ref` and `from` attributes, here is the documentation:
 * `ref` (required): reference of a field of the entity. You can use "pointed annotation" like `field.subField` if `field` is a `<many-to-one>`
 * `from` (default: `$record`): name of a jointed entity declared in the `<query>`'s `<from>`

The reference to java method should be like `="com.iorga.ivif.test.UserService.save"`.
Javascript expressions accept those special references:

 * `$action(actionName)({param1: value1, param2: value2, ...})`: will point to an action defined with `<action-open-view name="actionName">` and which have in this example `:param1` and `:param2` as query parameters in its `<where>` part.
 * `$inject(angularServiceName)(param1, param2, ...)`: will inject `angularServiceName` in the angular controller of the grid so it can be used in the grid view
 * `$line.field1`: references the field `field1` of current selected line
 * `$line(joinedEntityReference).field2`: references the field `field2` of the jointed entity reference `joinedEntityReference` declared in the `<query>`'s `<from>`
 * `$record.field1`: references the field `field1` of the original record value of the current selected line
 * `$record(joinedEntityReference).field2`: references the field `field2` of the original record value of the jointed entity reference `joinedEntityReference` declared in the `<query>`'s `<from>`

### Action open view ###
An "action open view" will generate the code needed to define a transition between 2 grids.
It contains a query part which will be added to the target grid in order to "filter out" the target grid data.

Here are the attributes of `<action-open-view>`:

 * `name`: the name of this action. Should start with a lower case letter.
 * `grid-name`: the name of the grid this action will open.
 * `roles-allowed`: the roles allowed to use this action
 * `<query>`: a query element, like in a `<grid>` except that all the parameters (`:param`) defined in the `<where>` part will be used as an input parameter

Here is an example:

```xml
    <action-open-view name="openComputerGridFromUser" grid-name="ComputerGrid">
        <query>
            <where>$record.user.id = :userid</where>
        </query>
    </action-open-view>
```

which will be used by this grid:

```xml
    <grid name="UserGrid" title="Users" entity="User" on-open="$action(openComputerGridFromUser)({userId: $line.id})">
        <column ref="name"/>
        <column ref="profile.id"/>
    </grid>
```

So the target `ComputerGrid` will be opened and will filter the computers of this user.

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
                <version>1.0.7.Final</version>
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