# DataScoop
Imports data from one or more csv files and inserts them into a target postgres table.
Postgres is currently supported, but other database technologies will be followed in the future.

## Import Preset File
CSV files can be specified as a datasource grouped by there structure. 
A mapping must be stored in a json configuration file for each structure.
This mapping applies to all files to be imported per datasource.
The configuration file is called import preset file.

An example for an import preset file would be:

```json
{
  "datasources": {
    "datasource-1": {
      "csv-files": [
        "/tmp/example1.csv",
        "/tmp/example2.csv"
      ],
      "csv-separator": ",",
      "csv-header": true,
      "csv-encoding": "UTF-8",
      "csv-mapping": {
        "Name": "text",
        "Age": "integer",
        "City": "text",
        "Email": "text",
        "Salary": "float",
        "Active": "boolean"
      }
    }
  },
  "database": {
    "url": "jdbc:postgresql://localhost:5432/datascoop",
    "username": "datascoop",
    "password": "datascoop",
    "table-name": "target_table_name"
  }
}
```
A corresponding CSV file would be (data was generated):

```csv
Name,Age,City,Email,Salary,Active
fnvpUxID,59,wbvdVd,sBSIMENdSG@example.com,3216.54,True
bClkrBnT,52,lDpIkT,HxeZJGxndY@example.com,2837.87,True
XSvpCdbm,26,ieApnz,JpFPSzVczJ@example.com,3109.29,True
mmZlkWvB,54,TvjQCK,HSBxUNTMOr@example.com,1802.94,True
CJNTOZtB,31,tGanpg,UGxhgbINuZ@example.com,3222.58,False
phWMCYSZ,18,StcWnh,UxLyCkNhSK@example.com,2656.52,False
iCeMWXct,56,saZhxa,XeUlIqhWpE@example.com,3024.28,True
vLKLEjZQ,48,RtyCDE,tnXLeZdlpp@example.com,2945.15,True
NvxqUUua,51,WAwOrX,TXufqTyLIq@example.com,3741.78,True
```
### Data Types
The following data types can be used in the import preset file:

| Data Type | Description                                                                                                                                    |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------|
| text      | This is the most universal data type. It can either be a simple text or a column that cannot be parsed or represented by the other data types. |
| integer   | A number without a decimal place                                                                                                               |
| float     | A number with a decimal place                                                                                                                  |
| boolean   | A bool value can either be True, False or empty                                                                                                |


## Build and Run
You can either create and run the project with your own gradle version or with the gradlew wrapper which comes with this project. 
In both cases, please open a terminal in the root directory of the project. Then execute one of the following options:

- gradlew: Use cli and go into the root project folder, then execute: `./gradlew jar` or `./gradlew.bat jar` on win
- own gradle installation `gradle jar`

After the build process is complete, you can find the JAR executable file in the build/libs directory of the project. 
You can then use this JAR file to run the app.

`java -jar build/libs/datascoop-0.0.1-SNAPSHOT.jar [path/to/your/import_preset_file]`


