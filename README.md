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
        "Name": "String",
        "Age": "Int",
        "City": "String",
        "Email": "String"
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
Name,Age,City,Email
YeQozaDy,29,eounRu,YwJJhSNNTq@example.com
YCKZxzFx,48,ShshaK,hQHsaNMgMe@example.com
rBOEiXgw,51,efpiYC,NeTnCWFmgV@example.com
ahNSBbIE,57,PIQuqu,IKsyyRRqZF@example.com
Yittxgos,39,aPwaYG,OVAQaJEvLv@example.com
NfswvjXg,40,UawAKa,aRcZMcCoVP@example.com
OLvYmYts,41,euVZfg,JWoLrrcXCN@example.com
KmdcEgnM,29,yEITIj,atRavRyyoy@example.com
TZGcKNsM,50,kGyYox,ckyHfEomUj@example.com
tGHOSuqR,53,XeDmIE,ZfRZjoKtHX@example.com
wAnCRXel,43,rthHKK,AdPTxINYpX@example.com
huOoCjzw,65,xxEeIi,kuyltpBRNC@example.com
WcNPBUOv,38,ncFTdB,GnHjnGurAc@example.com

```

## Build and Run
You can either create and run the project with your own gradle version or with the gradlew wrapper which comes with this project. 
In both cases, please open a terminal in the root directory of the project. Then execute one of the following options:

- gradlew: Use cli and go into the root project folder, then execute: `./gradlew jar` or `./gradlew.bat jar` on win
- own gradle installation `gradle jar`

After the build process is complete, you can find the JAR executable file in the build/libs directory of the project. 
You can then use this JAR file to run the app.

`java -jar build/libs/datascoop-0.0.1-SNAPSHOT.jar [path/to/your/import_preset_file]`


